package com.starrepublic.meetrix2.events

import android.R
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import com.starrepublic.meetrix2.MeetrixApp
import com.starrepublic.meetrix2.utils.addFragmentToActivity


/**
 * Created by richard on 2016-11-02.
 */
class EventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var eventsFragment: EventsFragment? = supportFragmentManager.findFragmentById(R
                .id.content) as EventsFragment?
        if (eventsFragment == null) {
            // Create the fragment
            eventsFragment = EventsFragment.create()

            addFragmentToActivity(eventsFragment, android.R.id.content);
        }



        DaggerEventsComponent.builder().eventsRepositoryComponent(MeetrixApp.eventsRepositoryComponent).eventsPresenterModule(EventsPresenterModule(eventsFragment)).build().inject(this);


        //val eventsViewModel = EventsViewModel(applicationContext, eventsPresenter):

        //DaggerEveComponent.builder().tasksRepositoryComponent((application as ToDoApplication).getTasksRepositoryComponent()).tasksPresenterModule(TasksPresenterModule(tasksFragment)).build().inject(this)

        // Create the presenter
        /*var eventsPresenter = EventsPresenter(Injection.provideEventsRepository(
                applicationContext), eventsFragment)

        val tasksViewModel = EventsViewModel(applicationContext, eventsPresenter)

        eventsFragment!!.setViewModel(tasksViewModel)

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
            mTasksPresenter.setFiltering(currentFiltering)
        }*/


    }

}