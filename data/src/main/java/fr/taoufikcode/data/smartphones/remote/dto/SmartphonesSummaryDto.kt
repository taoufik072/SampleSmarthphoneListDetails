package fr.taoufikcode.data.smartphones.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeResponseDto(
    @param:Json(name = "smartphones") val smartphones: List<SmartphoneSummaryDto>
)

@JsonClass(generateAdapter = true)
data class SmartphoneSummaryDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "model") val model: String?,
    @param:Json(name = "imageUrl") val imageUrl: String?
)
