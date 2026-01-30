package fr.taoufikcode.data.smartphones.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmartphoneDetailsDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "model") val model: String?,
    @param:Json(name = "price") val price: Double?,
    @param:Json(name = "description") val description: String?,
    @param:Json(name = "constructionDate") val constructionDate: String?,
    @param:Json(name = "imageUrl") val imageUrl: String?
)
