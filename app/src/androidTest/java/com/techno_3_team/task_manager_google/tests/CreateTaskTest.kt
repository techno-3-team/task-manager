package com.techno_3_team.task_manager_google.tests

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.techno_3_team.task_manager_google.MainActivity
import com.techno_3_team.task_manager_google.scenario.MainScreenTransitionScenario
import org.junit.Rule
import org.junit.Test

class CreateTaskTest : TestCase() {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun test() = run {
        step("Transition to Main Screen") {
            scenario(MainScreenTransitionScenario())
        }
    }
}