// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/AlarmScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riccardopinato.tabletopalarm.data.SecurityRepository
import com.riccardopinato.tabletopalarm.data.SettingsRepository
import com.riccardopinato.tabletopalarm.hardware.HardwareSnapshot
import com.riccardopinato.tabletopalarm.model.AppSettings
import com.riccardopinato.tabletopalarm.model.Sensitivity
import com.riccardopinato.tabletopalarm.service.AlarmForegroundService
import com.riccardopinato.tabletopalarm.service.AlarmPhase
import com.riccardopinato.tabletopalarm.service.AlarmRuntimeState
import com.riccardopinato.tabletopalarm.ui.components.AlarmStatusCard
import com.riccardopinato.tabletopalarm.ui.components.PermissionInfoDialog
import com.riccardopinato.tabletopalarm.ui.components.SetPinDialog
import com.riccardopinato.tabletopalarm.ui.components.SettingSwitchRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmScreen(
    settingsRepository:
        SettingsRepository,
    securityRepository:
        SecurityRepository,
    hardware:
        HardwareSnapshot,
    runtime:
        AlarmRuntimeState,
    onRequestDisarm:
        () -> Unit
) {
    val context =
        LocalContext.current

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

    var showPermissionInfo by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestMultiplePermissions()
        ) {
            AlarmForegroundService
                .arm(context)
        }

    fun missingPermissions(): List<String> = buildList {
        if (settings.flashEnabled && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun requestArm() {
        if (!hasPin) { showSetPin = true; return }
        val missing = missingPermissions()
        if (missing.isEmpty()) AlarmForegroundService.arm(context) else showPermissionInfo = true
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
        Spacer(
            Modifier.height(4.dp)
        )

        Text(
            "Tabletop Motion Alarm",
            style =
                MaterialTheme
                    .typography
                    .headlineSmall
        )

        AlarmStatusCard(
            runtime = runtime,
            countdownTotal = settings.countdownSeconds,
            hardware = hardware
        )

        if (!hasPin) {
            Card(
                colors =
                    CardDefaults
                        .cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .errorContainer
                        )
            ) {
                Column(
                    Modifier.padding(
                        16.dp
                    )
                ) {
                    Text(
                        "Imposta prima un PIN",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Text(
                        "Il PIN impedisce che chi muove " +
                            "il telefono possa spegnere " +
                            "subito l'allarme."
                    )

                    Spacer(
                        Modifier.height(
                            12.dp
                        )
                    )

                    OutlinedButton(
                        onClick = {
                            showSetPin = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription =
                                null
                        )

                        Text(
                            "  Imposta PIN"
                        )
                    }
                }
            }
        }

        Button(
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                hardware
                    .hasAccelerometer,
            onClick = {
                when (runtime.phase) {
                    AlarmPhase.DISARMED ->
                        requestArm()

                    AlarmPhase.ARMING,
                    AlarmPhase.CALIBRATING,
                    AlarmPhase.ERROR ->
                        AlarmForegroundService
                            .disarm(context)

                    AlarmPhase.ARMED,
                    AlarmPhase.TRIGGERED ->
                        onRequestDisarm()
                }
            }
        ) {
            Text(
                when (runtime.phase) {
                    AlarmPhase.DISARMED ->
                        "ATTIVA ALLARME"

                    AlarmPhase.ARMING ->
                        "ANNULLA " +
                            "(${runtime.countdownSeconds}s)"

                    AlarmPhase.CALIBRATING ->
                        "ANNULLA CALIBRAZIONE"

                    AlarmPhase.ARMED ->
                        "DISATTIVA"

                    AlarmPhase.TRIGGERED ->
                        "SBLOCCA E DISATTIVA"

                    AlarmPhase.ERROR ->
                        "CHIUDI"
                }
            )
        }

        Text(
            "Countdown",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        FlowRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {
            listOf(
                3,
                5,
                10,
                15
            ).forEach {
                seconds ->

                FilterChip(
                    selected =
                        settings
                            .countdownSeconds ==
                        seconds,
                    enabled =
                        runtime.phase ==
                        AlarmPhase.DISARMED,
                    onClick = {
                        scope.launch {
                            settingsRepository
                                .setCountdown(
                                    seconds
                                )
                        }
                    },
                    label = {
                        Text("${seconds}s")
                    }
                )
            }
        }

        Text(
            "Sensibilità",
            style =
                MaterialTheme
                    .typography
                    .titleMedium
        )

        FlowRow(
            horizontalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {
            Sensitivity.entries
                .forEach {
                    sensitivity ->

                    FilterChip(
                        selected =
                            settings
                                .sensitivity ==
                            sensitivity,
                        enabled =
                            runtime.phase ==
                            AlarmPhase
                                .DISARMED,
                        onClick = {
                            scope.launch {
                                settingsRepository
                                    .setSensitivity(
                                        sensitivity
                                    )
                            }
                        },
                        label = {
                            Text(
                                sensitivity
                                    .label
                            )
                        }
                    )
                }
        }

        Card {
            Column(
                Modifier.padding(
                    16.dp
                )
            ) {
                SettingSwitchRow(
                    title = "Sirena",
                    subtitle =
                        "Tono d'allarme generato " +
                        "direttamente dall'app.",
                    checked =
                        settings
                            .soundEnabled,
                    enabled =
                        runtime.phase ==
                        AlarmPhase.DISARMED,
                    onCheckedChange = {
                        scope.launch {
                            settingsRepository
                                .setSoundEnabled(
                                    it
                                )
                        }
                    }
                )

                SettingSwitchRow(
                    title = "Vibrazione",
                    subtitle =
                        "Pattern continuo quando " +
                        "viene rilevato movimento.",
                    checked =
                        settings
                            .vibrationEnabled,
                    enabled =
                        runtime.phase ==
                            AlarmPhase.DISARMED &&
                            hardware
                                .hasVibrator,
                    onCheckedChange = {
                        scope.launch {
                            settingsRepository
                                .setVibrationEnabled(
                                    it
                                )
                        }
                    }
                )

                SettingSwitchRow(
                    title = "Flash LED",
                    subtitle =
                        if (
                            hardware.hasFlash
                        ) {
                            "Lampeggio rapido durante l'allarme."
                        } else {
                            "Flash non disponibile su questo dispositivo."
                        },
                    checked =
                        settings.flashEnabled &&
                            hardware.hasFlash,
                    enabled =
                        runtime.phase ==
                            AlarmPhase.DISARMED &&
                            hardware.hasFlash,
                    onCheckedChange = {
                        scope.launch {
                            settingsRepository
                                .setFlashEnabled(
                                    it
                                )
                        }
                    }
                )
            }
        }


        Spacer(
            Modifier.height(80.dp)
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
                }
            }
        )
    }

    if (showPermissionInfo) {
        val missing = missingPermissions()
        PermissionInfoDialog(
            needsCamera = missing.contains(Manifest.permission.CAMERA),
            needsNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && missing.contains(Manifest.permission.POST_NOTIFICATIONS),
            onContinue = { showPermissionInfo = false; permissionLauncher.launch(missing.toTypedArray()) },
            onDismiss = { showPermissionInfo = false }
        )
    }

}
