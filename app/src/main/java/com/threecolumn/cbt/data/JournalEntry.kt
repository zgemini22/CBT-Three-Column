package com.threecolumn.cbt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A dated page in the single-topic journal (see R.string.journal_topic).
 * sortIndex drives manual drag-to-reorder position (higher sorts first);
 * it starts equal to createdAt so a fresh page's default order is newest-first.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val body: String,
    val pinned: Boolean = false,
    val sortIndex: Long = createdAt
)
