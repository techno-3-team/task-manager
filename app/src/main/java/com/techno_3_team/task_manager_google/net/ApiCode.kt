package com.techno_3_team.task_manager_google.net

import com.google.gson.annotations.SerializedName

data class ApiCode(
    @SerializedName("code")
    val access_code: String/*,
    @SerializedName("error")
    val error: String*/
)