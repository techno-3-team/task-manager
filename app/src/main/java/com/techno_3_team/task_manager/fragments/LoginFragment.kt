package com.techno_3_team.task_manager.fragments

import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.navigators.PrimaryNavigator
import com.techno_3_team.task_manager.databinding.LoginFragmentBinding
import com.techno_3_team.task_manager.support.AUTH_KEY

class LoginFragment : Fragment() {

    private var loginBinding: LoginFragmentBinding? = null
    private val _loginBinding: LoginFragmentBinding
        get() = loginBinding!!

    private var showOneTapUI = true
    private var oneTapClient: SignInClient? = null
    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null

    private val serverClientId by lazy { getString(R.string.web_client_id) }

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext)
    }

//    временная переменная до создания логики авторизированного пользователя
//    TODO: инициализировать переменную в правильных местах
    private var authorized = true
//    private var authorized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = LoginFragmentBinding.inflate(inflater)
        return _loginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _loginBinding.continueWithoutAutorization.setOnClickListener {
            Log.e("tag", "clicked on continue without authorization")
            preference.edit()
                .putBoolean(AUTH_KEY, false)
                .apply()
            (requireActivity() as PrimaryNavigator).showMainFragment()
        }
        loginBinding!!.continueWithGoogle.setOnClickListener {
            Log.e("tag", "clicked on google authorization")
            startAuthorization()
            displaySignIn()
//            (requireActivity() as PrimaryNavigator).showMainFragment()
        }

        Log.println(Log.INFO, "tag", "login fragment was created")
    }

    override fun onDestroyView() {
        loginBinding = null
        super.onDestroyView()
    }

    private val oneTapResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
                val idToken = credential?.googleIdToken
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        preference.edit().putString("idToken", idToken)
                        val msg = "idToken $idToken"
                        Snackbar.make(loginBinding!!.root, msg, Snackbar.LENGTH_INDEFINITE).show()
                        Log.e("onActivityResult", msg)
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.e("onActivityResult", "No ID token or password!")
                    }
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.e("onActivityResult", "One-tap dialog was closed.")
                        // Don't re-prompt the user.
                        showOneTapUI = false
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.e("onActivityResult", "One-tap encountered a network error.")
                        // Try again or just ignore.
                    }
                    else -> {
                        Log.e(
                            "onActivityResult", "Couldn't get credential from result." +
                                    " (${e.localizedMessage})"
                        )
                    }
                }
            }

        }

    //вынести в отдельный класс
    private fun displaySignIn() {
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener(requireActivity()) { result ->
                try {
                    Log.e("displaySignIn", "siginig in")
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("displaySignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }?.addOnFailureListener(requireActivity()) { e ->
            // No Google Accounts found. Just continue presenting the signed-out UI.
            displaySignUp()
            Log.e("displaySignIn", e.localizedMessage!!)
        }
    }

    //вынести в отдельный класс
    private fun displaySignUp() {
        oneTapClient?.beginSignIn(signUpRequest!!)
            ?.addOnSuccessListener(requireActivity()) { result ->
                try {
                    Log.e("displaySignUp", "siginig up")
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
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

    //вынести в отдельный класс
    private fun startAuthorization() {
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
        oneTapClient = Identity.getSignInClient(requireActivity())
    }
}