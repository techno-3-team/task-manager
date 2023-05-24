package com.techno_3_team.task_manager_google.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import com.techno_3_team.task_manager_google.screen.LoginScreen

class MainScreenTransitionScenario : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Transition to Main Screen") {
            LoginScreen {
                loginWithoutAuthorizationButton {
                    isVisible()
                    click()
                }
            }
        }
    }

}