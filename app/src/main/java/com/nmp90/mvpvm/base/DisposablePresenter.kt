package com.nmp90.mvpvm.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * A presenter that listens to lifecycle and handles lifecycle for [createSubscriptions] and [dispose].
 *
 * Usage: as a last line of your component onCreate, call [register].
 *
 * Once [register] is used, [createSubscriptions] will be invoked automatically after your Component onCreate, and
 * [dispose] will be invoked automatically on your Component onDestroy.
 *
 * Do not invoke [createSubscriptions] or [dispose] on your implementations directly, they should be only used as
 * callbacks.
 */
interface DisposablePresenter : LifecycleObserver {

    /**
     * Implementations SHOULD subscribe to event sources from here.
     */
    fun createSubscriptions()

    /**
     * Implementations SHOULD dispose all the subscriptions upon this event.
     */
    fun dispose()
}

/**
 * MUST be called from your component onCreate.
 */
fun DisposablePresenter.register(lifecycle: Lifecycle) {

    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        throw IllegalStateException("MUST call register(lifecycle) from onCreate()")
    }

    lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            createSubscriptions()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            lifecycle.removeObserver(this)
            dispose()
        }
    })
}
