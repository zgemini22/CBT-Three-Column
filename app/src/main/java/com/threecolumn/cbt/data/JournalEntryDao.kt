package com.threecolumn.cbt.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY pinned DESC, sortIndex DESC")
    fun observeAll(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries ORDER BY pinned DESC, sortIndex DESC")
    suspend fun getAllOnce(): List<JournalEntry>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getById(id: Long): JournalEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: JournalEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<JournalEntry>)

    @Delete
    suspend fun delete(entry: JournalEntry)
}
