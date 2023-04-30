package com.techno_3_team.task_manager.fragments

import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.preference.PreferenceManager
import com.techno_3_team.task_manager.*
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.IS_DEFAULT_THEME_KEY
import com.techno_3_team.task_manager.support.LANGUAGE_KEY
import com.techno_3_team.task_manager.support.RESULT_KEY
import com.techno_3_team.task_manager.support.RandomData
import java.util.*


class MainFragment : Fragment(), Navigator {

    private var mainBinding: MainFragmentBinding? = null
    private val _mainBinding: MainFragmentBinding
        get() = mainBinding!!

    private lateinit var supportFM: FragmentManager
    private lateinit var listOfLists: ListOfLists
    private lateinit var randomData: RandomData
    private var sortOrder = SortOrder.BY_DATE
    private var isDay: Boolean = true

    private val currentFragment: Fragment?
        get() = requireActivity().supportFragmentManager.findFragmentById(_mainBinding.mainContainer.id)

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
    private var authorized = true

    //    private var authorized = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainBinding = MainFragmentBinding.inflate(layoutInflater)
        supportFM = requireActivity().supportFragmentManager

        val toolbar: Toolbar = requireActivity().findViewById(_mainBinding.appBar.id)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.app_name)

        initData()

        val taskListContainerFragment = TaskListContainerFragment.newInstance(listOfLists)
        if (savedInstanceState == null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(_mainBinding.mainContainer.id, taskListContainerFragment, "MF")
                .commit()
        }

        with(_mainBinding) {
            sideBar.btListsSideBar.setOnClickListener {
                showListSettingsScreen()
            }
            sideBar.radioButtonEn.setOnClickListener {
                setLocaleLanguage(requireActivity(), "en")
            }
            sideBar.radioButtonRus.setOnClickListener {
                setLocaleLanguage(requireActivity(), "ru")
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
                val currInflater: MenuInflater = inflater
                val fragment = currentFragment

                menu.clear()
                if (fragment is HasMainScreenActions) {
                    currInflater.inflate(R.menu.main_menu, menu)
                    requireActivity().actionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
                } else if (fragment is HasDeleteAction) {
                    currInflater.inflate(R.menu.menu_trashbox, menu)
                    if (currentFragment is ListsSettingsFragment) {
                        menu[0].isVisible = false
                    }
                    requireActivity().actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when {
                    item.itemId == R.id.clear_checked -> {
                        clearCheckedTasks()
                    }
                    item.itemId == R.id.sort_by_date -> {
                        updateTasksOrder()
                        sortOrder = SortOrder.BY_DATE
                    }
                    item.itemId == R.id.sort_by_name -> {
                        updateTasksOrder()
                        sortOrder = SortOrder.BY_NAME
                    }
                    item.itemId == R.id.sort_by_importance -> {
                        updateTasksOrder()
                        sortOrder = SortOrder.BY_IMPORTANCE
                    }
                    item.itemId == android.R.id.home && currentFragment is TaskListContainerFragment -> {
                        _mainBinding.drawer.openDrawer(GravityCompat.START)
                    }
                    else -> return false
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setCurrentThemeIcon()
        setLanguageRadioButton()

        supportFM.registerFragmentLifecycleCallbacks(fragmentListener, false)

        return _mainBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFM.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    private fun clearCheckedTasks() {
        // TODO()
    }

    private fun updateTasksOrder() {
        // TODO()
    }

    private fun initData() {
        randomData = RandomData((4..12).random(), 20, 12)
        listOfLists = randomData.getRandomData()
    }

    private fun updateUi() {
        val fragment = currentFragment
        if (fragment is HasCustomTitle) {
            requireActivity().actionBar?.title = (fragment as HasCustomTitle).getCustomTitle()
        } else {
            requireActivity().actionBar?.title = getString(R.string.app_name)
        }
//        onCreateOptionsMenu(_mainBinding.toolbar.menu)
    }

    override fun showTaskScreen(subtasksCount: Int) {
        val randomSubtaskList = randomData.getRandomSubtasks(subtasksCount)
        launchFragment(TaskFragment.newInstance(randomSubtaskList))
    }

    override fun showSubtaskScreen() {
        launchFragment(SubtaskFragment.newInstance(RandomData.getRandomSubtask()))
    }

    override fun showListSettingsScreen() {
        launchFragment(ListsSettingsFragment.newInstance(listOfLists))
        _mainBinding.drawer.closeDrawer(Gravity.LEFT)
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
            .replace(_mainBinding.mainContainer.id, fragment, "")
            .commit()
    }


    fun onClickListenerButtonDayNight(view: View) {
        toggleButtonImageDayNight()
    }

    private fun toggleButtonImageDayNight() {
        isDay = !isDay
        updateButtonImageDayNight()
    }

    private fun updateButtonImageDayNight() {
        preference.edit()
            .putBoolean(IS_DEFAULT_THEME_KEY, isDay)
            .apply()
        requireActivity().recreate()
    }

    private fun setCurrentThemeIcon() {
        val imgBt = requireActivity().findViewById<ImageButton>(R.id.btSwitcherTheme)
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
            _mainBinding.sideBar.radioButtonEn.isChecked = true
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
                rotateFab(_mainBinding.sideBar.accountSelectAction, !managementHidden)
            _mainBinding.sideBar.managementGroup.visibility = when {
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
            _mainBinding.sideBar.googleAccount.text = accountName
        } else {
            _mainBinding.sideBar.accountSelectAction.visibility = View.GONE
            _mainBinding.sideBar.managementGroup.visibility = View.GONE
            _mainBinding.sideBar.accountImage.setImageResource(R.drawable.google)
            _mainBinding.sideBar.googleAccount.setText(R.string.continue_with_google)
        }
    }
}