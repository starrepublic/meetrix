package com.starrepublic.meetrix2.data

import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.admin.directory.model.CalendarResources
import com.google.api.services.admin.directory.model.User
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
class GoogleApiRepository @Inject constructor(val calendar:com.google.api.services.calendar.Calendar, val directory: Directory): DataSource {



    override fun getEvents(calendarId:String): Observable<List<Event>> {

        return Observable.create {
            val cal = Calendar.getInstance() // locale-specific
            cal.time = Date()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val minTime = DateTime(cal.time)
            val maxTime = DateTime(cal.time.time + 86400000-1)



            try {
                val events = calendar.events().list(calendarId).setMaxResults(10).setTimeMin(minTime).setTimeMax(maxTime).setOrderBy("startTime").setSingleEvents(true).execute()
                val items = events.items
                if(!it.isUnsubscribed) {
                    it.onNext(items)
                }
            }catch (e:Exception){
                if(!it.isUnsubscribed) {
                    it.onError(e);
                }

            }
        };

    }

    override fun deleteEvent(event: Event): Single<Unit> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEvent(calendarId:String, event: Event): Single<Event> {

        return Single.create {

            try{
                val result = calendar.events().insert(calendarId, event).execute()
                if(!it.isUnsubscribed) {
                    it.onSuccess(result)
                }
            }catch (e:Exception){
                if(!it.isUnsubscribed) {
                    it.onError(e);
                }
            }
        }
    }

    override fun getRooms(): Single<List<CalendarResource>> {

        return Single.create {
            try {
                val resources = directory.resources().calendars().list("my_customer").execute()
                val items = resources.items
                items.sortBy {
                    it.resourceName
                }
                if(!it.isUnsubscribed) {
                    it.onSuccess(resources.items)
                }
            }catch (e:Exception){
                if(!it.isUnsubscribed) {
                    it.onError(e);
                }
            }
        }
    }

    override fun getUsers(): Single<Map<String,User>> {
        return Single.create {


            try {
                val users = directory.users().list().setCustomer("my_customer").execute().users

                val map = users.associateBy({it.primaryEmail},{it})

                if(!it.isUnsubscribed) {
                    it.onSuccess(map)
                }
            }catch (e:Exception){
                if(!it.isUnsubscribed) {
                    it.onError(e);
                }
            }
        }
    }


}