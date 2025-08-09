package com.rust_book.example

import android.app.Application
import com.rust_book.example.di.appModule
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