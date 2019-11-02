package com.nmp90.mvpvm.extensions

import androidx.annotation.IdRes
import com.nmp90.mvpvm.base.UiEvent
import com.nmp90.mvpvm.events.ClickEvent
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Observable<UiEvent>.click(@IdRes viewId: Int): Observable<ClickEvent> = this
    .ofType(ClickEvent::class.java)
    .filter { it.id == viewId }

fun Disposable.disposeWith(disposables: CompositeDisposable): Disposable {
    disposables.add(this)
    return this
}
