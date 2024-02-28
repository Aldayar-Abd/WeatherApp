package com.example.weatherappjc.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import com.example.weatherappjc.activity.viewmodel.WeatherViewModel
import com.example.weatherappjc.core.ConnectionStatus
import com.example.weatherappjc.core.currentConnectivityStatus
import com.example.weatherappjc.core.observeConnectivityAsFlow
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun CheckNetworkConnectionScreen(
    viewModel: WeatherViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    location: MutableState<String>
) {
    val connection by checkInternetConnection()
    val isLoading by viewModel.isLoading.collectAsState()


    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(connection) {
        showDialog = if (connection != ConnectionStatus.Available) {
            true
        } else {
            viewModel.getWeather(location.value)
            false
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            title = { Text(text = "Warning") },
            text = { Text(text = "You are not connected to the internet, please connect with internet and then press Re-Check button") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        if (connection == ConnectionStatus.Available) {
                            showDialog = false
                        }
                    }
                }) {
                    Text(text = "Re-Check")
                }
            },
            dismissButton = {
                TextButton(onClick = { exitProcess(0) }) {
                    Text(text = "Exit")
                }
            }
        )
    }
}


@Composable
fun checkInternetConnection(): State<ConnectionStatus> {
    val localContext = LocalContext.current

    return produceState(initialValue = localContext.currentConnectivityStatus) {
        localContext.observeConnectivityAsFlow().collect { value = it }
    }
}