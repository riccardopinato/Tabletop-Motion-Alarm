// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/data/SessionStateRepository.kt
package com.riccardopinato.tabletopalarm.data

import android.content.Context
import com.riccardopinato.tabletopalarm.service.AlarmPhase

data class PersistedAlarmSession(
    val phase: AlarmPhase,
    val updatedAt: Long
)

class SessionStateRepository(
    context: Context
) {
    private val prefs =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun savePhase(
        phase: AlarmPhase
    ) {
        if (
            phase == AlarmPhase.DISARMED ||
            phase == AlarmPhase.ERROR
        ) {
            clear()
            return
        }

        prefs.edit()
            .putString(
                KEY_PHASE,
                phase.name
            )
            .putLong(
                KEY_UPDATED_AT,
                System.currentTimeMillis()
            )
            .apply()
    }

    fun read():
        PersistedAlarmSession? {

        val phaseName =
            prefs.getString(
                KEY_PHASE,
                null
            ) ?: return null

        val phase =
            runCatching {
                AlarmPhase.valueOf(
                    phaseName
                )
            }.getOrNull()
                ?: return null

        if (
            phase == AlarmPhase.DISARMED ||
            phase == AlarmPhase.ERROR
        ) {
            return null
        }

        return PersistedAlarmSession(
            phase = phase,
            updatedAt =
                prefs.getLong(
                    KEY_UPDATED_AT,
                    0L
                )
        )
    }

    fun clear() {
        prefs.edit()
            .clear()
            .apply()
    }

    private companion object {
        const val PREFS_NAME =
            "alarm_runtime_session"

        const val KEY_PHASE =
            "phase"

        const val KEY_UPDATED_AT =
            "updated_at"
    }
}
