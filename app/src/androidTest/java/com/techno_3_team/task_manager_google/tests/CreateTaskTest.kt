package com.techno_3_team.task_manager_google.tests

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.techno_3_team.task_manager_google.MainActivity
import com.techno_3_team.task_manager_google.scenario.MainScreenTransitionScenario
import com.techno_3_team.task_manager_google.screen.MainScreen
import com.techno_3_team.task_manager_google.screen.TaskScreen
import org.junit.Rule
import org.junit.Test

class CreateTaskTest : TestCase() {

    private val mainScreenTransitionStep = "Transition to Main Screen"
    private val addNewTaskStep = "Create new task"
    private val checkHintsStep = "Check task name and description hints"
    private val taskNameHint = "task name"
    private val taskDescHint = "task description"

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun test() = run {
        step(mainScreenTransitionStep) {
            scenario(MainScreenTransitionScenario())
        }
        step(addNewTaskStep) {
            MainScreen {
                simpleClick(addTaskButton)
            }
        }
        step(checkHintsStep) {
            TaskScreen {
                taskNameView.hasHint(taskNameHint)
                taskDescView.hasHint(taskDescHint)
                addTaskName("First task")
                simpleClick(submitButton)
            }
        }
        step(addNewTaskStep) {
            MainScreen {
                recycler.hasSize(1)
            }
        }
    }
}