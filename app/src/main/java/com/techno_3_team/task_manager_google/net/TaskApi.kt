package com.techno_3_team.task_manager_google.net

import com.google.gson.Gson
import retrofit2.http.GET
import retrofit2.http.Path

interface TaskApi {

    @GET("/tasks/v1/users/{token}/lists")
    suspend fun getLists(
        @Path("token") token: String
    ) : retrofit2.Response<TaskList>

    @GET("/tasks/v1/users/{token}/lists/{taskListId}")
    suspend fun getTaskList(
        @Path("taskListId") taskListId: String,
        @Path("token") token: String
    ) : retrofit2.Response<Gson>
}