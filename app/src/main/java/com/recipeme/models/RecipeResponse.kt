package com.recipeme.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RecipeResponse(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("usedIngredients")
	val usedIngredients: List<UsedIngredientsItem?>? = null,

	@field:SerializedName("missedIngredients")
	val missedIngredients: List<MissedIngredientsItem?>? = null,

	@field:SerializedName("extendedIngredients")
	val extenedIngredients: List<UsedIngredientsItem?>? = null,

//	@field:SerializedName("missedIngredientCount")
//	val missedIngredientCount: Int? = null,
//
//	@field:SerializedName("unusedIngredients")
//	val unusedIngredients: List<Any?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

)


data class UsedIngredientsItem(

	@field:SerializedName("originalName")
	val originalName: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("amount")
	val amount: Any? = null,

	@field:SerializedName("unit")
	val unit: String? = null,

	@field:SerializedName("unitShort")
	val unitShort: String? = null,

	@field:SerializedName("original")
	val original: String? = null,

	@field:SerializedName("meta")
	val meta: List<String?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("unitLong")
	val unitLong: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("aisle")
	val aisle: String? = null
)


data class MissedIngredientsItem(

	@field:SerializedName("originalName")
	val originalName: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("amount")
	val amount: Any? = null,

	@field:SerializedName("unit")
	val unit: String? = null,

	@field:SerializedName("unitShort")
	val unitShort: String? = null,

	@field:SerializedName("original")
	val original: String? = null,

	@field:SerializedName("meta")
	val meta: List<Any?>? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("unitLong")
	val unitLong: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("aisle")
	val aisle: String? = null
)
