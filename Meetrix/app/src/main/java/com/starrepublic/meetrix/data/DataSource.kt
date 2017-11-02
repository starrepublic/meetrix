package com.starrepublic.meetrix.data

import com.google.api.services.admin.directory.model.CalendarResource
import com.google.api.services.admin.directory.model.User
import com.google.api.services.calendar.model.Event
import rx.Observable
import rx.Single

/**
 * Created by richard on 2016-11-02.
 */
interface DataSource {
    fun getEvents(calendarId: String): Observable<List<Event>>
    fun deleteEvent(event: Event): Single<Unit>
    fun saveEvent(calendarId: String, event: Event): Single<Event>
    fun getRooms(): Single<List<CalendarResource>>
    fun getUsers(): Single<Map<String, User>>
}
