package com.nmp90.mvpvm.base

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseActivity : AppCompatActivity() {

    private val viewUiEventDisposables = CompositeDisposable()

    private val oneShotUiEvents = PublishSubject.create<UiEvent>()
    /**
     * @return all the UI Events generated from this Fragment.
     */
    val uiEvents: Observable<UiEvent>
        get() = oneShotUiEvents

    /**
     * This method should be overridden in Activities to declare all the view UiEvents that are available.
     * The UiEvents will be subscribed onStart() and disposed onStop()
     */
    open fun collectViewUiEventGenerators() = emptyArray<Observable<out UiEvent>>()

    private fun initAndRegisterUiEvents() {
        val generators = collectViewUiEventGenerators()
        registerViewUiEventGenerators(generators)
    }

    private fun registerViewUiEventGenerators(generators: Array<Observable<out UiEvent>>) {
        for (generator in generators) {
            val disposable = generator.subscribe { event -> manuallyEmitUiEvent(event) }
            viewUiEventDisposables.addAll(disposable)
        }
    }

    /**
     * Emits an UiEvent that is to be handled by a [ModelPresenter]. Such events can be subscribed through the
     * method [uiEvents].
     */
    protected fun manuallyEmitUiEvent(event: UiEvent) {
        oneShotUiEvents.onNext(event)
    }

    override fun onStart() {
        super.onStart()
        initAndRegisterUiEvents()
    }


    override fun onStop() {
        viewUiEventDisposables.clear()
        super.onStop()
    }

}
