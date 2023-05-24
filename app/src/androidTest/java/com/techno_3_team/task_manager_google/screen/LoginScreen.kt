package com.techno_3_team.task_manager_google.screen

import com.kaspersky.kaspresso.screens.KScreen
import com.techno_3_team.task_manager_google.R
import io.github.kakaocup.kakao.text.KButton

object LoginScreen : KScreen<LoginScreen>() {

    override val layoutId: Int?
        get() = R.layout.login_fragment
    override val viewClass: Class<*>?
        get() = null

    val loginWithoutAuthorizationButton = KButton { withId(R.id.continue_without_autorization) }
    val loginWithAuthorizationButton = KButton { withId(R.id.continue_with_google) }
}