package com.example.githubtask.data.network.remote

sealed class ApiState {
    class Success<T>(val data: T) : ApiState()
    class Failure(val message: String, val field: String? = null) : ApiState()
    object Loading : ApiState()
}