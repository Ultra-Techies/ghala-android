package com.ultratechies.ghala

import android.app.Activity
import android.app.Application
import com.ultratechies.ghala.utils.AppActivityLifecycleCb

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    val activityLifecycleCallbacks = AppActivityLifecycleCb()

    companion object {
        var application: App? = null

        fun currentActivity(): Activity? = application?.activityLifecycleCallbacks?.currentActivity
    }

    override fun onCreate() {
        super.onCreate()

        application = this
    }
}
