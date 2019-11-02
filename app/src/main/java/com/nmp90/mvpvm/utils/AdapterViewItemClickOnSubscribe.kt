package com.nmp90.mvpvm.utils

import android.widget.AdapterView
import com.nmp90.mvpvm.events.AdapterViewItemClickEvent
import io.reactivex.ObservableEmitter
import io.reactivex.android.MainThreadDisposable.verifyMainThread

/**
 * OnSubscribe bridging [AdapterView]'s on item click callbacks to a [AdapterViewItemClickEvent] of a
 * [AdapterView.OnItemClickListener].
 */
class AdapterViewItemClickOnSubscribe(view: AdapterView<*>) :
    MainThreadOnSubscribe<AdapterView<*>, AdapterViewItemClickEvent, AdapterView.OnItemClickListener>(view) {

    override fun createListener(view: AdapterView<*>, subscriber: ObservableEmitter<AdapterViewItemClickEvent>):
            AdapterView.OnItemClickListener {

        return AdapterView.OnItemClickListener { _, adapterView, position, _ ->
            if (!subscriber.isDisposed) {
                verifyMainThread()
                subscriber.onNext(AdapterViewItemClickEvent(adapterView.id, position))
            }
        }
    }

    override fun addListener(adapterView: AdapterView<*>, listener: AdapterView.OnItemClickListener) {
        adapterView.onItemClickListener = listener
    }

    override fun removeListener(adapterView: AdapterView<*>, listener: AdapterView.OnItemClickListener) {
        adapterView.onItemClickListener = null
    }

    override fun emitOnSubscriptionCreated(
        view: AdapterView<*>,
        emitter: ObservableEmitter<AdapterViewItemClickEvent>
    ) {
        // emmit only when click happens, not on subscription.
    }
}


