package com.recipeme.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.recipeme.models.Ingredient
import com.recipeme.models.Instructions
import com.recipeme.models.UsedIngredientsItem

class Converters() {

    @TypeConverter
    fun fromList(value: MutableList<Ingredient>?): String?{
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): MutableList<Ingredient> {
        return Gson().fromJson(value, object : TypeToken<MutableList<Ingredient>>(){}.type)
    }



    @TypeConverter
    fun fromListInstructions(value: Instructions): String?{
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun toListInstructions(value: String): Instructions {
        return Gson().fromJson(value, object : TypeToken<Instructions>(){}.type)
    }

    @TypeConverter
    fun fromListUsedIngredients(value: MutableList<UsedIngredientsItem>?): String?{
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun toListUsedIngredients(value: String): MutableList<UsedIngredientsItem> {
        return Gson().fromJson(value, object : TypeToken<MutableList<UsedIngredientsItem>>(){}.type)
    }
}