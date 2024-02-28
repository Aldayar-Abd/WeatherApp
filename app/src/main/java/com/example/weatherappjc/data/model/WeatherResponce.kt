package com.example.weatherappjc.data.model

data class WeatherResponse(
    val current: Current?,
    val forecast: Forecast?,
    val location: Location?
)