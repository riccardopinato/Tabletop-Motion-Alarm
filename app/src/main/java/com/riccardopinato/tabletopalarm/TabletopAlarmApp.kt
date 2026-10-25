// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/TabletopAlarmApp.kt
package com.riccardopinato.tabletopalarm

import android.app.Application

class TabletopAlarmApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
