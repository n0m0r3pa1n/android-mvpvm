package com.nmp90.mvpvm.utils

import android.view.View
import com.nmp90.mvpvm.events.ViewClickEvent
import io.reactivex.ObservableEmitter
import io.reactivex.android.MainThreadDisposable.verifyMainThread

/**
 * OnSubscribe bridging [View]'s click callbacks to a [ViewClickEvent] of a [View.OnClickListener].
 */
class ViewClickOnSubscribe(view: View) : MainThreadOnSubscribe<View, ViewClickEvent, View.OnClickListener>(view) {

    override fun createListener(view: View, subscriber: ObservableEmitter<ViewClickEvent>) = View.OnClickListener {
        if (!subscriber.isDisposed) {
            verifyMainThread()
            subscriber.onNext(ViewClickEvent(it.id))
        }
    }

    override fun addListener(view: View, listener: View.OnClickListener) {
        view.setOnClickListener(listener)
    }

    override fun removeListener(view: View, listener: View.OnClickListener) {
        view.setOnClickListener(null)
    }

    override fun emitOnSubscriptionCreated(view: View, emitter: ObservableEmitter<ViewClickEvent>) = Unit
}
