package com.techno_3_team.task_manager_google.fragments

import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.techno_3_team.task_manager_google.BuildConfig
import com.techno_3_team.task_manager_google.R
import com.techno_3_team.task_manager_google.data.LTSTViewModel
import com.techno_3_team.task_manager_google.databinding.MainFragmentBinding
import com.techno_3_team.task_manager_google.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager_google.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager_google.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager_google.navigators.Navigator
import com.techno_3_team.task_manager_google.navigators.navigator
import com.techno_3_team.task_manager_google.net.RetrofitClient
import com.techno_3_team.task_manager_google.net.TaskApi
import com.techno_3_team.task_manager_google.support.CURRENT_LIST_ID
import com.techno_3_team.task_manager_google.support.ID_TOKEN
import com.techno_3_team.task_manager_google.support.IS_AUTHORIZED
import com.techno_3_team.task_manager_google.support.IS_DEFAULT_THEME_KEY
import com.techno_3_team.task_manager_google.support.LANGUAGE_KEY
import com.techno_3_team.task_manager_google.support.RESULT_KEY
import com.techno_3_team.task_manager_google.support.USERNAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.Locale


class MainFragment : Fragment(), Navigator {

    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var supportFM: FragmentManager

    private val currentFragment: Fragment?
        get() = requireActivity().supportFragmentManager.findFragmentById(mainBinding.mainContainer.id)

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().baseContext)
    }

    private lateinit var ltstViewModel: LTSTViewModel

    private var fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            updateUi()
        }
    }

    private var managementHidden = true
    private var authorized = false

    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null
    private var oneTapClient: SignInClient? = null

    private val serverClientId by lazy { BuildConfig.CLIENT_ID }

    private var username: String? = null

    private var token: String? = null

    private val oneTapResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val tag = "val oneTapResult"
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val credential =
                        oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val idToken = credential?.googleIdToken
                    username = credential?.displayName

                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate with your backend.
                            preference.edit().putString(ID_TOKEN, idToken).apply()
                            preference.edit().putString(USERNAME, username).apply()
                            token = idToken
                            Log.e(tag, "idToken $idToken")
                            authorized = true
                            preference.edit().putBoolean(IS_AUTHORIZED, authorized).apply()
                            val welcomeMsg = getString(R.string.welcome) + " $username!"
                            val toast = Toast.makeText(context, welcomeMsg, Toast.LENGTH_LONG)
                            toast.show()

                            Log.e(tag, "activity recreate")
                            requireActivity().recreate()
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
        mainBinding = MainFragmentBinding.inflate(layoutInflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
        return mainBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        supportFM = requireActivity().supportFragmentManager

        val toolbar: MaterialToolbar = requireActivity().findViewById(mainBinding.toolbar.id)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)

        val taskListContainerFragment = TaskListContainerFragment()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(mainBinding.mainContainer.id, taskListContainerFragment, "MF")
            .commit()

        with(mainBinding) {
            sideBar.btListsSideBar.setOnClickListener {
                navigator().showListSettingsScreen()
            }
            sideBar.radioButtonEn.setOnClickListener {
                setLocaleLanguage(requireActivity(), "en")
            }
            sideBar.radioButtonRus.setOnClickListener {
                setLocaleLanguage(requireActivity(), "ru")
            }
            sideBar.btSwitcherTheme.setOnClickListener {
                updateTheme()
            }

            Log.e("onViewCreated", "authorized $authorized")
            authorized = preference.getBoolean(IS_AUTHORIZED, false)
            Log.e("onViewCreated", "preference authorized $authorized")
            username = preference.getString(USERNAME, null)
            token = preference.getString(ID_TOKEN, null)
            setAccountButton()
            sideBar.btGoogleSideBAr.setOnClickListener {
                accountManagement()
                if (!authorized)
                    displaySignIn()
            }
            sideBar.btGoogleSideBAr2AccountManagement.setOnClickListener {
                Log.e("onViewCreated", "click on sign out")
                oneTapClient?.signOut()
                preference.edit().putString(ID_TOKEN, null).apply()
                username = null
                preference.edit().putString(USERNAME, username).apply()
                authorized = false
                preference.edit().putBoolean(IS_AUTHORIZED, authorized).apply()
                requireActivity().recreate()
            }
            sideBar.btGoogleSideBAr2Sync.setOnClickListener {
                googleSynchronize()
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                updateMenu(menu, inflater)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when {
                    item.itemId == R.id.clear_checked -> {
                        deleteCheckedTasks()
                    }
//                    item.itemId == R.id.sort_by_name ||
//                            item.itemId == R.id.sort_by_date -> {
//                        updateTasksOrder(item.itemId)
//                    }
                    item.itemId == android.R.id.home && currentFragment is TaskListContainerFragment -> {
                        mainBinding.drawer.openDrawer(GravityCompat.START)
                    }

                    item.itemId == R.id.delete_task -> {
                        setDeleteDialog()
                    }

                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setCurrentThemeIcon()
        setLanguageRadioButton()

        supportFM.registerFragmentLifecycleCallbacks(fragmentListener, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFM.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    private fun deleteTask() {
        (currentFragment as HasDeleteAction).delete()
    }

    private fun deleteCheckedTasks() {
        val currListId = preference.getInt(CURRENT_LIST_ID, -1)
        ltstViewModel.deleteCompletedTasks(currListId)
    }

    private fun updateTasksOrder(itemId: Int) {
    }

    /**
     * UI
     */

    private fun updateUi() {
        val fragment = currentFragment
        val bar = (requireActivity() as AppCompatActivity).supportActionBar
        if (fragment is HasCustomTitle) {
            bar?.title = (fragment as HasCustomTitle).getCustomTitle()
        } else {
            bar?.title = getString(R.string.app_name)
        }
        updateMenu(mainBinding.toolbar.menu, requireActivity().menuInflater)
    }


    private fun setDeleteDialog() {
        val message = getString(R.string.delete_task_dialog_msg)
        val deleteBut = getString(R.string.delete_button_name)
        val cancelBut = getString(R.string.cancel_button_name)

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(deleteBut) { _, _ ->
            deleteTask()
        }
        builder.setNegativeButton(cancelBut) { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun updateMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        val bar = (requireActivity() as AppCompatActivity).supportActionBar
        if (currentFragment is HasMainScreenActions) {
            inflater.inflate(R.menu.main_menu, menu)
            bar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        } else {
            if (currentFragment is HasDeleteAction) {
                inflater.inflate(R.menu.menu_trashbox, menu)
            }
            bar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
    }

    private fun updateTheme() {
        val isDay = preference.getBoolean(IS_DEFAULT_THEME_KEY, true)
        Log.println(Log.INFO, "tag", "update $isDay")
        preference.edit()
            .putBoolean(IS_DEFAULT_THEME_KEY, !isDay)
            .apply()
        requireActivity().recreate()
    }

    private fun setCurrentThemeIcon() {
        val isDay = preference.getBoolean(IS_DEFAULT_THEME_KEY, true)
        Log.println(Log.INFO, "tag", "set $isDay")
        val imgBt =
            requireActivity().findViewById<ImageButton>(mainBinding.sideBar.btSwitcherTheme.id)
        if (isDay) {
            imgBt.setImageResource(R.drawable.baseline_wb_sunny_32)
        } else {
            imgBt.setImageResource(R.drawable.baseline_nights_stay_32)
        }
    }

    private fun setLanguageRadioButton() {
        val languageCode = preference.getInt(LANGUAGE_KEY, -1)

        val currLang = Locale.getDefault().language
        if (languageCode < 1 && currLang == "en" || languageCode == 0 && currLang == "ru") {
            mainBinding.sideBar.radioButtonEn.isChecked = true
        }

        if (languageCode == 0 && currLang == "ru") {
            setLocaleLanguage(requireActivity(), "en")
        } else if (languageCode > 0 && currLang == "en") {
            setLocaleLanguage(requireActivity(), "ru")
        }
    }

    private fun setLocaleLanguage(activity: Activity, languageStringCode: String?) {
        if (Locale.getDefault().language == languageStringCode) {
            return
        }
        preference.edit()
            .putInt(
                LANGUAGE_KEY,
                if (languageStringCode == "ru") 1 else 0
            ).apply()

        val locale = languageStringCode?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    private fun rotateFab(view: View, managementHidden: Boolean): Boolean {
        view.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (!managementHidden) 180f else 0f)
        return managementHidden
    }

    private fun setAccountButton() {
        if (authorized) {
            mainBinding.sideBar.googleAccount.text = username
            accountManagement()
        } else {
            mainBinding.sideBar.accountSelectAction.visibility = View.GONE
            mainBinding.sideBar.managementGroup.visibility = View.GONE
            mainBinding.sideBar.accountImage.setImageResource(R.drawable.google)
            mainBinding.sideBar.googleAccount.setText(R.string.continue_with_google)
            setAuthorizationVariables()
        }
    }

    /**
     * GOOGLE AUTHORIZATION
     */

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

    private fun accountManagement() {
        if (authorized) {
            Log.e("accountManagement", "managementHidden $managementHidden")
            managementHidden = rotateFab(mainBinding.sideBar.accountSelectAction, !managementHidden)
            mainBinding.sideBar.managementGroup.visibility = when {
                mainBinding.sideBar.managementGroup.visibility == View.VISIBLE -> View.GONE
                else -> View.VISIBLE
            }
            Log.e("accountManagement", "managementHidden $managementHidden")
        }
    }


    /**
     * NET
     */


    @RequiresApi(Build.VERSION_CODES.O)
    private fun googleSynchronize() {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = getRetrofitApi(gson)
        CoroutineScope(Dispatchers.IO).launch {
//            val newToken = retrofit.getToken(token!!)
//            Log.e("googleSynchronize", newToken.toString())

            val  charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val secureRandom = SecureRandom()
            val str= (1..43).map {
                secureRandom.nextInt(charPool.size).let { charPool[it] }
            }.joinToString("")
            val codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(str.toByteArray())

            val md = MessageDigest.getInstance("SHA-256")
            val codeChallenge=  Base64.getUrlEncoder().withoutPadding()
                .encodeToString(md.digest(codeVerifier.toByteArray()))
            println(codeChallenge)

            val token = retrofit.postToken(
                "176729332799-nj3fescrstoane4j6pir4fejgpi6hvk3.apps.googleusercontent.com",
                "https://my-app.com/callback",
                "https://www.googleapis.com/auth/tasks",
                codeChallenge.toString(),
                "S256",
                "code"
            )
            Log.e("googleSynchronize", token.toString())


            /*             "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",
                        "https://task-manager-2-386811.firebaseapp.com",
                        "https://www.googleapis.com/auth/tasks")*/

//            val lists = retrofit.getLists(token!!)
//            val lists = retrofit.getLists("ya29.a0AWY7CkljcnMhO173gmuQR_fFvZecm0BkoU5VyaByEt4eEzx_S5mrpG22Mpf8Gelbq2iqRsSKmS35rkuMJRofBiVJToyQ1M30uFkhQpOzuJophocAFxgHmJ4-FYHaL6czXi6lT7jtuT-gHnLTHkdmVgo_77T0GU8czkV8aCgYKAcISARMSFQG1tDrpYohw9cUZ9B4J80uQnPuhsA0171")
//            Log.e("googleSynchronize", lists.toString())
        }
    }

    private fun getRetrofitApi(gson: Gson) =
        RetrofitClient.getClient(gson).create(TaskApi::class.java)


//    fun provideRetrofit(client: OkHttpClient, gson: Gson) =
//        Retrofit.Builder().baseUrl(BuildConfig.ENDPOINT)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//
//    fun provideApi(retrofit: Retrofit): TaskApi = retrofit.create(TaskApi::class.java)
//
//    fun provideGson(): Gson = GsonBuilder().create()
//
//    fun provideOkHttpClient(restInterceptor: Interceptor) =
//        OkHttpClient.Builder()
//            .readTimeout(10, TimeUnit.SECONDS)
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .callTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(restInterceptor)
//            .build()


    /**
     * NAVIGATION
     */

    override fun showTaskScreen(taskId: Int) {
        launchFragment(TaskFragment(taskId))
    }

    override fun showSubtaskScreen(taskId: Int, subtaskId: Int) {
        launchFragment(SubtaskFragment(taskId, subtaskId))
    }

    override fun showListSettingsScreen() {
        launchFragment(ListsSettingsFragment())
        mainBinding.drawer.closeDrawer(Gravity.LEFT)
    }

    override fun goToMainScreen() {
        requireActivity().supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun goBack() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun <T : Parcelable> publishResult(result: T) {
        requireActivity().supportFragmentManager.setFragmentResult(
            result.javaClass.name,
            bundleOf(RESULT_KEY to result)
        )
    }

    private fun launchFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(mainBinding.mainContainer.id, fragment, "")
            .commit()
    }
}