package com.threecolumn.cbt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A dated page in the single-topic journal (see JOURNAL_TOPIC). */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val body: String
)
