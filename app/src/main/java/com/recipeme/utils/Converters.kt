package com.recipeme.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.recipeme.models.Ingredient

class Converters() {

    @TypeConverter
    fun fromList(value: MutableList<Ingredient>?): String?{
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): MutableList<Ingredient> {
        return Gson().fromJson(value, object : TypeToken<MutableList<Ingredient>>(){}.type)
    }
}