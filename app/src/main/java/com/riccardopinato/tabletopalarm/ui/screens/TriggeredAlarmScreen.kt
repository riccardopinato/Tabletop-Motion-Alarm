// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/TriggeredAlarmScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TriggeredAlarmScreen(
    onUnlock: () -> Unit
) {
    Surface(
        modifier =
            Modifier.fillMaxSize(),
        color =
            MaterialTheme
                .colorScheme
                .errorContainer
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(28.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {
            Icon(
                imageVector =
                    Icons.Default.Warning,
                contentDescription =
                    null,
                tint =
                    MaterialTheme
                        .colorScheme
                        .error
            )

            Spacer(
                Modifier.height(
                    20.dp
                )
            )

            Text(
                "MOVIMENTO RILEVATO",
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer
            )

            Spacer(
                Modifier.height(
                    8.dp
                )
            )

            Text(
                "Autenticati per fermare sirena, " +
                    "vibrazione e flash.",
                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer
            )

            Spacer(
                Modifier.height(
                    28.dp
                )
            )

            Button(
                modifier =
                    Modifier.fillMaxWidth(),
                onClick =
                    onUnlock
            ) {
                Text(
                    "SBLOCCA E DISATTIVA"
                )
            }
        }
    }
}
