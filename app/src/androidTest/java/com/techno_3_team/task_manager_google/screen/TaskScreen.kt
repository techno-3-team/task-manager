package com.techno_3_team.task_manager_google.screen

import com.kaspersky.kaspresso.screens.KScreen
import com.techno_3_team.task_manager_google.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView

object TaskScreen : KScreen<TaskScreen>() {
    override val layoutId: Int?
        get() = R.layout.task_fragment
    override val viewClass: Class<*>?
        get() = TaskScreen::class.java

    val taskNameView = KEditText { withId(R.id.editText) }
    val taskDescView = KTextView { withId(R.id.taDesc) }
    val submitButton = KButton { withId(R.id.submit_button) }

    fun simpleClick(button: KButton) {
        button {
            isVisible()
            click()
        }
    }

    fun addTaskName(name: String) {
        taskNameView.typeText(name)
    }
}