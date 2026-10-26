package com.threecolumn.cbt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A dated page in the single-topic journal (see R.string.journal_topic).
 * Each page has its own optional title; blank means untitled.
 * sortIndex drives manual drag-to-reorder position (higher sorts first);
 * it starts equal to createdAt so a fresh page's default order is newest-first.
 * `pinned` is unused (the feature was removed) but kept as a column so
 * existing installs that already migrated to it don't need another schema
 * change just to drop it.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val body: String,
    @ColumnInfo(defaultValue = "''") val title: String = "",
    val pinned: Boolean = false,
    val sortIndex: Long = createdAt
) {
    companion object {
        /** A title is shown on one line, so pasted line breaks become single spaces. */
        fun cleanTitle(raw: String): String =
            raw.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
    }
}
