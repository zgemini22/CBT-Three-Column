package com.threecolumn.cbt.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDistortionList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toDistortionList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")
}
