package com.example.weatherapiproject.di

import android.app.Application
import com.example.weatherapiproject.appModule
import com.example.weatherapiproject.networkModule
import com.example.weatherapiproject.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(viewModelModules, appModule, networkModule))
        }
    }

}