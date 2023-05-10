package com.techno_3_team.task_manager_two.support

import com.techno_3_team.task_manager_two.structures.ListOfLists
import com.techno_3_team.task_manager_two.structures.ListOfTasks
import com.techno_3_team.task_manager_two.structures.Subtask
import com.techno_3_team.task_manager_two.structures.Task
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class RandomData(
    private val listsCount: Int,
    private val maxTasksCount: Int,
    private val maxSubTasksCount: Int
) {

    fun getRandomData(): ListOfLists {
        return getRandomListOfLists(listsCount)
    }

    private fun getRandomListOfLists(count: Int): ListOfLists {
        val resList = ArrayList<ListOfTasks>()
        for (i in 0 until count) {
            val taskList = getRandomTasks((2..maxTasksCount).random())
            resList.add(
                ListOfTasks(
                    getRandomString((5..20).random()),
                    taskList,
                    taskList.size,
                    taskList.stream().filter { it.isCompleted }.count().toInt()
                )
            )
        }
        return ListOfLists(resList)
    }

    private fun getRandomTasks(count: Int): ArrayList<Task> {
        val taskList = ArrayList<Task>()
        for (i in 0 until count) {
            val isDateNull = Random.nextBoolean()
            val isSubTasksExists = Random.nextBoolean()
            var subTasks: ArrayList<Subtask>? = null
            if (isSubTasksExists) {
                subTasks = getRandomSubtasks((1..maxSubTasksCount).random())
            }
            taskList.add(
                Task(
                    getRandomString((5..100).random()),
                    Random.nextBoolean(),
                    if (isDateNull) null else Date(System.currentTimeMillis()),
                    getRandomString((25..100).random()),
                    if (isSubTasksExists)
                        subTasks!!.stream().filter { it.isCompleted }.count().toInt() else null,
                    if (isSubTasksExists) subTasks!!.size else null,
                    subTasks
                )
            )
        }
        return taskList
    }

    fun getRandomSubtasks(count: Int): ArrayList<Subtask> {
        val subTaskList = ArrayList<Subtask>()
        for (i in 0 until count) {
            subTaskList.add(
                getRandomSubtask()
            )
        }
        return subTaskList
    }

    companion object {
        fun getRandomSubtask() : Subtask {
            return Subtask(
                getRandomString((5..100).random()),
                Random.nextBoolean(),
                if (Random.nextBoolean()) null else Date(System.currentTimeMillis()),
                getRandomString((25..100).random()),
            )
        }
    }
}
