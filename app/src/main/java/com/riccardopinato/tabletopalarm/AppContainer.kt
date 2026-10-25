// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/AppContainer.kt
package com.riccardopinato.tabletopalarm

import android.content.Context
import com.riccardopinato.tabletopalarm.data.EventRepository
import com.riccardopinato.tabletopalarm.data.SecurityRepository
import com.riccardopinato.tabletopalarm.data.SettingsRepository
import com.riccardopinato.tabletopalarm.data.SessionStateRepository
import com.riccardopinato.tabletopalarm.hardware.BatteryDiagnostics
import com.riccardopinato.tabletopalarm.hardware.HardwareDiagnostics

class AppContainer(context: Context) {
    val settingsRepository =
        SettingsRepository(context)

    val securityRepository =
        SecurityRepository(context)

    val eventRepository =
        EventRepository(context)

    val sessionStateRepository = SessionStateRepository(context)

    val hardwareDiagnostics =
        HardwareDiagnostics(context)

    val batteryDiagnostics = BatteryDiagnostics(context)
}
