package com.techno_3_team.task_manager_google.net

import com.google.gson.annotations.SerializedName

data class NewApiToken(
    @SerializedName("iss")
    val iss: String,

    @SerializedName("sub")
    val sub: String,

    @SerializedName("aud")
    val aud: String,

    @SerializedName("iat")
    val iat: String,

    @SerializedName("exp")
    val exp: String
)