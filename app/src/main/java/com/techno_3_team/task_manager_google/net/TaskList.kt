package com.techno_3_team.task_manager_google.net

import com.google.gson.annotations.SerializedName

data class TaskList(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated")
    val updated: String,
    @SerializedName("selfLink")
    val selfLink: String
)