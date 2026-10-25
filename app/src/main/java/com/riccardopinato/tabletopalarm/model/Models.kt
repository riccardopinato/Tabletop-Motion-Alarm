// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/model/Models.kt
package com.riccardopinato.tabletopalarm.model

enum class Sensitivity {
    LOW,
    MEDIUM,
    HIGH;

    val label: String
        get() = when (this) {
            LOW -> "Bassa"
            MEDIUM -> "Media"
            HIGH -> "Alta"
        }
}

data class GoogleProfile(
    val id: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

data class AppSettings(
    val countdownSeconds: Int = 5,
    val sensitivity: Sensitivity = Sensitivity.MEDIUM,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val flashEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val googleProfile: GoogleProfile? = null
)

enum class AlarmEventType {
    ARMING_STARTED,
    ARMED,
    TRIGGERED,
    DISARMED,
    CANCELED,
    ERROR
}

data class AlarmEvent(
    val id: Long,
    val timestamp: Long,
    val type: AlarmEventType,
    val detail: String
)
