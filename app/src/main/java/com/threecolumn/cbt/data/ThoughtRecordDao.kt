package com.threecolumn.cbt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ThoughtRecordDao {
    @Query("SELECT * FROM thought_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ThoughtRecord>>

    @Query("SELECT * FROM thought_records WHERE id = :id")
    suspend fun getById(id: Long): ThoughtRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: ThoughtRecord): Long

    @Delete
    suspend fun delete(record: ThoughtRecord)
}
