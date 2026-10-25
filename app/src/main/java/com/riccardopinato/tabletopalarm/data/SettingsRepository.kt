// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/data/SettingsRepository.kt
package com.riccardopinato.tabletopalarm.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.riccardopinato.tabletopalarm.model.AppSettings
import com.riccardopinato.tabletopalarm.model.GoogleProfile
import com.riccardopinato.tabletopalarm.model.Sensitivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val countdown = intPreferencesKey("countdown_seconds")
        val sensitivity = stringPreferencesKey("sensitivity")
        val sound = booleanPreferencesKey("sound_enabled")
        val vibration = booleanPreferencesKey("vibration_enabled")
        val flash = booleanPreferencesKey("flash_enabled")
        val biometric = booleanPreferencesKey("biometric_enabled")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val googleId = stringPreferencesKey("google_id")
        val googleName = stringPreferencesKey("google_name")
        val googleEmail = stringPreferencesKey("google_email")
        val googlePhoto = stringPreferencesKey("google_photo")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        val profileId = prefs[Keys.googleId]

        AppSettings(
            countdownSeconds = prefs[Keys.countdown] ?: 5,
            sensitivity = runCatching {
                Sensitivity.valueOf(
                    prefs[Keys.sensitivity] ?: Sensitivity.MEDIUM.name
                )
            }.getOrDefault(Sensitivity.MEDIUM),
            soundEnabled = prefs[Keys.sound] ?: true,
            vibrationEnabled = prefs[Keys.vibration] ?: true,
            flashEnabled = prefs[Keys.flash] ?: true,
            biometricEnabled = prefs[Keys.biometric] ?: false,
            onboardingCompleted = prefs[Keys.onboardingCompleted] ?: false,
            googleProfile = profileId?.let {
                GoogleProfile(
                    id = it,
                    displayName = prefs[Keys.googleName],
                    email = prefs[Keys.googleEmail],
                    photoUrl = prefs[Keys.googlePhoto]
                )
            }
        )
    }

    suspend fun setCountdown(seconds: Int) {
        require(seconds in setOf(3, 5, 10, 15))
        context.settingsDataStore.edit { it[Keys.countdown] = seconds }
    }

    suspend fun setSensitivity(value: Sensitivity) {
        context.settingsDataStore.edit { it[Keys.sensitivity] = value.name }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.sound] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.vibration] = enabled }
    }

    suspend fun setFlashEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.flash] = enabled }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.biometric] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.settingsDataStore.edit { it[Keys.onboardingCompleted] = completed }
    }

    suspend fun saveGoogleProfile(profile: GoogleProfile) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.googleId] = profile.id
            profile.displayName?.let { prefs[Keys.googleName] = it }
                ?: prefs.remove(Keys.googleName)
            profile.email?.let { prefs[Keys.googleEmail] = it }
                ?: prefs.remove(Keys.googleEmail)
            profile.photoUrl?.let { prefs[Keys.googlePhoto] = it }
                ?: prefs.remove(Keys.googlePhoto)
        }
    }

    suspend fun clearGoogleProfile() {
        context.settingsDataStore.edit { prefs ->
            prefs.remove(Keys.googleId)
            prefs.remove(Keys.googleName)
            prefs.remove(Keys.googleEmail)
            prefs.remove(Keys.googlePhoto)
        }
    }
}
