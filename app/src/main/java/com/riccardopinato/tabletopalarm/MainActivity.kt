// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/MainActivity.kt
package com.riccardopinato.tabletopalarm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.riccardopinato.tabletopalarm.auth.BiometricAuthManager
import com.riccardopinato.tabletopalarm.auth.GoogleAccountManager
import com.riccardopinato.tabletopalarm.ui.TabletopAlarmRoot
import com.riccardopinato.tabletopalarm.ui.theme.TabletopAlarmTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        enableEdgeToEdge()

        val container =
            (application as TabletopAlarmApp)
                .container

        val googleAccountManager =
            GoogleAccountManager(
                activity = this,
                webClientId =
                    getString(
                        R.string
                            .google_web_client_id
                    )
            )

        val biometricAuthManager =
            BiometricAuthManager(this)

        setContent {
            TabletopAlarmTheme {
                TabletopAlarmRoot(
                    container = container,
                    googleAccountManager =
                        googleAccountManager,
                    biometricAuthManager =
                        biometricAuthManager
                )
            }
        }
    }
}
