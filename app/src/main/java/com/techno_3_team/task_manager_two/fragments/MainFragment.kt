package com.techno_3_team.task_manager_two.fragments

import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.appbar.MaterialToolbar
import com.techno_3_team.task_manager_two.R
import com.techno_3_team.task_manager_two.databinding.MainFragmentBinding
import com.techno_3_team.task_manager_two.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager_two.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager_two.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager_two.navigators.Navigator
import com.techno_3_team.task_manager_two.navigators.navigator
import com.techno_3_team.task_manager_two.support.*
import java.util.*


class MainFragment : Fragment(), Navigator {

    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var supportFM: FragmentManager

    private val currentFragment: Fragment?
        get() = requireActivity().supportFragmentManager.findFragmentById(mainBinding.mainContainer.id)

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireActivity().baseContext)
    }

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

    private val serverClientId by lazy { getString(R.string.web_client_id) }

    private var username: String? = null

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
                            Log.e(tag, "idToken $idToken")
                            authorized = true
                            preference.edit().putBoolean(AUTHORIZED, authorized).apply()
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
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportFM = requireActivity().supportFragmentManager

        val toolbar: MaterialToolbar = requireActivity().findViewById(mainBinding.toolbar.id)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)

        val taskListContainerFragment = TaskListContainerFragment.newInstance(
            RandomData((4..12).random(), 20, 12).getRandomData()
        )
        if (savedInstanceState == null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(mainBinding.mainContainer.id, taskListContainerFragment, "MF")
//                .addToBackStack(null)
                .commit()
        }

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
            authorized = preference.getBoolean(AUTHORIZED, false)
            Log.e("onViewCreated", "preference authorized $authorized")
            username = preference.getString(USERNAME, null)
            setAccountButton()
            sideBar.btGoogleSideBAr.setOnClickListener {
                accountManagement()
                if (!authorized)
                    displaySignIn()
            }
            sideBar.btGoogleSideBAr2AccountManagement.setOnClickListener {
                //TODO: управление аккаунтами гугл
            }
            sideBar.btGoogleSideBAr2Sync.setOnClickListener {
                //TODO: синхорнизировать задачи с гугл аккаунтом.
                //для разреешения коллизий -- наше приложение в приоритете
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
                        clearCheckedTasks()
                    }
                    item.itemId == R.id.sort_by_date ||
                            item.itemId == R.id.sort_by_name ||
                            item.itemId == R.id.sort_by_importance -> {
                        updateTasksOrder(item.itemId)
                    }
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

    private fun setDeleteDialog() {
        val message = getString(R.string.delete_task_dialog_msg)
        val deleteBut = getString(R.string.delete_button_name)
        val cancelBut = getString(R.string.cancel_button_name)

        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(message)

        builder.setCancelable(false)
        builder.setPositiveButton(deleteBut) { dialog, which ->
            deleteTask()
        }

        builder.setNegativeButton(cancelBut) { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteTask() {
        // TODO()
    }

    private fun clearCheckedTasks() {
        // TODO()
    }

    private fun updateTasksOrder(itemId: Int) {
        // TODO()
    }

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

    override fun showTaskScreen(subtasksCount: Int) {
        launchFragment(
            TaskFragment.newInstance(
                RandomData((4..12).random(), 20, 12)
                    .getRandomSubtasks(subtasksCount)
            )
        )
    }

    override fun showSubtaskScreen() {
        launchFragment(SubtaskFragment.newInstance(RandomData.getRandomSubtask()))
    }

    override fun showListSettingsScreen() {
        launchFragment(
            ListsSettingsFragment.newInstance(
                RandomData((4..12).random(), 20, 12).getRandomData()
            )
        )
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