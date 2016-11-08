package com.starrepublic.meetrix2.events

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.starrepublic.meetrix2.data.EventsRepository
import com.starrepublic.meetrix2.mvp.BaseFragment2
import com.starrepublic.meetrix2.mvp.BasePresenter2
import com.starrepublic.meetrix2.mvp.BaseViewModel
import com.starrepublic.meetrix2.utils.Settings
import javax.inject.Inject

/**
 * Created by richard on 2016-11-08.
 */
class EventsPresenter2 @Inject constructor(val cedentials: GoogleAccountCredential,
                                            val settings: Settings,
                                            val eventsRepository: EventsRepository): BasePresenter2<EventsView2>() {



    override fun onStart() {


    }

    override fun onStop() {

    }
}