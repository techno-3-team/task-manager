package com.techno_3_team.task_manager.support

import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.structures.ListOfTasks
import com.techno_3_team.task_manager.structures.SubTask
import com.techno_3_team.task_manager.structures.Task
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

    private fun getRandomString(length: Int): String {
        return (1..length).joinToString("") {
            ('a'.plus((0..25).random())).toString()
        }
    }

    private fun getRandomTasks(count: Int): ArrayList<Task> {
        val taskList = ArrayList<Task>()
        for (i in 0 until count) {
            val isDateNull = Random.nextBoolean()
            val isSubTasksExists = Random.nextBoolean()
            var subTasks: ArrayList<SubTask>? = null
            if (isSubTasksExists) {
                subTasks = getRandomSubTasks((1..maxSubTasksCount).random())
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

    private fun getRandomSubTasks(count: Int): ArrayList<SubTask> {
        val subTaskList = ArrayList<SubTask>()
        for (i in 0 until count) {
            subTaskList.add(
                SubTask(
                    getRandomString((5..100).random()),
                    Random.nextBoolean(),
                    if (Random.nextBoolean()) null else Date(System.currentTimeMillis()),
                    getRandomString((25..100).random()),
                )
            )
        }
        return subTaskList
    }
}
