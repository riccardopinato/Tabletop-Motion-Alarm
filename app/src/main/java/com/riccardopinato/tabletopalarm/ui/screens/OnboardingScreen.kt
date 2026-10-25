// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/OnboardingScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val text: String
)

@Composable
fun OnboardingScreen(
    onCompleted: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                icon = Icons.Default.Security,
                title = "Proteggi il telefono",
                text = "Appoggia il dispositivo, attiva l'allarme e lascia che Tabletop Motion Alarm controlli eventuali movimenti."
            ),
            OnboardingPage(
                icon = Icons.Default.Notifications,
                title = "Allarme immediato",
                text = "Se il telefono viene spostato o sollevato, partono sirena, vibrazione e flash LED."
            ),
            OnboardingPage(
                icon = Icons.Default.Lock,
                title = "Solo tu puoi fermarlo",
                text = "Configura un PIN e, se disponibile, usa anche la biometria per disattivare rapidamente l'allarme."
            )
        )
    }

    var page by remember {
        mutableIntStateOf(0)
    }

    val current = pages[page]

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = {
                        (page + 1).toFloat() /
                            pages.size.toFloat()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(18.dp)
            ) {
                Icon(
                    imageVector = current.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    current.title,
                    style =
                        MaterialTheme.typography
                            .headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    current.text,
                    style =
                        MaterialTheme.typography
                            .bodyLarge,
                    textAlign = TextAlign.Center,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (page < pages.lastIndex) {
                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),
                        onClick = {
                            page++
                        }
                    ) {
                        Text("Continua")
                    }

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),
                        onClick = onCompleted
                    ) {
                        Text("Salta")
                    }
                } else {
                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),
                        onClick = onCompleted
                    ) {
                        Text("Inizia")
                    }
                }
            }
        }
    }
}
