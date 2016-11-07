package com.starrepublic.meetrix2.events

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.MeetrixApp
import com.starrepublic.meetrix2.data.EventsRepository
import com.starrepublic.meetrix2.utils.Settings
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.internal.util.ExceptionsUtils
import rx.internal.util.SubscriptionList
import rx.lang.kotlin.subscribeWith
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by richard on 2016-11-02.
 */

const val REQUEST_ACCOUNT_PICKER = 1000
const val REQUEST_AUTHORIZATION = 1001
const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003

class EventsPresenter @Inject constructor(
        val eventsRepository: EventsRepository,
        val view: EventsContract.View,
        val cedentials: GoogleAccountCredential,
        val settings: Settings) : EventsContract.Presenter {




    private val getEventsObservable = Observable.interval(15,TimeUnit.SECONDS)
        .flatMap{ eventsRepository.getEvents() }
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext {
            view.showError(it)
            Observable.empty()
        }


    private val subscriptions:SubscriptionList = SubscriptionList()


    override fun stop() {
        subscriptions.unsubscribe();
    }


    init {

    }


    fun chooseAccount(){
        val accountName = settings.accountName

        if(view.hasPermission(Manifest.permission.GET_ACCOUNTS)){
            val accountName = settings.accountName
            if(accountName!=null){
                cedentials.setSelectedAccountName(accountName)
            }else{
                view.startActivityForResult(cedentials.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER)
            }
        }else{
            view.showError(SecurityException("permission"))
        }
    }


    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        view.setPresenter(this)
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode !== Activity.RESULT_OK) {
            } else {
                loadEvents()
            }
            REQUEST_ACCOUNT_PICKER -> if (resultCode === Activity.RESULT_OK && data != null &&
                    data.extras != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {

                    settings.accountName = accountName;
                    cedentials.setSelectedAccountName(accountName)
                    loadEvents();
                }
            }
            REQUEST_AUTHORIZATION -> if (resultCode === Activity.RESULT_OK) {
                loadEvents()
            }
        }

    }

    override fun loadEvents() {
        subscriptions.add(getEventsObservable.subscribe{
            view.showEvents(it)
        })
    }

    override fun addNewEvent() {

    }

    override fun error(exception: Throwable?) {

    }

    override fun start() {

    }
}
