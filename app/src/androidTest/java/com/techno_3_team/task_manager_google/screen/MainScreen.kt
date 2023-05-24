package com.techno_3_team.task_manager_google.screen

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.screens.KScreen
import com.techno_3_team.task_manager_google.MainActivity
import com.techno_3_team.task_manager_google.R
import com.techno_3_team.task_manager_google.fragments.MainFragment
import io.github.kakaocup.kakao.text.KButton
import org.junit.Rule
import org.junit.Test

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int?
        get() = R.layout.main_fragment
    override val viewClass: Class<*>?
        get() = MainFragment::class.java

    val addTaskButton = KButton { withId(R.id.FAB) }

}