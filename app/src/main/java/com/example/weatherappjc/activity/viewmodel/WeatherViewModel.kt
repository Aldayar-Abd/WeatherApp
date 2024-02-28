package com.example.weatherappjc.activity.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappjc.R
import com.example.weatherappjc.data.model.ForecastModel
import com.example.weatherappjc.data.model.WeatherResponse
import com.example.weatherappjc.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository,
) :
    ViewModel() {
    private val _weatherData = MutableStateFlow<Result<WeatherResponse>?>(null)

    val isLoading = MutableStateFlow(false)

    val currentWeatherText: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.current?.condition?.text
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentWeatherTempMax: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.day?.maxtemp_c.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val sunrise: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.astro?.sunrise
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val sunset: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.astro?.sunset
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val moonPhase: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.astro?.moon_phase
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val humidity: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.current?.humidity.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val heatIndex: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.hour?.get(0)?.heatindex_f.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    val currentLocation: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.location?.country.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentWeatherTempMin: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.day?.mintemp_c.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentChanceOfSnow: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.hour?.get(0)?.chance_of_snow.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val currentChanceOfRain: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.hour?.get(0)?.chance_of_rain.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val currentWind: StateFlow<String?> = _weatherData.map {
        if (it?.isSuccess == true) {
            it.getOrNull()?.forecast?.forecastday?.get(0)?.hour?.get(0)?.wind_kph.toString()
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val forecastData: StateFlow<List<ForecastModel>?> = _weatherData.map { result ->
        if (result?.isSuccess == true) {
            result.getOrNull()?.forecast?.forecastday?.map { forecastDay ->
                ForecastModel(
                    date = forecastDay.date.toString(),
                    image = R.drawable.ic_weather,
                    weather = forecastDay.day?.condition?.text.toString(),
                    tempMax = forecastDay.day?.maxtemp_c?.toString()!!,
                    tempMin = forecastDay.day.mintemp_c?.toString()!!
                )
            }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


     fun getWeather(location: String) {
        viewModelScope.launch {
            repository.getWeatherData(location = location).collect {
                _weatherData.value = it
            }
        }
    }

    fun refreshData(location: String) {
        viewModelScope.launch {
            repository.getWeatherData(location = location).collect {
                _weatherData.value = it
            }
        }
    }
}