package com.techno_3_team.task_manager.fragments

import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.techno_3_team.task_manager.R

abstract class Authorized : Fragment() {
    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null

    var oneTapClient: SignInClient? = null
//    var showOneTapUI = true

    private val serverClientId by lazy { getString(R.string.web_client_id) }

    var successfullyAuthorized = false
    var username: String? = null

    fun setAuthorizationVariables() {
        Log.e("startAuthorization", "creating requests")
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(serverClientId)
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(serverClientId)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
        Log.e("startAuthorization", "get one tap client")
        oneTapClient = Identity.getSignInClient(requireActivity())
    }


    fun displaySignIn() {
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener(requireActivity()) { result ->
                try {
                    Log.e("displaySignIn", "siginig in")
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapRes(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("displaySignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }?.addOnFailureListener(requireActivity()) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                displaySignUp()
                Log.e("displaySignIn", e.localizedMessage!!)
            }
    }

    private fun displaySignUp() {
        oneTapClient?.beginSignIn(signUpRequest!!)
            ?.addOnSuccessListener(requireActivity()) { result ->
                try {
                    Log.e("displaySignUp", "siginig up")
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapRes(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("displaySignUp", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            ?.addOnFailureListener(requireActivity()) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI.
                displaySignUp()
                Log.e("displaySignUp", "FailureListener ${e.localizedMessage!!}")
            }
    }

    private fun oneTapRes(intentSenderRequest: IntentSenderRequest) {
        oneTapResult.launch(intentSenderRequest)
    }

    abstract val oneTapResult: ActivityResultLauncher<IntentSenderRequest>

}