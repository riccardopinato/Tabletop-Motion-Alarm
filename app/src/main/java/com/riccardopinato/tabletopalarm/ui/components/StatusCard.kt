package com.riccardopinato.tabletopalarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riccardopinato.tabletopalarm.hardware.HardwareSnapshot
import com.riccardopinato.tabletopalarm.service.AlarmPhase
import com.riccardopinato.tabletopalarm.service.AlarmRuntimeState

@Composable
fun AlarmStatusCard(runtime: AlarmRuntimeState, countdownTotal: Int, hardware: HardwareSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (runtime.phase == AlarmPhase.TRIGGERED) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        if (runtime.phase == AlarmPhase.DISARMED) HardwareOnlyContent(hardware) else AlarmStateContent(runtime, countdownTotal)
    }
}

@Composable private fun HardwareOnlyContent(hardware: HardwareSnapshot) {
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=14.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("Hardware", style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
        HardwareRow("Accelerometro", hardware.hasAccelerometer)
        HardwareRow("Giroscopio", hardware.hasGyroscope)
        HardwareRow("Flash", hardware.hasFlash)
        HardwareRow("Vibrazione", hardware.hasVibrator, "Pronta")
    }
}

@Composable private fun HardwareRow(label:String, ready:Boolean, readyText:String="Pronto") {
    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween) {
        Text(label, style=MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(if(ready) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error))
            Text(if(ready) readyText else "Non disponibile", style=MaterialTheme.typography.bodySmall, fontWeight=FontWeight.Medium, color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun AlarmStateContent(runtime:AlarmRuntimeState, countdownTotal:Int) {
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Icon(when(runtime.phase){ AlarmPhase.ARMING,AlarmPhase.CALIBRATING->Icons.Default.HourglassTop; AlarmPhase.ARMED->Icons.Default.CheckCircle; else->Icons.Default.Warning }, null)
        Text(when(runtime.phase){ AlarmPhase.ARMING->"Armamento in corso"; AlarmPhase.CALIBRATING->"Calibrazione sensori"; AlarmPhase.ARMED->"Protezione attiva"; AlarmPhase.TRIGGERED->"MOVIMENTO RILEVATO"; AlarmPhase.ERROR->"Errore"; AlarmPhase.DISARMED->"Pronto"}, style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.SemiBold)
        Text(runtime.message, style=MaterialTheme.typography.bodyMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
        if(runtime.phase==AlarmPhase.ARMING){
            val progress=if(countdownTotal>0) 1f-runtime.countdownSeconds.toFloat()/countdownTotal else 0f
            LinearProgressIndicator(progress={progress.coerceIn(0f,1f)},modifier=Modifier.fillMaxWidth())
        } else if(runtime.phase==AlarmPhase.CALIBRATING) CircularProgressIndicator()
    }
}
