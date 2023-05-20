package com.techno_3_team.task_manager_google.net

import com.google.gson.Gson
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApi {

//    @GET("/tasks/v1/users/{token}/lists")
    @GET("/tasks/v1/users/@me/lists?maxResults=100")
    suspend fun getLists(
        @Query("token") token: String
    ) : retrofit2.Response<Gson>

    @GET("/tasks/v1/users/{token}/lists/{taskListId}")
    suspend fun getTaskList(
        @Path("taskListId") taskListId: String,
        @Path("token") token: String
    ) : retrofit2.Response<TaskList>

    @GET("https://oauth2.googleapis.com/tokeninfo?")
    suspend fun getToken(
        @Query("id_token") token: String
    ) : retrofit2.Response<NewApiToken>

    @GET("https://cloudresourcemanager.googleapis.com/v3/projects/{project_id}")
    suspend fun request(
        @Query("project_id") project_id: String
    ) : retrofit2.Response<Gson>

    @POST("https://accounts.google.com/o/oauth2/v2/auth?")
    suspend fun postToken(
        @Query("client_id") client_id: String,
        @Query("redirect_uri") redirect_uri: String,
        @Query("scope") scope: String,
        @Query("code_challenge") code_challenge: String,
        @Query("code_challenge_method") code_challenge_method: String,
        @Query("response_type") response_type: String,
    ): retrofit2.Response<String>

//    @POST("https://oauth2.googleapis.com/token")
//    suspend fun postToken(
//        @Query("client_is") client_id: String,
//        @Query("secret_client") secret_client: String,
//        @Query("uri") uri: String,
//        @Query("scope") scope: String,
//    )

}
/*
web:
client id: 176729332799-cjonm5oerv57tau4hpjmun71ua6b2hov.apps.googleusercontent.com
secret client: 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
uri: https://task-manager-2-386811.firebaseapp.com

android:
176729332799-nj3fescrstoane4j6pir4fejgpi6hvk3.apps.googleusercontent.com

scopes:
https://www.googleapis.com/auth/tasks
//hhtps://www.googleapis.com/auth/tasks.readonly

 */