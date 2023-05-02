package com.techno_3_team.task_manager.fragments

import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.appbar.MaterialToolbar
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.*
import java.util.*


class MainFragment : Authorized(), Navigator {

    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var supportFM: FragmentManager
    private var isDay: Boolean = false

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

    private var managementHidden = false

    //временная переменная до создания логики авторизированного пользователя
    //TODO: инициализировать переменную в правильных местах
//    private var authorized = true
    //    private var authorized = false
    private var authorized = preference.getBoolean("authorized", false)
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
            setAccountButton()
            //TODO нужно ли?
            accountManagement()
            sideBar.btGoogleSideBAr.setOnClickListener {
                accountManagement()
//                if (!successfullyAuthorized)
                if(!authorized)
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
        val message: String
        val deleteBut: String
        val cancelBut: String
        val title: String
        if (mainBinding.sideBar.radioButtonRus.isChecked) {
            message = "Вы уверены, что хотите удалить задачу?"
            deleteBut = "УДАЛИТЬ"
            cancelBut = "ОТМЕНИТЬ"
        } else {
            message = "Do you want to delete this task?"
            deleteBut = "DELETE"
            cancelBut = "CANCEL"
            title = "Are you sure?"
        }

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
        isDay = !isDay
        preference.edit()
            .putBoolean(IS_DEFAULT_THEME_KEY, isDay)
            .apply()
        requireActivity().recreate()
    }

    private fun setCurrentThemeIcon() {
        isDay = preference.getBoolean(IS_DEFAULT_THEME_KEY, isDay)
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

    private fun rotateFab(view: View, rotate: Boolean): Boolean {
        view.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (!rotate) 180f else 0f)
        return rotate
    }

    private fun accountManagement() {
        Log.e("accountManagement", "managementHidden $managementHidden")
        Log.e("accountManagement", "successfully Authorized $successfullyAuthorized")
        Log.e("accountManagement", "authorized $authorized")
        Log.e("accountManagement", "username $username")
        if (authorized) {
//        if (successfullyAuthorized) {
            managementHidden =
                rotateFab(mainBinding.sideBar.accountSelectAction, !managementHidden)
            mainBinding.sideBar.managementGroup.visibility = when {
                managementHidden -> View.GONE
                else -> View.VISIBLE
            }
            Log.e("accountManagement", "managementHidden $managementHidden")
        } else {
            Log.e("accountManagement", "starting authorization")
            setAuthorizationVariables()
        }
    }

    private fun setAccountButton() {
        if (authorized) {
//        if (successfullyAuthorized) {
            mainBinding.sideBar.googleAccount.text = username
            accountManagement()
        } else {
            mainBinding.sideBar.accountSelectAction.visibility = View.GONE
            mainBinding.sideBar.managementGroup.visibility = View.GONE
            mainBinding.sideBar.accountImage.setImageResource(R.drawable.google)
            mainBinding.sideBar.googleAccount.setText(R.string.continue_with_google)
        }
    }

    override val oneTapResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val tag = "val oneTapResult"
            try {
                if (result.resultCode == Activity.RESULT_OK) {
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
                            authorized = true
                            preference.edit().putBoolean("authorized", authorized).apply()
                            preference.edit().putBoolean(AUTH_KEY, true).apply()
                            val welcomeMsg = getString(R.string.welcome) + " $username!"
                            val toast = Toast.makeText(context, welcomeMsg, Toast.LENGTH_LONG)
                            toast.show()

                            Log.e(tag, "refresh")

//                            requireActivity().recreate()
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