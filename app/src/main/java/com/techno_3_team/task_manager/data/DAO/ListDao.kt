package com.techno_3_team.task_manager.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.techno_3_team.task_manager.data.entities.List

@Dao
interface ListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)
}