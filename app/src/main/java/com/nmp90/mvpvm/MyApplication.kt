package com.nmp90.mvpvm

import android.app.Application
import com.nmp90.mvpvm.main.MainActivity
import com.nmp90.mvpvm.main.provideMainModule
import com.nmp90.mvpvm.repo.NotesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(provideMainModule())
        }
    }
}
