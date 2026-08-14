package com.threecolumn.cbt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HobbyIdeaDao {
    @Query("SELECT * FROM hobby_ideas ORDER BY tried ASC, addedAt DESC")
    fun observeAll(): Flow<List<HobbyIdea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(idea: HobbyIdea): Long

    @Delete
    suspend fun delete(idea: HobbyIdea)
}
