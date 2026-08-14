package com.threecolumn.cbt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A free-form diary entry, optionally written against a reflection prompt
 * (e.g. a chapter topic from the book).
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val prompt: String = "",
    val body: String
)
