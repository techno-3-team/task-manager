package com.techno_3_team.task_manager_google.net

import com.google.gson.annotations.SerializedName

class AccessToken (
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("expires_in")
    val expires_in: String,
    @SerializedName("refresh_token")
    val refresh_token: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("token_type")
    val token_type: String,
)