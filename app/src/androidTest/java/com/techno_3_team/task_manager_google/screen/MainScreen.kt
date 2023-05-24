package com.techno_3_team.task_manager_google.screen

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.kaspersky.kaspresso.screens.KScreen
import com.techno_3_team.task_manager_google.R
import com.techno_3_team.task_manager_google.adapters.TaskListAdapter
import com.techno_3_team.task_manager_google.fragments.MainFragment
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView

object MainScreen : KScreen<MainScreen>() {
    override val layoutId: Int?
        get() = R.layout.main_fragment
    override val viewClass: Class<*>?
        get() = MainFragment::class.java

    val addTaskButton = KButton { withId(R.id.FAB) }
    val recycler = KRecyclerView( builder = { withId(R.id.lv_tasks_list) }, {})

    fun simpleClick(button: KButton) {
        button {
            isVisible()
            click()
        }
    }
}