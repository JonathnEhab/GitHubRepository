package com.example.githubtask.feature.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginState = MutableStateFlow<ApiState>(ApiState.Loading)
    val loginState: MutableStateFlow<ApiState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiState.Loading
            _loginState.value = authRepository.login(email, password)
        }
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _loginState.value = ApiState.Loading
            _loginState.value = authRepository.googleLogin(idToken)
        }
    }
}