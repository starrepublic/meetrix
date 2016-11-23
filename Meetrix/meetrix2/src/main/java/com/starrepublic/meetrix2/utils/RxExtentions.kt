package com.starrepublic.meetrix2.utils

import rx.Observable
import rx.Single
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.DAYS


fun <T> Single<T>.androidAsync(): Single<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.androidAsync(): Observable<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}


class OperatorCountDownLatch<T>(val count: Int) : Observable.Operator<T, T> {


    override fun call(child: Subscriber<in T>?): Subscriber<in T> {
        return object : Subscriber<T>() {
            override fun onNext(t: T) {
                try {
                    signal.await(5, TimeUnit.DAYS)
                    child?.onNext(t)
                } catch (e: InterruptedException) {
                    child?.onError(e)
                }
            }

            override fun onCompleted() {
                child?.onCompleted();
            }

            override fun onError(e: Throwable?) {
                child?.onError(e)
            }

        }
    }


    private var signal: CountDownLatch;


    init {
        signal = CountDownLatch(count);
    }

    fun reset(){
        signal = CountDownLatch(count);
    }

    fun countDown() {
        signal.countDown();
    }

}
