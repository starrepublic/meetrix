package com.starrepublic.meetrix.events

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.admin.directory.model.User
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttachment
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.starrepublic.meetrix.data.GoogleApiRepository
import com.starrepublic.meetrix.mvp.BaseFragment
import com.starrepublic.meetrix.mvp.BasePresenter
import com.starrepublic.meetrix.mvp.BaseViewModel
import com.starrepublic.meetrix.utils.*
import rx.Observable
import rx.Single
import rx.Subscription
import rx.functions.Action1
import rx.internal.util.SubscriptionList
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.onErrorReturnNull
import rx.lang.kotlin.switchOnNext
import rx.subjects.PublishSubject
import rx.subjects.Subject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by richard on 2016-11-08.
 */


class EventsPresenter @Inject constructor(val cedentials: GoogleAccountCredential,
                                          val settings: Settings,
                                          val googleApiRepository: GoogleApiRepository) : BasePresenter<EventsView>() {


    //messages
    val MESSAGE_USERS: Int = 1
    val MESSAGE_EVENTS: Int = 2
    val MESSAGE_CREATE: Int = 3

    //errors
    val ERROR_CREATE_EVENT: Int = 960
    val ERROR_USERS: Int = 970
    val ERROR_ROOMS: Int = 980
    val ERROR_EVENTS: Int = 990


    var room: CalendarResource?
        set(value) {
            settings.roomResourceId = value
            view?.setRoom(value!!)
        }
        get() = settings.roomResourceId

    var accountName: String?
        set(value) {
            settings.accountName = value
            cedentials.selectedAccountName = value
        }
        get() = settings.accountName

    var users: Map<String, User> = emptyMap()

    var adding: Boolean = false

    private val refreshEventsSubject = PublishSubject<Long>()


    private val getRoomsSingle = googleApiRepository.getRooms().androidAsync()
    private val getUsersSingle = googleApiRepository.getUsers().androidAsync()
    private var subscriptions: SubscriptionList = SubscriptionList()
    private var eventsSubscription: Subscription? = null
    private var loadingEvents: Boolean = false
    private var eventObservable = Observable.merge(Observable.interval(0,15,TimeUnit.SECONDS).filter {
                !loadingEvents
            }, refreshEventsSubject)
            .filter { !adding }
            .doOnNext {
                view?.showLoading(view?.string(MESSAGE_EVENTS))
            }
            .flatMap {
                loadingEvents = true
                googleApiRepository.getEvents(room!!.resourceEmail).retryWithDelay(3,3)
            }
            .flatMap {
                loadingEvents = false
                val events = it
                it.forEach {
                    if (users.contains(it.creator.email)) {
                        return@flatMap getUsersSingle.onErrorReturn {
                            emptyMap()
                        }.map {
                            users = it
                        }.toObservable().flatMap {
                            Observable.just(events)
                        }
                    }
                }
                Observable.just(it)
            }
            .flatMap {
                it.forEach {
                    val event = it
                    it.creator.displayName = users.get(it.creator.email)?.name?.fullName ?: event.creator.email
                }
                Observable.just(it)
            }
            .filter { !adding }
            .androidAsync()
            .doOnUnsubscribe {
                view?.hideLoading()
            }

    override fun onStart() {
        init()
    }

    fun init() {
        cedentials.selectedAccountName = accountName
        room?.let { view?.setRoom(it) }
        if (accountName == null) {
            chooseAccount()
        } else if (room == null) {
            view?.showSelectRoomDialog()
        } else if (users.isEmpty()) {
            loadUsers()
        } else {
            loadEvents()
        }
    }

    private fun loadUsers() {
        view?.showLoading(view?.string(MESSAGE_USERS))
        subscriptions.add(getUsersSingle.subscribe({
            view?.hideLoading()
            users = it
            loadEvents()
        }, {
            view?.hideLoading()
            view?.showError(it, ERROR_USERS)
        }))
    }

    override fun onStop() {
        subscriptions.unsubscribe()
        subscriptions = SubscriptionList()
    }

    fun chooseAccount() {
        if (view?.hasPermission(Manifest.permission.GET_ACCOUNTS)!!) {
            val accountName = settings.accountName
            if (accountName != null) {
                cedentials.selectedAccountName = accountName
                init()
            } else {
                view?.pickAccount(cedentials.newChooseAccountIntent())
            }
        } else {
            view?.showError(SecurityException("permission"))
        }
    }

    fun loadEvents() {

        eventsSubscription?.unsubscribe()
        if(eventsSubscription != null && eventsSubscription!!.isUnsubscribed){
            eventsSubscription = null
            subscriptions.remove(eventsSubscription)
        }


        if(subscriptions.isUnsubscribed){
            subscriptions = SubscriptionList()
        }

        loadingEvents = false




        eventsSubscription = eventObservable.subscribe({
            if (it != null) {
                view?.showEvents(it)
            }
            view?.hideLoading()
        }, {
            view?.showError(it, ERROR_EVENTS)
            view?.hideLoading()
            loadEvents()

        }, {

        })
        subscriptions.add(eventsSubscription)
    }


    fun loadRooms() {
        subscriptions.add(getRoomsSingle.subscribe({
            view?.showRooms(it)
        }, {
            view?.showError(it, ERROR_ROOMS)
        }))
    }

    fun createEvent(eventName: String?, from: Date?, to: Date?, accountName: String) {

        val event: Event = Event()
        event.start = EventDateTime().setDateTime(DateTime(from))
        event.end = EventDateTime().setDateTime(DateTime(to))
        event.creator = Event.Creator().setEmail(accountName).setDisplayName(accountName)
        event.location = room?.resourceName
        event.summary = eventName
        event.attendees = arrayListOf(EventAttendee().setEmail(room?.resourceEmail))

        view?.addEvent(event)

        adding = true

        view?.showLoading(view?.string(MESSAGE_CREATE))
        subscriptions.add(googleApiRepository.saveEvent(accountName, event).doOnSuccess {
            //refreshEventsSubject.onNext(0L)
        }.androidAsync().subscribe({
            adding = false
        }, {
            adding = false
            view?.hideLoading()
            view?.removeEvent(event)
            view?.showError(it, ERROR_CREATE_EVENT)
        }))
    }

    init {

    }
}
