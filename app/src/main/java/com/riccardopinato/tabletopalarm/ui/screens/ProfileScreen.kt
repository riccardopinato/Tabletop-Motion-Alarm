// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/ProfileScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riccardopinato.tabletopalarm.BuildConfig
import com.riccardopinato.tabletopalarm.auth.BiometricAuthManager
import com.riccardopinato.tabletopalarm.auth.GoogleAccountManager
import com.riccardopinato.tabletopalarm.data.SecurityRepository
import com.riccardopinato.tabletopalarm.data.SettingsRepository
import com.riccardopinato.tabletopalarm.hardware.HardwareDiagnostics
import com.riccardopinato.tabletopalarm.hardware.HardwareSnapshot
import com.riccardopinato.tabletopalarm.model.AppSettings
import com.riccardopinato.tabletopalarm.ui.components.SetPinDialog
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    settingsRepository:
        SettingsRepository,
    securityRepository:
        SecurityRepository,
    googleAccountManager:
        GoogleAccountManager,
    biometricAuthManager:
        BiometricAuthManager,
    batteryDiagnostics:
        com.riccardopinato.tabletopalarm.hardware.BatteryDiagnostics,
    onOpenAboutPrivacy: () -> Unit,
    diagnostics:
        HardwareDiagnostics,
    hardware:
        HardwareSnapshot,
    alarmRunning:
        Boolean
) {
    val scope =
        rememberCoroutineScope()

    val settings by
        settingsRepository
            .settings
            .collectAsStateWithLifecycle(
                initialValue =
                    AppSettings()
            )

    val hasPin by
        securityRepository
            .hasPin
            .collectAsStateWithLifecycle(
                initialValue = false
            )

    var showSetPin by remember {
        mutableStateOf(false)
    }

    var statusMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }

    val flashPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestPermission()
        ) {
            granted ->

            if (granted) {
                scope.launch {
                    statusMessage =
                        if (
                            diagnostics
                                .testFlash()
                        ) {
                            "Flash OK"
                        } else {
                            "Test flash non riuscito."
                        }
                }
            } else {
                statusMessage =
                    "Permesso fotocamera non concesso."
            }
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(
                16.dp
            )
    ) {
        Text(
            "Profilo",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        Card {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                Text(
                    "Account Google",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                val profile =
                    settings
                        .googleProfile

                if (profile == null) {
                    Text(
                        "Accedi con Google per " +
                            "associare il tuo profilo all'app."
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                googleAccountManager
                                    .signIn()
                                    .onSuccess {
                                        settingsRepository
                                            .saveGoogleProfile(
                                                it
                                            )

                                        statusMessage =
                                            "Accesso Google completato."
                                    }
                                    .onFailure {
                                        statusMessage =
                                            it.message
                                                ?: "Accesso Google non riuscito."
                                    }
                            }
                        }
                    ) {
                        Text(
                            "Accedi con Google"
                        )
                    }
                } else {
                    Text(
                        profile.displayName
                            ?: "Account Google"
                    )

                    profile.email?.let {
                        Text(it)
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                googleAccountManager
                                    .signOut()

                                settingsRepository
                                    .clearGoogleProfile()

                                statusMessage =
                                    "Account scollegato."
                            }
                        }
                    ) {
                        Text(
                            "Esci dall'account"
                        )
                    }
                }
            }
        }

        Card {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                Text(
                    "Sicurezza",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Text(
                    if (hasPin) {
                        "PIN configurato."
                    } else {
                        "PIN non configurato."
                    }
                )

                OutlinedButton(
                    enabled =
                        !alarmRunning,
                    onClick = {
                        showSetPin =
                            true
                    }
                ) {
                    Text(
                        if (hasPin) {
                            "Cambia PIN"
                        } else {
                            "Imposta PIN"
                        }
                    )
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                    ) {
                        Text(
                            "Biometria"
                        )

                        Text(
                            "Usa biometria forte " +
                                "per disattivare l'allarme.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Switch(
                        checked =
                            settings
                                .biometricEnabled,
                        enabled =
                            hasPin &&
                            biometricAuthManager
                                .isAvailable() &&
                            !alarmRunning,
                        onCheckedChange = {
                            scope.launch {
                                settingsRepository
                                    .setBiometricEnabled(
                                        it
                                    )
                            }
                        }
                    )
                }

                if (
                    !biometricAuthManager
                        .isAvailable()
                ) {
                    Text(
                        "Biometria forte non disponibile " +
                            "o non configurata sul dispositivo.",
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }
        }

        Card {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                Text(
                    "Diagnostica hardware",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Text(
                    "Accelerometro: " +
                        if (
                            hardware
                                .hasAccelerometer
                        ) {
                            "OK"
                        } else {
                            "NO"
                        }
                )

                Text(
                    "Giroscopio: " +
                        if (
                            hardware
                                .hasGyroscope
                        ) {
                            "OK"
                        } else {
                            "non presente"
                        }
                )

                Text(
                    "Flash: " +
                        if (
                            hardware
                                .hasFlash
                        ) {
                            "OK"
                        } else {
                            "non presente"
                        }
                )

                Text(
                    "Vibrazione: " +
                        if (
                            hardware
                                .hasVibrator
                        ) {
                            "OK"
                        } else {
                            "non presente"
                        }
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {
                    OutlinedButton(
                        enabled =
                            !alarmRunning,
                        onClick = {
                            diagnostics
                                .testSound()

                            statusMessage =
                                "Test audio avviato."
                        }
                    ) {
                        Text("Audio")
                    }

                    OutlinedButton(
                        enabled =
                            !alarmRunning &&
                            hardware
                                .hasVibrator,
                        onClick = {
                            statusMessage =
                                if (
                                    diagnostics
                                        .testVibration()
                                ) {
                                    "Vibrazione OK"
                                } else {
                                    "Vibrazione non disponibile."
                                }
                        }
                    ) {
                        Text("Vibra")
                    }

                    OutlinedButton(
                        enabled =
                            !alarmRunning &&
                            hardware
                                .hasFlash,
                        onClick = {
                            flashPermissionLauncher
                                .launch(
                                    Manifest
                                        .permission
                                        .CAMERA
                                )
                        }
                    ) {
                        Text("Flash")
                    }
                }
            }
        }

        Card {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Affidabilità in background", style = MaterialTheme.typography.titleMedium)
                val unrestricted = batteryDiagnostics.isIgnoringBatteryOptimizations()
                Text(
                    if (unrestricted) "Ottimizzazioni batteria escluse per questa app."
                    else "Android o il produttore del telefono potrebbe limitare le app in background in condizioni estreme."
                )
                Text(
                    "La protezione usa un Foreground Service visibile. Non è obbligatorio disattivare le ottimizzazioni batteria, ma puoi controllarle se noti interruzioni.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    enabled = !alarmRunning,
                    onClick = {
                        val opened = batteryDiagnostics.openBatteryOptimizationSettings()
                        statusMessage = if (opened) "Impostazioni batteria aperte." else "Impossibile aprire le impostazioni batteria su questo dispositivo."
                    }
                ) { Text("Controlla impostazioni batteria") }
            }
        }

        Card {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Informazioni", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Privacy, uso dei permessi, backup e funzionamento del monitoraggio in background.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = onOpenAboutPrivacy) { Text("Info e privacy") }
            }
        }

        Card {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(
                        6.dp
                    )
            ) {
                Text(
                    "Backup",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Text(
                    "Le impostazioni non sensibili " +
                        "sono compatibili con Android " +
                        "Auto Backup e trasferimento dispositivo."
                )

                Text(
                    "PIN, dati di sicurezza e storico " +
                        "eventi sono esclusi dal backup.",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }

        statusMessage?.let {
            message ->

            Card {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween
                ) {
                    Text(
                        message,
                        modifier =
                            Modifier
                                .weight(1f)
                    )

                    TextButton(
                        onClick = {
                            statusMessage =
                                null
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }

        Text(
            "Versione " +
                BuildConfig.VERSION_NAME,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }

    if (showSetPin) {
        SetPinDialog(
            onDismiss = {
                showSetPin = false
            },
            onSave = {
                pin ->

                scope.launch {
                    securityRepository
                        .setPin(pin)

                    showSetPin =
                        false

                    statusMessage =
                        "PIN aggiornato."
                }
            }
        )
    }
}
