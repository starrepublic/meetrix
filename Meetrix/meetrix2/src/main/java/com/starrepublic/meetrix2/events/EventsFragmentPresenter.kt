/*
 * Copyright (c) 2016 by David Hardy. Licensed under the Apache License, Version 2.0.
 */

package nl.endran.skeleton.fragments


import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.data.EventsRepository
import com.starrepublic.meetrix2.mvp.BaseViewModel
import com.starrepublic.meetrix2.utils.Settings
import com.starrepublic.meetrix2.utils.androidAsync
import com.starrepublic.meetrix2.mvp.BaseFragmentPresenter
import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.internal.util.SubscriptionList
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

const val REQUEST_ACCOUNT_PICKER = 1000
const val REQUEST_AUTHORIZATION = 1001
const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003


class EventsFragmentPresenter @Inject constructor(val cedentials: GoogleAccountCredential,
                                                  val settings: Settings,
                                                  val eventsRepository: EventsRepository)
: BaseFragmentPresenter<EventsFragmentPresenter.ViewModel>() {

    interface ViewModel : BaseViewModel {
        fun showEvents(events: List<Event>?)
        fun showError(error: Throwable?)
        fun showRooms(it: List<Directory.Resources>?)
    }



    private val getEventsObservable = Observable.interval(15, TimeUnit.SECONDS)
            .flatMap { eventsRepository.getEvents() }
            .androidAsync()
            .onErrorResumeNext {
                viewModel?.showError(it)
                Observable.empty()
            }

    private val getRoomsSingle = eventsRepository.getRooms().androidAsync()


    private val subscriptions: SubscriptionList = SubscriptionList()

    override fun onStart() {

    }

    override fun onStop() {
        subscriptions.unsubscribe()
    }




    fun chooseAccount(){
        val accountName = settings.accountName



        if(viewModel?.hasPermission(Manifest.permission.GET_ACCOUNTS)!!){
            val accountName = settings.accountName
            if(accountName!=null){
                cedentials.setSelectedAccountName(accountName)
            }else{
                viewModel?.startActivityForResult(cedentials.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER,null)
            }
        }else{
            viewModel?.showError(SecurityException("permission"))
        }
    }

    fun loadEvents() {
        subscriptions.add(getEventsObservable.subscribe{
            viewModel?.showEvents(it)
        })
    }

    fun loadRooms(){
        subscriptions.add(getRoomsSingle.subscribe {

            viewModel?.showRooms(it)
        })
    }

    fun result(requestCode: Int, resultCode: Int, data: Intent?) {

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


}
