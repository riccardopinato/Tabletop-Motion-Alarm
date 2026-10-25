// TARGET: app/src/main/java/com/riccardopinato/tabletopalarm/auth/GoogleAccountManager.kt
package com.riccardopinato.tabletopalarm.auth

import android.content.MutableContextWrapper
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.riccardopinato.tabletopalarm.model.GoogleProfile

class GoogleAccountManager(
    private val activity: FragmentActivity,
    private val webClientId: String
) {

    private val credentialManager =
        CredentialManager.create(activity)

    suspend fun signIn():
        Result<GoogleProfile> {

        if (
            webClientId.isBlank() ||
            webClientId.startsWith(
                "REPLACE_WITH_"
            )
        ) {
            return Result.failure(
                IllegalStateException(
                    "Google Sign-In non configurato: " +
                        "sostituisci " +
                        "google_web_client_id " +
                        "in strings.xml."
                )
            )
        }

        return runCatching {
            val option =
                GetSignInWithGoogleOption
                    .Builder(
                        serverClientId =
                            webClientId
                    )
                    .build()

            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(
                        option
                    )
                    .build()

            val response =
                credentialManager
                    .getCredential(
                        context =
                            MutableContextWrapper(
                                activity
                            ),
                        request =
                            request
                    )

            val credential =
                response.credential

            require(
                credential is
                    CustomCredential
            ) {
                "Credenziale Google inattesa."
            }

            require(
                credential.type ==
                    GoogleIdTokenCredential
                        .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                "Tipo di credenziale Google inatteso."
            }

            val googleCredential =
                GoogleIdTokenCredential
                    .createFrom(
                        credential.data
                    )

            GoogleProfile(
                id =
                    googleCredential.uniqueId,
                displayName =
                    googleCredential.displayName,
                email =
                    googleCredential.email,
                photoUrl =
                    googleCredential
                        .profilePictureUri
                        ?.toString()
            )
        }
    }

    suspend fun signOut():
        Result<Unit> {

        return runCatching {
            credentialManager
                .clearCredentialState(
                    ClearCredentialStateRequest()
                )
        }
    }
}
