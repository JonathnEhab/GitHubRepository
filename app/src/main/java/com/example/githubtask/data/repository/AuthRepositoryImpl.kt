package com.example.githubtask.data.repository

import com.example.githubtask.data.network.FirebaseAuthData
import com.example.githubtask.data.network.remote.ApiState

class AuthRepositoryImpl(private val firebaseAuthData: FirebaseAuthData) : AuthRepository {
    override suspend fun login(email: String, password: String): ApiState {
        return firebaseAuthData.loginWithEmail(email, password)
    }

    override suspend fun googleLogin(idToken: String): ApiState {
        return firebaseAuthData.loginWithGoogle(idToken)
    }

    override suspend fun registerUser(email: String, password: String): ApiState {
        return firebaseAuthData.registerUser(email, password)
    }
}