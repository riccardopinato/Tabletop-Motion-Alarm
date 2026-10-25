package com.riccardopinato.tabletopalarm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.riccardopinato.tabletopalarm.AppContainer
import com.riccardopinato.tabletopalarm.auth.BiometricAuthManager
import com.riccardopinato.tabletopalarm.auth.GoogleAccountManager
import com.riccardopinato.tabletopalarm.model.AppSettings
import com.riccardopinato.tabletopalarm.service.AlarmForegroundService
import com.riccardopinato.tabletopalarm.service.AlarmPhase
import com.riccardopinato.tabletopalarm.service.AlarmStateStore
import com.riccardopinato.tabletopalarm.ui.components.UnlockDialog
import com.riccardopinato.tabletopalarm.ui.screens.AboutPrivacyScreen
import com.riccardopinato.tabletopalarm.ui.screens.AlarmScreen
import com.riccardopinato.tabletopalarm.ui.screens.EventsScreen
import com.riccardopinato.tabletopalarm.ui.screens.OnboardingScreen
import com.riccardopinato.tabletopalarm.ui.screens.ProfileScreen
import com.riccardopinato.tabletopalarm.ui.screens.TriggeredAlarmScreen
import com.riccardopinato.tabletopalarm.ui.theme.OmniAccent
import kotlinx.coroutines.launch

private data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun TabletopAlarmRoot(
    container: AppContainer,
    googleAccountManager: GoogleAccountManager,
    biometricAuthManager: BiometricAuthManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val runtime by
        AlarmStateStore.state.collectAsStateWithLifecycle()

    val settings by
        container.settingsRepository.settings.collectAsStateWithLifecycle(
            initialValue = AppSettings()
        )

    val hardware =
        remember {
            container.hardwareDiagnostics.snapshot()
        }

    var showUnlock by remember {
        mutableStateOf(false)
    }

    var unlockError by remember {
        mutableStateOf<String?>(null)
    }

    fun completeDisarm() {
        showUnlock = false
        unlockError = null
        AlarmForegroundService.disarm(context)
    }

    if (!settings.onboardingCompleted) {
        OnboardingScreen(
            onCompleted = {
                scope.launch {
                    container.settingsRepository.setOnboardingCompleted(true)
                }
            }
        )
        return
    }

    if (runtime.phase == AlarmPhase.TRIGGERED) {
        TriggeredAlarmScreen(
            onUnlock = {
                showUnlock = true
                unlockError = null
            }
        )
    } else {
        val destinations =
            listOf(
                BottomDestination(
                    route = "alarm",
                    label = "Allarme",
                    icon = Icons.Default.Notifications
                ),
                BottomDestination(
                    route = "events",
                    label = "Eventi",
                    icon = Icons.Default.History
                ),
                BottomDestination(
                    route = "profile",
                    label = "Profilo",
                    icon = Icons.Default.Person
                )
            )

        val backStackEntry by
            navController.currentBackStackEntryAsState()

        val currentDestination =
            backStackEntry?.destination

        val showBottomBar =
            destinations.any { item ->
                currentDestination.isOn(item.route)
            }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    UtilityBottomBar(
                        destinations = destinations,
                        currentDestination = currentDestination,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(
                                    navController.graph.startDestinationId
                                ) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "alarm",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("alarm") {
                    AlarmScreen(
                        settingsRepository = container.settingsRepository,
                        securityRepository = container.securityRepository,
                        hardware = hardware,
                        runtime = runtime,
                        onRequestDisarm = {
                            showUnlock = true
                            unlockError = null
                        }
                    )
                }

                composable("events") {
                    EventsScreen(
                        eventRepository = container.eventRepository
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        settingsRepository = container.settingsRepository,
                        securityRepository = container.securityRepository,
                        googleAccountManager = googleAccountManager,
                        biometricAuthManager = biometricAuthManager,
                        batteryDiagnostics = container.batteryDiagnostics,
                        onOpenAboutPrivacy = {
                            navController.navigate("about_privacy")
                        },
                        diagnostics = container.hardwareDiagnostics,
                        hardware = hardware,
                        alarmRunning =
                            runtime.phase != AlarmPhase.DISARMED
                    )
                }

                composable("about_privacy") {
                    AboutPrivacyScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    if (showUnlock) {
        UnlockDialog(
            biometricAvailable = biometricAuthManager.isAvailable(),
            biometricEnabled = settings.biometricEnabled,
            error = unlockError,
            onDismiss = {
                showUnlock = false
                unlockError = null
            },
            onPinSubmit = { pin ->
                scope.launch {
                    if (container.securityRepository.verifyPin(pin)) {
                        completeDisarm()
                    } else {
                        unlockError = "PIN non corretto."
                    }
                }
            },
            onBiometric = {
                biometricAuthManager.authenticate { success ->
                    if (success) {
                        completeDisarm()
                    } else {
                        unlockError =
                            "Autenticazione biometrica non riuscita."
                    }
                }
            }
        )
    }
}

@Composable
private fun UtilityBottomBar(
    destinations: List<BottomDestination>,
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            destinations.forEach { item ->
                val selected = currentDestination.isOn(item.route)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            if (!selected) {
                                onNavigate(item.route)
                            }
                        }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint =
                            if (selected) {
                                OmniAccent
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        modifier = Modifier.size(22.dp)
                    )

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight =
                            if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                        color =
                            if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                    )

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    OmniAccent
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                    )
                }
            }
        }
    }
}

private fun NavDestination?.isOn(
    route: String
): Boolean =
    this
        ?.hierarchy
        ?.any { destination ->
            destination.route == route
        } == true
