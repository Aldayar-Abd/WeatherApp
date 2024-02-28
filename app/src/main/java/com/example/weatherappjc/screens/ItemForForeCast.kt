package com.example.weatherappjc.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherappjc.R
import com.example.weatherappjc.data.model.ForecastModel
import com.example.weatherappjc.core.weatherAnimation
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ItemForForeCast(model: ForecastModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .background(Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(24.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val outputFormat = SimpleDateFormat("EE-dd", Locale.ENGLISH)

                val date = inputFormat.parse(model.date)
                val formattedDate = date?.let { outputFormat.format(it) }

                Text(
                    text = formattedDate!!,
                    modifier = Modifier.padding(start = 15.dp, end = 40.dp),
                    fontWeight = FontWeight.Thin,
                    fontSize = 18.sp
                )

                val weatherText = model.weather.toString()
                val animationResource =
                    weatherAnimation[weatherText] ?: R.raw.loading
                val compositionResult by rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(
                        animationResource
                    )
                )

                LottieAnimation(
                    composition = compositionResult,
                    modifier = Modifier.size(64.dp),
                    iterations = LottieConstants.IterateForever
                )
                Text(
                    text = model.weather,
                    modifier = Modifier.padding(start = 20.dp),
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                Text(
                    text = "max temp:",
                    modifier = Modifier.padding(start = 15.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Thin
                )
                Text(
                    text = "+" + model.tempMax + "°C",
                    modifier = Modifier.padding(start = 5.dp, end = 4.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "min temp:",
                    modifier = Modifier.padding(start = 28.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Thin
                )
                Text(
                    text = "+" + model.tempMin + "°C",
                    modifier = Modifier.padding(start = 5.dp, end = 4.dp),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}