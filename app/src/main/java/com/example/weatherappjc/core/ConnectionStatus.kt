package com.example.weatherappjc.core

sealed class ConnectionStatus {
    object Available: ConnectionStatus()
    object UnAvailable: ConnectionStatus()
}