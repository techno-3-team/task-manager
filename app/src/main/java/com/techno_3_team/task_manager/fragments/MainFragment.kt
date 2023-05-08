package com.techno_3_team.task_manager.fragments

import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
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
import com.google.android.material.appbar.MaterialToolbar
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.*
import java.util.*


class MainFragment : Fragment(), Navigator {

    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var supportFM: FragmentManager

    private val currentFragment: Fragment?
        get() = requireActivity().supportFragmentManager.findFragmentById(mainBinding.mainContainer.id)

    private val preference: SharedPreferences by lazy {
        requireActivity().applicationContext.getSharedPreferences("app_base_preference", Context.MODE_PRIVATE)
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

    private var managementHidden = false

    //временная переменная до создания логики авторизированного пользователя
    //TODO: инициализировать переменную в правильных местах
    private var authorized = true

    //    private var authorized = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = MainFragmentBinding.inflate(layoutInflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
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

        val taskListContainerFragment = TaskListContainerFragment()
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
                setLocaleLanguage("en")
            }
            sideBar.radioButtonRus.setOnClickListener {
                setLocaleLanguage("ru")
            }
            sideBar.btSwitcherTheme.setOnClickListener {
                updateTheme()
            }
            setAccountButton()
            accountManagement()
            sideBar.btGoogleSideBAr.setOnClickListener {
                accountManagement()
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
                        deleteCheckedTasks()
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
        builder.setPositiveButton(deleteBut) { _, _ ->
            deleteTask()
        }
        builder.setNegativeButton(cancelBut) { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteTask() {
        (currentFragment as HasDeleteAction).delete()
    }

    private fun deleteCheckedTasks() {
        val currListId = preference.getInt(CURRENT_LIST_ID, -1)
        ltstViewModel.deleteCompletedTasks(currListId)
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
            setLocaleLanguage("en")
        } else if (languageCode > 0 && currLang == "en") {
            setLocaleLanguage("ru")
        }
    }

    private fun setLocaleLanguage(languageStringCode: String?) {
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
        val resources: Resources = requireActivity().resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }

    fun rotateFab(view: View, rotate: Boolean): Boolean {
        view.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (!rotate) 180f else 0f)
        return rotate
    }

    private fun accountManagement() {
        Log.e("accountManagement", "managementHidden $managementHidden")
        if (authorized) {
            managementHidden =
                rotateFab(mainBinding.sideBar.accountSelectAction, !managementHidden)
            mainBinding.sideBar.managementGroup.visibility = when {
                managementHidden -> View.GONE
                else -> View.VISIBLE
            }
            Log.e("accountManagement", "managementHidden $managementHidden")
        } else {
            //авторизация -- войти
        }
    }

    private fun setAccountButton() {
        if (authorized) {
            //TODO: получить имя пользователя
            val accountName = null
            mainBinding.sideBar.googleAccount.text = accountName
        } else {
            mainBinding.sideBar.accountSelectAction.visibility = View.GONE
            mainBinding.sideBar.managementGroup.visibility = View.GONE
            mainBinding.sideBar.accountImage.setImageResource(R.drawable.google)
            mainBinding.sideBar.googleAccount.setText(R.string.continue_with_google)
        }
    }

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