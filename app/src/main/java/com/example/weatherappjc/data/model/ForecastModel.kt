package com.example.weatherappjc.data.model

data class ForecastModel(
    val date: String,
    val image: Int,
    val weather: String,
    val tempMin: String,
    val tempMax: String
)