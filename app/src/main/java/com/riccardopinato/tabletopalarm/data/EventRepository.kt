// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/data/EventRepository.kt
package com.riccardopinato.tabletopalarm.data

import android.content.Context
import com.riccardopinato.tabletopalarm.model.AlarmEvent
import com.riccardopinato.tabletopalarm.model.AlarmEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class EventRepository(context: Context) {

    private val file = File(context.filesDir, "events.json")
    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    private val _events =
        MutableStateFlow(loadFromDisk())

    val events: StateFlow<List<AlarmEvent>> =
        _events.asStateFlow()

    fun add(
        type: AlarmEventType,
        detail: String = ""
    ) {
        scope.launch {
            mutex.withLock {
                val event = AlarmEvent(
                    id = System.nanoTime(),
                    timestamp = System.currentTimeMillis(),
                    type = type,
                    detail = detail
                )

                val updated =
                    (listOf(event) + _events.value)
                        .take(MAX_EVENTS)

                persist(updated)
                _events.value = updated
            }
        }
    }

    fun clear() {
        scope.launch {
            mutex.withLock {
                persist(emptyList())
                _events.value = emptyList()
            }
        }
    }

    private fun loadFromDisk(): List<AlarmEvent> {
        if (!file.exists()) return emptyList()

        return runCatching {
            val array = JSONArray(file.readText())

            buildList {
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)

                    add(
                        AlarmEvent(
                            id = item.optLong("id"),
                            timestamp = item.optLong("timestamp"),
                            type = runCatching {
                                AlarmEventType.valueOf(
                                    item.optString("type")
                                )
                            }.getOrDefault(
                                AlarmEventType.ERROR
                            ),
                            detail = item.optString("detail")
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun persist(
        events: List<AlarmEvent>
    ) {
        val array = JSONArray()

        events.forEach { event ->
            array.put(
                JSONObject()
                    .put("id", event.id)
                    .put("timestamp", event.timestamp)
                    .put("type", event.type.name)
                    .put("detail", event.detail)
            )
        }

        file.writeText(array.toString())
    }

    private companion object {
        const val MAX_EVENTS = 200
    }
}
