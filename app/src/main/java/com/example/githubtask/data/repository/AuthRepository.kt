package com.example.githubtask.data.repository

import com.example.githubtask.data.network.remote.ApiState

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiState
    suspend fun googleLogin(idToken: String): ApiState
    suspend fun registerUser(email: String, password: String): ApiState
}