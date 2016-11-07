package com.starrepublic.meetrix2.events


import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.api.services.calendar.model.Event
import com.starrepublic.meetrix2.BaseFragment
import com.starrepublic.meetrix2.MeetrixApp
import javax.inject.Inject

/**
 * Created by richard on 2016-11-02.
 */
class EventsFragment : Fragment(), EventsContract.View {

    override fun hasPermission(permission: String): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startActivityForResult(newChooseAccountIntent: Intent?, requestCode: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun showError(exception: Throwable?) {

    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    @Inject lateinit var eventsViewModel: EventsViewModel


    private lateinit var presenter: EventsContract.Presenter


    override fun showEvents(events: List<Event>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.result(requestCode,resultCode, data)
    }









    override fun setPresenter(presenter: EventsContract.Presenter) {
        this.presenter = presenter;
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {




        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    companion object Factory {
        fun create(): EventsFragment = EventsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
    }
}
