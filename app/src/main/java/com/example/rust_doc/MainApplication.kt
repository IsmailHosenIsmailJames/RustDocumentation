package com.example.rust_doc

import android.app.Application
import com.example.rust_doc.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule) // We'll create this soon
        }
    }
}