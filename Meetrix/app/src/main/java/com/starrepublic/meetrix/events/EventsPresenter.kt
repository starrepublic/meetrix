package com.starrepublic.meetrix.events

import android.Manifest
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.admin.directory.model.User
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.starrepublic.meetrix.R
import com.starrepublic.meetrix.data.GoogleApiRepository
import com.starrepublic.meetrix.mvp.BasePresenter
import com.starrepublic.meetrix.utils.NetworkUtils
import com.starrepublic.meetrix.utils.Settings
import com.starrepublic.meetrix.utils.androidAsync
import com.starrepublic.meetrix.utils.retryWithDelay
import rx.Observable
import rx.Subscription
import rx.internal.util.SubscriptionList
import rx.lang.kotlin.PublishSubject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by richard on 2016-11-08.
 */
class EventsPresenter @Inject constructor(val cedentials: GoogleAccountCredential,
                                          val settings: Settings,
                                          val googleApiRepository: GoogleApiRepository,
                                          val networkUtils: NetworkUtils) : BasePresenter<EventsView>() {

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
    //var adding: Boolean = false
    private val refreshEventsSubject = PublishSubject<Long>()
    private val getRoomsSingle = googleApiRepository.getRooms().androidAsync()
    private val getUsersSingle = googleApiRepository.getUsers().androidAsync()
    private var subscriptions: SubscriptionList = SubscriptionList()
    private var eventsSubscription: Subscription? = null
    private var loadingEvents: Boolean = false
    private var addedEvent: Event? = null
    private var eventObservable = Observable.merge(Observable.interval(0, 15, TimeUnit.SECONDS).filter {
        !loadingEvents
    }, refreshEventsSubject.delay(1, TimeUnit.SECONDS))
            .doOnNext {
                view?.showLoading(view?.getString(R.string.loading_events))
            }
            .flatMap {
                loadingEvents = true
                googleApiRepository.getEvents(room!!.resourceEmail).retryWithDelay(3, 3)
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
                Observable.just(events)
            }
            .map {
                var containsEvent = false
                it.forEach {
                    it.creator.displayName = users[it.creator.email]?.name?.fullName ?: it.creator.email
                    if (it.id == addedEvent?.id) {

                        containsEvent = true
                    }
                }
                if (!containsEvent && addedEvent != null) {
                    return@map it.plus(addedEvent!!)
                }
                it
            }
            .onErrorResumeNext {
                val errorObservable = Observable.error<List<Event>>(it)

                if (it is IOException && networkUtils.isWifiEnabled) {
                    networkUtils.isWifiEnabled = false
                    return@onErrorResumeNext Observable.just(emptyList<Event>())
                            .delay(5, TimeUnit.SECONDS)
                            .flatMap {
                                networkUtils.isWifiEnabled = true
                                Observable.just(it)
                            }
                            .delay(10, TimeUnit.SECONDS)
                            .flatMap {
                                errorObservable
                            }
                }
                errorObservable
            }
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
        view?.showLoading(view?.getString(R.string.loading_users))
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
        if (eventsSubscription != null && eventsSubscription!!.isUnsubscribed) {
            eventsSubscription = null
            subscriptions.remove(eventsSubscription)
        }

        if (subscriptions.isUnsubscribed) {
            subscriptions = SubscriptionList()
        }

        loadingEvents = false

        eventsSubscription = eventObservable.subscribe({

            if (it != null) {
                view?.showEvents(it)
            }
            view?.hideLoading()
        }, {
            it.printStackTrace();
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
        addedEvent = event
        view?.addEvent(event)

        view?.showLoading(view?.getString(R.string.new_event))
        subscriptions.add(googleApiRepository.saveEvent(accountName, event).doOnSuccess {
            addedEvent = it
            view?.hideLoading()
            //refreshEventsSubject.onNext(0L)
        }.androidAsync().subscribe({
        }, {
            view?.hideLoading()
            view?.removeEvent(event)
            view?.showError(it, ERROR_CREATE_EVENT)
        }))
    }

    init {
    }
}

