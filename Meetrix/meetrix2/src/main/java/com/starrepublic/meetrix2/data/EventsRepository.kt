package com.starrepublic.meetrix2.data

import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import rx.Observable
import rx.Single
import rx.SingleSubscriber
import rx.Subscriber
import java.util.*
import javax.inject.Inject

/**
 * Created by richard on 2016-11-02.
 */
class EventsRepository @Inject constructor(val calendar:com.google.api.services.calendar.Calendar): EventsDataSource {



    override fun getEvents(): Observable<List<Event>> {

        return Observable.create { t ->
            val cal = Calendar.getInstance() // locale-specific
            cal.setTime(Date())
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val now = DateTime(cal.time)
            val eventStrings = ArrayList<String>()

            try {
                val events = calendar.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute()
                val items = events.getItems()
                t?.onNext(items)
            }catch (e:Exception){
                t?.onError(e);
            }
        };

    }

    override fun deleteEvent(event: Event): Single<Unit> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEvent(event: Event): Single<Unit> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRooms(): Single<List<Directory.Resources>> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUsers(): Single<List<Directory.Users>> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}