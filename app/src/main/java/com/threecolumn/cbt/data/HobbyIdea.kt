package com.threecolumn.cbt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * An entry in the "do it for yourself" hobby/interest list — things worth doing
 * for their own sake, independent of anyone else's approval.
 */
@Entity(tableName = "hobby_ideas")
data class HobbyIdea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String = "",
    val addedAt: Long,
    val tried: Boolean = false
)
