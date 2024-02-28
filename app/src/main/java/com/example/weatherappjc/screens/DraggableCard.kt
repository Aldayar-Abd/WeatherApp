package com.example.weatherappjc.screens

import android.content.Context
import android.location.LocationManager
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherappjc.R
import com.example.weatherappjc.activity.viewmodel.WeatherViewModel
import com.example.weatherappjc.ui.theme.CustomGrey
import com.example.weatherappjc.core.weatherAnimation

@Composable
fun DraggableCard(viewModel: WeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val isLocationEnabled =
        remember { mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) }
    val showDialog = remember { mutableStateOf(!isLocationEnabled.value) }
    if (!isLocationEnabled.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Location is turned off") },
            text = { Text("Please turn on location services to get weather data for your location.") },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }


    val weatherText by viewModel.currentWeatherText.collectAsState()
    val textForAnim = weatherText.toString()

    val currentWeatherMax by viewModel.currentWeatherTempMax.collectAsState()
    val currentWeatherMin by viewModel.currentWeatherTempMin.collectAsState()
    val currentWind by viewModel.currentWind.collectAsState()
    val chanceOfRain by viewModel.currentChanceOfRain.collectAsState()
    val chanceOfSnow by viewModel.currentChanceOfSnow.collectAsState()
    val forecastDay by viewModel.forecastData.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val sunrise by viewModel.sunrise.collectAsState()
    val sunset by viewModel.sunset.collectAsState()
    val moonPhase by viewModel.moonPhase.collectAsState()
    val humidity by viewModel.humidity.collectAsState()
    val heatIndex by viewModel.heatIndex.collectAsState()
    val offsetY = remember { mutableStateOf(0f) }

    val animatedHeight by animateDpAsState(
        targetValue = 400.dp + offsetY.value.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomGrey)
    ) {
        Card(
            modifier = Modifier
                .height(animatedHeight)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->

                        offsetY.value += delta.coerceIn(0f, 100f)
                    },
                    onDragStopped = { velocity ->
                        if (velocity < 0) {
                            offsetY.value = 0f
                        } else {
                            offsetY.value = 100f
                        }
                    }
                )
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(bottomStart = 200.dp, bottomEnd = 200.dp)
        ) {
            Box(modifier = Modifier.background(Color.White)) {
                if (offsetY.value >= 100) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                            .background(Color.White)
                    ) {
                        items(forecastDay ?: emptyList()) { item ->
                            ItemForForeCast(model = item)
                        }
                    }
                }

                if (offsetY.value < 100) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 10.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.padding(end = 20.dp)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = "location",
                                modifier = Modifier.size(36.dp),
                            )
                            Text(
                                text = currentLocation.toString(),
                                modifier = Modifier.padding(bottom = 5.dp),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }
                        Text(
                            text = currentWeatherMax.toString() + "°C/" + currentWeatherMin.toString() + "°C",
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weatherText.toString(),
                            modifier = Modifier.padding(bottom = 20.dp),
                            fontSize = 34.sp
                        )
                        Card(
                            modifier = Modifier.size(150.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 100.dp),
                            shape = RoundedCornerShape(100.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val animationResource =
                                    weatherAnimation[textForAnim] ?: R.raw.loading
                                val compositionResult by rememberLottieComposition(
                                    spec = LottieCompositionSpec.RawRes(
                                        animationResource
                                    )
                                )

                                LottieAnimation(
                                    composition = compositionResult,
                                    modifier = Modifier.size(200.dp),
                                    iterations = LottieConstants.IterateForever
                                )
                            }

                        }
                    }
                }

            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Row {
                        Text(
                            text = "Sunrise:",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sunrise.toString(),
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin
                        )
                    }
                    Row {
                        Text(
                            text = "Sunset:",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sunset.toString(),
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin
                        )
                    }
                    Row {
                        Text(
                            text = "Moon Phase:",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = moonPhase.toString(),
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        color = CustomGrey,
                        thickness = 3.dp
                    )

                    Row {
                        Text(
                            text = "Humidity:",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = humidity.toString() + "%",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin
                        )
                    }
                    Row {
                        Text(
                            text = "Heat index:",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = heatIndex.toString() + "F",
                            modifier = Modifier.padding(5.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Thin
                        )
                    }

                }

            }

            Row {
                Card(
                    modifier = Modifier
                        .size(height = 130.dp, width = 120.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(top = 5.dp)) {
                        val animationResource = R.raw.heavy_rain
                        val compositionResult by rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(
                                animationResource
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(60.dp),
                            composition = compositionResult,
                            iterations = LottieConstants.IterateForever
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = chanceOfRain.toString() + " %",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "Chance of Rain",
                            fontSize = 16.sp,
                        )
                    }

                }

                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(height = 130.dp, width = 120.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(top = 5.dp)) {
                        val animationResource = R.raw.windy
                        val compositionResult by rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(
                                animationResource
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(60.dp),
                            composition = compositionResult,
                            iterations = LottieConstants.IterateForever

                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = currentWind.toString() + " kph",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "Current wind",
                            fontSize = 16.sp,
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .size(height = 130.dp, width = 120.dp)
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(top = 2.dp)) {
                        val animationResource = R.raw.snowflakes
                        val compositionResult by rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(
                                animationResource
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(60.dp),
                            composition = compositionResult,
                            iterations = LottieConstants.IterateForever
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = chanceOfSnow.toString() + " %",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "Chance of snow",
                            fontSize = 16.sp,
                        )
                    }
                }
            }


        }
    }
}