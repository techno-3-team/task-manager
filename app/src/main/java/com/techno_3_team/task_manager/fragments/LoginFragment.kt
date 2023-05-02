package com.techno_3_team.task_manager.fragments

import android.app.Activity.RESULT_OK
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.databinding.LoginFragmentBinding
import com.techno_3_team.task_manager.navigators.PrimaryNavigator
import com.techno_3_team.task_manager.support.AUTH_KEY

class LoginFragment : Authorized() {

    private var loginBinding: LoginFragmentBinding? = null
    private val _loginBinding: LoginFragmentBinding
        get() = loginBinding!!

//    private var showOneTapUI = true
//    private var oneTapClient: SignInClient? = null
//    private var signUpRequest: BeginSignInRequest? = null
//    private var signInRequest: BeginSignInRequest? = null
//
//    private val serverClientId by lazy { getString(R.string.web_client_id) }

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().baseContext)
    }

//    временная переменная до создания логики авторизированного пользователя
//    TODO: инициализировать переменную в правильных местах
//    private var authorized = true
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
//            (requireActivity() as PrimaryNavigator).showMainFragment()
        }

        Log.e("onViewCreated", "login fragment was created")
    }

    override fun onDestroyView() {
        loginBinding = null
        super.onDestroyView()
    }

//    private val oneTapResult =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            try {
//                val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
//                val idToken = credential?.googleIdToken
//                when {
//                    idToken != null -> {
//                        // Got an ID token from Google. Use it to authenticate
//                        // with your backend.
//                        preference.edit().putString("idToken", idToken)
//                        val msg = "idToken $idToken"
//                        Snackbar.make(loginBinding!!.root, msg, Snackbar.LENGTH_INDEFINITE).show()
//                        Log.e("onActivityResult", msg)
//                    }
//                    else -> {
//                        // Shouldn't happen.
//                        Log.e("onActivityResult", "No ID token or password!")
//                    }
//                }
//            } catch (e: ApiException) {
//                when (e.statusCode) {
//                    CommonStatusCodes.CANCELED -> {
//                        Log.e("onActivityResult", "One-tap dialog was closed.")
//                        // Don't re-prompt the user.
//                        showOneTapUI = false
//                    }
//                    CommonStatusCodes.NETWORK_ERROR -> {
//                        Log.e("onActivityResult", "One-tap encountered a network error.")
//                        // Try again or just ignore.
//                    }
//                    else -> {
//                        Log.e(
//                            "onActivityResult", "Couldn't get credential from result." +
//                                    " (${e.localizedMessage})"
//                        )
//                    }
//                }
//            }
//
//        }

    //вынести в отдельный класс
//    private fun displaySignIn() {
//        oneTapClient?.beginSignIn(signInRequest!!)
//            ?.addOnSuccessListener(requireActivity()) { result ->
//                try {
//                    Log.e("displaySignIn", "siginig in")
//                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
//                    oneTapResult.launch(ib)
//                } catch (e: IntentSender.SendIntentException) {
//                    Log.e("displaySignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
//                }
//            }?.addOnFailureListener(requireActivity()) { e ->
//            // No Google Accounts found. Just continue presenting the signed-out UI.
//            displaySignUp()
//            Log.e("displaySignIn", e.localizedMessage!!)
//        }
//    }

    //вынести в отдельный класс
//    private fun displaySignUp() {
//        oneTapClient?.beginSignIn(signUpRequest!!)
//            ?.addOnSuccessListener(requireActivity()) { result ->
//                try {
//                    Log.e("displaySignUp", "siginig up")
//                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
//                    oneTapResult.launch(ib)
//                } catch (e: IntentSender.SendIntentException) {
//                    Log.e("displaySignUp", "Couldn't start One Tap UI: ${e.localizedMessage}")
//                }
//            }
//            ?.addOnFailureListener(requireActivity()) { e ->
//                // No Google Accounts found. Just continue presenting the signed-out UI.
//                displaySignUp()
//                Log.e("displaySignUp", "FailureListener ${e.localizedMessage!!}")
//            }
//    }

    //вынести в отдельный класс
//    private fun startAuthorization() {
//        signUpRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId(serverClientId)
//                    // Show all accounts on the device.
//                    .setFilterByAuthorizedAccounts(false)
//                    .build()
//            )
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(false)
//            .build()
//        signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(serverClientId)
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build()
//            )
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(true)
//            .build()
//        oneTapClient = Identity.getSignInClient(requireActivity())
//    }

    override val oneTapResult =
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
                            preference.edit().putString("idToken", idToken).apply()
                            preference.edit().putString("username", username).apply()
                            Log.e(tag, "idToken $idToken")
                            successfullyAuthorized = true
                            preference.edit().putBoolean("authorized", successfullyAuthorized).apply()
                            Log.e(tag, "successfully Authorized $successfullyAuthorized")
                            preference.edit().putBoolean(AUTH_KEY, true).apply()
                            val welcomeMsg = getString(R.string.welcome) + " $username!"
                            val toast = Toast.makeText(context, welcomeMsg, Toast.LENGTH_LONG)
                            toast.show()

                            Log.e(tag, "go to main fragment")

                            //TODO исправить навигацию
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
//                        showOneTapUI = false
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
}