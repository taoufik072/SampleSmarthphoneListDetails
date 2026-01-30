package fr.taoufikcode.data.smartphones.remote.api

import fr.taoufikcode.data.smartphones.remote.dto.HomeResponseDto
import fr.taoufikcode.data.smartphones.remote.dto.SmartphoneDetailsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SmartphoneApi {
    @GET("home/contents")
    suspend fun getSmartphoneList(): HomeResponseDto

    @GET("smartphoneDetails/{id}")
    suspend fun getSmartphoneDetails(@Path("id") id: String): SmartphoneDetailsDto
}
