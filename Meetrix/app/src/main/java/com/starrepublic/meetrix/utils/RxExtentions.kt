package com.starrepublic.meetrix.utils

import rx.Observable
import rx.Single
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.api.services.calendar.model.Event
import rx.functions.Action1
import rx.functions.Func1
import rx.internal.operators.OperatorFilter
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.DAYS
import android.R.attr.delay
import javax.xml.datatype.DatatypeConstants.SECONDS
import rx.functions.Func2
import javax.xml.datatype.DatatypeConstants.SECONDS


fun <T> Single<T>.androidAsync(): Single<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.androidAsync(): Observable<T> {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.onErrorSuppress(action: (e: Throwable?) -> Unit): Observable<T> {
    return lift(OperatorSuppressError(action))
}

fun <T> Observable<T>.retryWithDelay(count: Int, power: Long): Observable<T> {

    return retryWhen {
        it.zipWith(Observable.range(1, count).materialize()) { n, i -> Pair(n, i) }
                .flatMap {
                    if (it.second.isOnCompleted) Observable.error(it.first) else Observable.timer(Math.pow(power.toDouble(), it.second.value.toDouble()).toLong(), TimeUnit.SECONDS)
                }
    }

}


class OperatorSuppressError<T, R>(val onErrorAction: (Throwable?) -> Unit) : Observable.Operator<T, R> {
    override fun call(t: Subscriber<in T>?): Subscriber<in R> {

        return object : Subscriber<R>(t) {
            override fun onCompleted() {
                t?.onCompleted()
            }

            override fun onError(e: Throwable?) {
                onErrorAction.invoke(e)
            }

            @Suppress("UNCHECKED_CAST")
            override fun onNext(t0: R) {
                t?.onNext(t0 as T)
            }
        }

    }

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

    fun reset() {
        signal = CountDownLatch(count);
    }

    fun countDown() {
        signal.countDown();
    }

}
