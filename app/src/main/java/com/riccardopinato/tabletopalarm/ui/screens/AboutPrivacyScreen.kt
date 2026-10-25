// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/AboutPrivacyScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.riccardopinato.tabletopalarm.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPrivacyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Info e privacy")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBack
                    ) {
                        Text("Indietro")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Tabletop Motion Alarm",
                style =
                    MaterialTheme.typography
                        .headlineSmall
            )

            Text(
                "Versione ${BuildConfig.VERSION_NAME}",
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            InfoCard(
                title = "Come funziona",
                text =
                    "Quando attivi la protezione, l'app usa i sensori di movimento del dispositivo per rilevare spostamenti o sollevamenti. Se viene rilevato un movimento significativo, può attivare sirena, vibrazione e flash LED in base alle tue impostazioni."
            )

            InfoCard(
                title = "Dati e privacy",
                text =
                    "Il rilevamento del movimento viene elaborato sul dispositivo. L'app non registra audio, non usa la posizione e non scatta fotografie. Lo storico degli eventi resta nella memoria locale dell'app."
            )

            InfoCard(
                title = "Fotocamera",
                text =
                    "Il permesso Fotocamera viene utilizzato esclusivamente per consentire il controllo della torcia/flash LED tramite le API Android. Tabletop Motion Alarm non usa la fotocamera per acquisire immagini o video."
            )

            InfoCard(
                title = "Account Google",
                text =
                    "L'accesso Google è facoltativo e viene utilizzato per associare un profilo all'app. Nome, indirizzo email e identificativo del profilo possono essere conservati localmente nelle preferenze dell'app. Il PIN di sicurezza non viene sincronizzato con l'account Google."
            )

            InfoCard(
                title = "Backup Android",
                text =
                    "Le impostazioni non sensibili possono essere incluse nei meccanismi Android di backup o trasferimento dispositivo. PIN, hash del PIN e cronologia degli eventi sono esclusi dal backup."
            )

            InfoCard(
                title = "Servizio in primo piano",
                text =
                    "Quando armi l'allarme, viene avviato un servizio in primo piano con una notifica permanente. È necessario per continuare a monitorare i sensori mentre lo schermo è spento o l'app non è visibile. Il servizio termina quando disattivi la protezione."
            )

            InfoCard(
                title = "Limiti",
                text =
                    "L'app è uno strumento di deterrenza e segnalazione locale. Non può impedire fisicamente un furto, lo spegnimento del dispositivo, il force-stop dell'app o altre azioni effettuate tramite il sistema operativo."
            )
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    text: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            Text(
                title,
                style =
                    MaterialTheme.typography
                        .titleMedium
            )

            Text(
                text,
                style =
                    MaterialTheme.typography
                        .bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}
