package com.nmp90.mvpvm.extensions

import android.view.View
import android.widget.AdapterView
import com.nmp90.mvpvm.events.AdapterViewItemClickEvent
import com.nmp90.mvpvm.events.ViewClickEvent
import com.nmp90.mvpvm.utils.AdapterViewItemClickOnSubscribe
import com.nmp90.mvpvm.utils.ViewClickOnSubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

const val VIEW_CLICK_THROTTLE_DELAY_MS = 500L

fun View.click(): Observable<ViewClickEvent> = clickRaw().throttleFirst(
    VIEW_CLICK_THROTTLE_DELAY_MS,
    TimeUnit.MILLISECONDS,
    AndroidSchedulers.mainThread()
)

fun View.clickRaw(): Observable<ViewClickEvent> = Observable.create(
    ViewClickOnSubscribe(
        this
    )
)

fun AdapterView<*>.itemClickRaw(): Observable<AdapterViewItemClickEvent> =
    Observable.create(AdapterViewItemClickOnSubscribe(this))

fun AdapterView<*>.itemClick(): Observable<AdapterViewItemClickEvent> = itemClickRaw().throttleFirst(
    VIEW_CLICK_THROTTLE_DELAY_MS,
    TimeUnit.MILLISECONDS,
    AndroidSchedulers.mainThread()
)
