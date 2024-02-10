package com.lukyanov.app.component.films.data.db.converter

import androidx.room.TypeConverter

class ListConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toList(value: String?) = value?.split("|") ?: emptyList()

        @TypeConverter
        @JvmStatic
        fun fromList(value: List<String>?) = value?.joinToString(separator = "|") ?: ""
    }
}