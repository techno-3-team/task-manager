package com.techno_3_team.task_manager.fragments

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.databinding.LoginFragmentBinding
import com.techno_3_team.task_manager.navigators.PrimaryNavigator
import com.techno_3_team.task_manager.support.AUTHORIZED
import com.techno_3_team.task_manager.support.AUTH_KEY
import com.techno_3_team.task_manager.support.ID_TOKEN
import com.techno_3_team.task_manager.support.USERNAME

class LoginFragment : Fragment() {

    private var loginBinding: LoginFragmentBinding? = null
    private val _loginBinding: LoginFragmentBinding
        get() = loginBinding!!

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().baseContext)
    }

    private var authorized = false

    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null

    private var oneTapClient: SignInClient? = null

    private val serverClientId by lazy { getString(R.string.web_client_id) }

    private var username: String? = null

    private val oneTapResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val tag = "val oneTapResult"
            try {
                if (result.resultCode == RESULT_OK) {
//                    oneTapClient = Identity.getSignInClient(requireActivity())
                    val credential =
                        oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val idToken = credential?.googleIdToken
                    username = credential?.displayName
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate with your backend.
                            preference.edit().putString(ID_TOKEN, idToken).apply()
                            preference.edit().putString(USERNAME, username).apply()
                            Log.e(tag, "idToken $idToken")
                            authorized = true
                            preference.edit().putBoolean(AUTHORIZED, authorized).apply()
                            Log.e(tag, "authorized $authorized")
                            preference.edit().putBoolean(AUTH_KEY, true).apply()
                            val welcomeMsg = getString(R.string.welcome) + " $username!"
                            val toast = Toast.makeText(context, welcomeMsg, Toast.LENGTH_LONG)
                            toast.show()

                            Log.e(tag, "go to main fragment")
                            (requireActivity() as PrimaryNavigator).showMainFragment()
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.e(tag, "No ID token or password!")
                        }
                    }
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.e(tag, "One-tap dialog was closed.")
                        // Don't re-prompt the user.
                        val toast =
                            Toast.makeText(context, getString(R.string.cancel), Toast.LENGTH_LONG)
                        toast.show()
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.e(tag, "One-tap encountered a network error.")
                        // Try again or just ignore.
                        val toast = Toast.makeText(
                            context,
                            getString(R.string.network_error),
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    }
                    else -> {
                        Log.e(
                            tag, "Couldn't get credential from result." +
                                    " (${exception.localizedMessage})"
                        )
                        val toast =
                            Toast.makeText(context, getString(R.string.cancel), Toast.LENGTH_LONG)
                        toast.show()
                    }
                }
            }
        }

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
            Log.e("onViewCreated", "clicked on continue without authorization")
            preference.edit()
                .putBoolean(AUTH_KEY, false)
                .apply()
            (requireActivity() as PrimaryNavigator).showMainFragment()
        }
        loginBinding!!.continueWithGoogle.setOnClickListener {
            Log.e("onViewCreated", "clicked on google authorization")
            setAuthorizationVariables()
            displaySignIn()
        }

        Log.e("onViewCreated", "login fragment was created")
    }

    override fun onDestroyView() {
        loginBinding = null
        super.onDestroyView()
    }

    private fun setAuthorizationVariables() {
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

    private fun displaySignIn() {
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
}