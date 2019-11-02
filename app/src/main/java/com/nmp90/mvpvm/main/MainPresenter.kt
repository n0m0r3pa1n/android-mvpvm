package com.nmp90.mvpvm.main

import android.util.Log
import com.nmp90.mvpvm.R
import com.nmp90.mvpvm.actions.GoToNoteDetailsAction
import com.nmp90.mvpvm.base.DisposablePresenter
import com.nmp90.mvpvm.base.PresenterAction
import com.nmp90.mvpvm.base.UiEvent
import com.nmp90.mvpvm.repo.NotesRepository
import com.nmp90.mvpvm.extensions.click
import com.nmp90.mvpvm.extensions.disposeWith
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class MainPresenter(
    private val uiEvents: Observable<UiEvent>,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val viewModel: MainViewModel,
    private val notesRepository: NotesRepository
) : DisposablePresenter {

    val presenterEvents: Observable<PresenterAction> get() = emitter.hide()

    private val disposables = CompositeDisposable()

    private val emitter = PublishSubject.create<PresenterAction>()

    override fun createSubscriptions() {
        uiEvents.click(R.id.fab)
            .subscribe {
                emitter.onNext(GoToNoteDetailsAction(viewModel.noteId.get()!!))
            }
            .disposeWith(disposables)

        notesRepository.getNote()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe(
                {
                    viewModel.noteId.set(it.id)
                    viewModel.noteText.set(it.text)
                },
                { Log.e("MainPresenter", "Error getting note", it) }
            )
            .disposeWith(disposables)
    }

    override fun dispose() {
        disposables.clear()
    }

}
