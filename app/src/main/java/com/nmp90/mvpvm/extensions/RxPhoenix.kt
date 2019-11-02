package com.nmp90.mvpvm.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.annotation.VisibleForTesting
import io.reactivex.Flowable
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.flowables.ConnectableFlowable
import io.reactivex.observables.ConnectableObservable

/**
 * Subscribes to a Flowable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Flowable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Flowable<T>.subscribe(lifecycle: Lifecycle, onNext: (T) -> Unit): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext
        )
    }

/**
 * Subscribes to a Flowable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Flowable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Flowable<T>.subscribe(lifecycle: Lifecycle, onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext,
            onError
        )
    }

/**
 * Subscribes to a Flowable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Flowable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Flowable<T>.subscribe(
    lifecycle: Lifecycle,
    onNext: (T) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext,
            onError,
            onComplete
        )
    }

/**
 * Subscribes to an Observable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Observable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Observable<T>.subscribe(lifecycle: Lifecycle, onNext: (T) -> Unit): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext
        )
    }

/**
 * Subscribes to an Observable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Observable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Observable<T>.subscribe(lifecycle: Lifecycle, onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext,
            onError
        )
    }

/**
 * Subscribes to an Observable which will only receive events between onStart and onStop. Any other event is
 * cached and not delivered to ensure that the view will not have troubles dealing with them.
 * When the view will be back available (Started), the cached events will delivered.
 * This Observable is automatically un-subscribed when the view goes to a destroyed state.
 */
fun <T> Observable<T>.subscribe(
    lifecycle: Lifecycle,
    onNext: (T) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
): Disposable =
    subscribeAndResubscribeAutomatically(lifecycle, this.publish()) {
        it.subscribe(
            onNext,
            onError,
            onComplete
        )
    }

private fun <T> subscribeAndResubscribeAutomatically(
    lifecycle: Lifecycle,
    connectableSource: ConnectableFlowable<T>,
    subscriptionFunction: (Flowable<T>) -> Disposable
): Disposable {
    var viewConnection = subscriptionFunction(connectableSource)
    val overallDisposable = connectableSource.connect()
    val undeliveredCache = ArrayList<Notification<T>>()
    var cachingDisposable: Disposable? = null
    lifecycle.addSimpleObserver(
        onStop = {
            viewConnection.dispose()
            cachingDisposable = connectableSource.materialize().subscribe { undeliveredCache.add(it) }
        },
        onStart = {
            viewConnection.dispose()
            viewConnection = if (undeliveredCache.firstOrNull { it.isOnComplete } != null) {
                // Completed while onStop, we just send the events until completion
                subscriptionFunction(Flowable.fromIterable(undeliveredCache).dematerialize())
            } else {
                // Not completed while onStop, we send all the events cached and keep listening
                subscriptionFunction(
                    connectableSource.startWith(Flowable.fromIterable(undeliveredCache).dematerialize())
                )
            }
            undeliveredCache.clear()
            cachingDisposable?.dispose()
        },
        onEnd = {
            overallDisposable.dispose()
            cachingDisposable?.dispose()
        }
    )
    return overallDisposable
}

private fun <T> subscribeAndResubscribeAutomatically(
    lifecycle: Lifecycle,
    connectableSource: ConnectableObservable<T>,
    subscriptionFunction: (Observable<T>) -> Disposable
): Disposable {
    var viewConnection = subscriptionFunction(connectableSource)
    val overallDisposable = connectableSource.connect()
    val undeliveredCache = ArrayList<Notification<T>>()
    var cachingDisposable: Disposable? = null
    lifecycle.addSimpleObserver(
        onStop = {
            viewConnection.dispose()
            cachingDisposable = connectableSource.materialize().subscribe { undeliveredCache.add(it) }
        },
        onStart = {
            viewConnection.dispose()
            viewConnection = if (undeliveredCache.firstOrNull { it.isOnComplete } != null) {
                // Completed while onStop, we just send the events until completion
                subscriptionFunction(Observable.fromIterable(undeliveredCache).dematerialize())
            } else {
                // Not completed while onStop, we send all the events cached and keep listening
                subscriptionFunction(
                    connectableSource.startWith(Observable.fromIterable(undeliveredCache).dematerialize())
                )
            }
            undeliveredCache.clear()
            cachingDisposable?.dispose()
        },
        onEnd = {
            overallDisposable.dispose()
            cachingDisposable?.dispose()
        }
    )
    return overallDisposable
}

private fun Lifecycle.addSimpleObserver(onStop: () -> Unit, onStart: () -> Unit, onEnd: () -> Unit): LifecycleObserver {
    val observer = SimpleLifecycleObserver(onStop, onStart, onEnd)
    this.addObserver(observer)
    return observer
}

@VisibleForTesting
internal class SimpleLifecycleObserver(
    private val onStop: () -> Unit,
    private val onStart: () -> Unit,
    private val onEnd: () -> Unit
) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifeStop() {
        onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifeStart() {
        onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifeEnd() {
        onEnd()
    }
}
