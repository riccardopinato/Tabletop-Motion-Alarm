// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/ui/theme/Theme.kt
package com.riccardopinato.tabletopalarm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightScheme =
    lightColorScheme()

private val DarkScheme =
    darkColorScheme()

@Composable
fun TabletopAlarmTheme(
    darkTheme: Boolean =
        isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (
            dynamicColor &&
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {
            val context =
                LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(
                    context
                )
            } else {
                dynamicLightColorScheme(
                    context
                )
            }
        } else {
            if (darkTheme) {
                DarkScheme
            } else {
                LightScheme
            }
        }

    MaterialTheme(
        colorScheme =
            colorScheme,
        content = content
    )
}
