// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/screens/EventsScreen.kt
package com.riccardopinato.tabletopalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riccardopinato.tabletopalarm.data.EventRepository
import com.riccardopinato.tabletopalarm.model.AlarmEvent
import com.riccardopinato.tabletopalarm.model.AlarmEventType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventsScreen(
    eventRepository:
        EventRepository
) {
    val events by
        eventRepository
            .events
            .collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp
                )
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 20.dp,
                        bottom = 12.dp
                    ),
            horizontalArrangement =
                Arrangement
                    .SpaceBetween
        ) {
            Text(
                "Eventi",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            if (events.isNotEmpty()) {
                TextButton(
                    onClick =
                        eventRepository::clear
                ) {
                    Text("Cancella")
                }
            }
        }

        if (events.isEmpty()) {
            Text(
                "Nessun evento registrato.",
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                items(
                    items = events,
                    key = {
                        it.id
                    }
                ) {
                    event ->

                    EventCard(event)
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: AlarmEvent
) {
    val dateFormat =
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        )

    Card {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            verticalArrangement =
                Arrangement.spacedBy(
                    4.dp
                )
        ) {
            Text(
                event.type
                    .toItalianLabel(),
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Text(
                dateFormat.format(
                    Date(
                        event.timestamp
                    )
                ),
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            if (
                event.detail.isNotBlank()
            ) {
                Text(
                    event.detail,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }
        }
    }
}

private fun AlarmEventType
    .toItalianLabel(): String =
    when (this) {
        AlarmEventType
            .ARMING_STARTED ->
            "Armamento avviato"

        AlarmEventType.ARMED ->
            "Allarme armato"

        AlarmEventType.TRIGGERED ->
            "Movimento rilevato"

        AlarmEventType.DISARMED ->
            "Allarme disattivato"

        AlarmEventType.CANCELED ->
            "Armamento annullato"

        AlarmEventType.ERROR ->
            "Errore"
    }
