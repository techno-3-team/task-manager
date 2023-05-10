package com.techno_3_team.task_manager_two.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "list_table")
@Parcelize
data class List(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    var listName: String
) : Parcelable