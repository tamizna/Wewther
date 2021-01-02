package com.tamizna.wewther.services

import com.tamizna.wewther.models.ResponseWeather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") query: String,
        @Query("appid") apiId: String
    ): ResponseWeather
}