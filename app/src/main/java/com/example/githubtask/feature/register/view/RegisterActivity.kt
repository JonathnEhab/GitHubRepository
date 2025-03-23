package com.example.githubtask.feature.register.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.githubtask.data.network.FirebaseAuthDataImpl
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.repository.AuthRepositoryImpl
import com.example.githubtask.databinding.ActivityRegisterBinding
import com.example.githubtask.feature.login.view.LoginActivity
import com.example.githubtask.feature.register.viewModel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        initializeViewModel()
        setupListeners()
        observeViewModel()
    }

    private fun initializeViewModel() {
        val authRepository = AuthRepositoryImpl(FirebaseAuthDataImpl(FirebaseAuth.getInstance()))
        registerViewModel = RegisterViewModel(authRepository)
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener { handleRegisterButtonClick() }
        binding.imgBack.setOnClickListener { handleBackButtonClick() }
    }

    private fun handleRegisterButtonClick() {
        val email = binding.emailLoginEditText.text.toString()
        val password = binding.passwordLoginEditText.text.toString()
        val confirmPassword = binding.confirmPasswordRegisterEditText.text.toString()

        clearErrorMessages()

        if (isInputValid(email, password, confirmPassword)) {
            binding.btnRegister.startAnimation()
            registerViewModel.register(email, password)
        }
    }

    private fun handleBackButtonClick() = onBackPressedDispatcher.onBackPressed()

    private fun observeViewModel() {
        lifecycleScope.launch {
            registerViewModel.registerState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {}
                    is ApiState.Success<*> -> handleSuccessState()
                    is ApiState.Failure -> handleErrorState(state.message, state.field)
                }
            }
        }
    }

    private fun handleSuccessState() {
        binding.btnRegister.stopAnimation()
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        navigateToLoginScreen()
    }

    private fun handleErrorState(message: String, field: String? = null) {
        binding.btnRegister.revertAnimation()
        clearErrorMessages()

        when (field) {
            "email" -> binding.textField.error = message
            "password" -> binding.passwordField.error = message
            "confirmPassword" -> binding.confirmPasswordField.error = message
            else -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearErrorMessages() {
        binding.textField.error = null
        binding.passwordField.error = null
        binding.confirmPasswordField.error = null
    }

    private fun isInputValid(email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (!isValidEmail(email) || email.isEmpty()) {
            binding.textField.error = "Please enter a valid email address."
            isValid = false
        }

        if (!isValidPassword(password)) {
            binding.passwordField.error = "Password must be at least 6 characters long."
            isValid = false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordField.error = "Passwords do not match."
            isValid = false
        }
        return isValid
    }

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}