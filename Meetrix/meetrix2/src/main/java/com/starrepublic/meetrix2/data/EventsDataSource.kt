package com.starrepublic.meetrix2.data

import android.provider.ContactsContract
import com.google.api.services.admin.directory.Directory
import com.google.api.services.calendar.model.Event
import rx.Observable
import rx.Single


/**
 * Created by richard on 2016-11-02.
 */

interface EventsDataSource {


    fun getEvents(): Observable<List<Event>>

    fun deleteEvent(event:Event):Single<Unit>

    fun saveEvent(event:Event):Single<Unit>

    fun getRooms():Single<List<Directory.Resources>>

    fun getUsers():Single<List<Directory.Users>>


}