package com.techno_3_team.task_manager

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.Date

@SuppressLint("ParcelCreator")
data class MainTask(
    val name: String,
    val date: Date?,
    val subtasksDoneCount: Int?,
    val subtasksCount: Int?
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
//        TODO("Not yet implemented")
    }
}
