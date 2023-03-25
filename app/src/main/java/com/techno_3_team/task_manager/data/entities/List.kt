package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "list_table")
@Parcelize
data class List(
    @PrimaryKey(autoGenerate = false)
    val listName: Int
) : Parcelable