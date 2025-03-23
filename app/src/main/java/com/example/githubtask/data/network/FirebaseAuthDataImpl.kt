package com.example.githubtask.data.network

import com.example.githubtask.data.network.remote.ApiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataImpl(private val auth: FirebaseAuth) : FirebaseAuthData {
    override suspend fun loginWithEmail(email: String, password: String): ApiState {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            ApiState.Success(result)
        } catch (e: Exception) {
            ApiState.Failure(e.message.toString())
        }
    }

    override suspend fun loginWithGoogle(idToken: String): ApiState {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val result = auth.signInWithCredential(credential).await()
            ApiState.Success(result)
        } catch (e: Exception) {
            ApiState.Failure(e.message.toString())
        }
    }

    override suspend fun registerUser(email: String, password: String): ApiState {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                return ApiState.Failure("User is null")
            } else
                return ApiState.Success(result.user!!)
        } catch (e: Exception) {
            ApiState.Failure(e.message.toString())
        }
    }

}