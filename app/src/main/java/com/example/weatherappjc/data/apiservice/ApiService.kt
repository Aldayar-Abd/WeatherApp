package com.example.weatherappjc.data.apiservice
import com.example.weatherappjc.BuildConfig.API_KEY
import com.example.weatherappjc.data.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("q") location: String,
        @Query("key") apiKey: String = API_KEY,
        @Query("days") forecastDays: Int = 3
    ):WeatherResponse
}