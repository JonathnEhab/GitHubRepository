package com.example.githubtask.feature.register.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registerState = MutableStateFlow<ApiState>(ApiState.Loading)
    val registerState: MutableStateFlow<ApiState> = _registerState

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = ApiState.Loading
            try {
                val result = authRepository.registerUser(email, password)
                _registerState.value = result
            } catch (e: Exception) {
                _registerState.value = ApiState.Failure(e.localizedMessage ?: "Registration failed")
            }
        }
    }
}