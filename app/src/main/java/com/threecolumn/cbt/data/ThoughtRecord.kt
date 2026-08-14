package com.threecolumn.cbt.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One entry in the three-column technique:
 * automatic thought -> cognitive distortion(s) -> rational response.
 */
@Entity(tableName = "thought_records")
data class ThoughtRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val situation: String = "",
    val automaticThought: String,
    val distortionKeys: List<String>,
    val rationalResponse: String,
    val beliefBefore: Int,
    val beliefAfter: Int
)
