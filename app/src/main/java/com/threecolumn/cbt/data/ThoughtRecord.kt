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
    /** 0..100, or [BELIEF_UNSET] when the user did not rate it. */
    val beliefBefore: Int,
    val beliefAfter: Int
) {
    val hasBelief: Boolean get() = beliefBefore >= 0 && beliefAfter >= 0

    companion object {
        const val BELIEF_UNSET = -1
    }
}
