package com.starrepublic.meetrix2.utils

import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

fun  <T> Single<T>.androidAsync(): Single<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

fun  <T> Observable<T>.androidAsync(): Observable<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}
