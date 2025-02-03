package com.example.composetutorial
import android.app.Application
import android.content.Context

class MyApplicationClass : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: MyApplicationClass

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}