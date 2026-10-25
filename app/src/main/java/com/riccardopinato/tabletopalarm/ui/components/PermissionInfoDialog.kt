// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/components/PermissionInfoDialog.kt
package com.riccardopinato.tabletopalarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun PermissionInfoDialog(
    needsCamera: Boolean,
    needsNotifications: Boolean,
    onContinue: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Permessi necessari")
        },
        text = {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Tabletop Motion Alarm richiede solo i permessi indispensabili alle funzioni che hai attivato."
                )

                if (needsCamera) {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null
                        )

                        Text(
                            "Fotocamera",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall
                        )

                        Text(
                            "Serve esclusivamente per controllare il flash LED. L'app non scatta foto.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }

                if (needsNotifications) {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null
                        )

                        Text(
                            "Notifiche",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleSmall
                        )

                        Text(
                            "Servono per mostrare chiaramente quando la protezione è attiva e quando viene rilevato movimento.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onContinue
            ) {
                Text("Continua")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annulla")
            }
        }
    )
}
