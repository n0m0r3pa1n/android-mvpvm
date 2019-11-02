package com.nmp90.mvpvm.main

import com.nmp90.mvpvm.repo.NotesRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun provideMainModule() = module {
    single { NotesRepository() }

    scope(named<MainActivity>()) {
        scoped { (activity: MainActivity) ->
            MainPresenter(
                uiEvents = activity.uiEvents,
                uiScheduler = AndroidSchedulers.mainThread(),
                ioScheduler = Schedulers.io(),
                viewModel = get(),
                notesRepository = get()
            )
        }

        scoped { MainViewModel() }
    }
}
