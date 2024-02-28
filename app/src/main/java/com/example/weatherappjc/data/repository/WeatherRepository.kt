package com.example.weatherappjc.data.repository

import com.example.weatherappjc.data.apiservice.ApiService
import com.example.weatherappjc.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: ApiService) {
    fun getWeatherData(location: String): Flow<Result<WeatherResponse>> = flow {
        try {
            emit(Result.success(apiService.getWeatherData(location)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
