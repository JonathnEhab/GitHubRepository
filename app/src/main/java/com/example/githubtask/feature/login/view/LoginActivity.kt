@file:Suppress("DEPRECATION")

package com.example.githubtask.feature.login.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.githubtask.R
import com.example.githubtask.data.network.FirebaseAuthDataImpl
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.repository.AuthRepositoryImpl
import com.example.githubtask.databinding.ActivityLoginBinding
import com.example.githubtask.feature.home.view.HomeActivity
import com.example.githubtask.feature.login.viewModel.LoginViewModel
import com.example.githubtask.feature.register.view.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInRequestCode = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        initViewModel()
        initGoogleSignInClient()
        observeLoginState()
        setupListeners()
    }

    private fun initViewModel() {
        viewModel = LoginViewModel(
            AuthRepositoryImpl(
                FirebaseAuthDataImpl(FirebaseAuth.getInstance())
            )
        )
    }

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener { handleLoginClick() }
        binding.txtRegister.setOnClickListener { navigateToRegisterScreen() }
        binding.signInGoogleButton.setOnClickListener { signInWithGoogle() }
    }

    private fun handleLoginClick() {
        val email = binding.emailLoginEditText.text.toString()
        val password = binding.passwordLoginEditText.text.toString()

        binding.textField.error = null
        binding.passwordField.error = null

        if (validateInputs(email, password)) {
            binding.btnSignIn.startAnimation()
            viewModel.login(email, password)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textField.error = "Please enter a valid email address"
            isValid = false
        }
        if (password.length < 6) {
            binding.passwordField.error = "Password must be at least 6 characters"
            isValid = false
        }
        return isValid
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, googleSignInRequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleSignInRequestCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.googleLogin(idToken)
                navigateToHomeScreen()
            } else {
                showError("Google Sign-In failed: ID Token is null")
            }
        } catch (e: ApiException) {
            showError("Google Sign-In failed: ${e.message}")
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {}
                    is ApiState.Success<*> -> navigateToHomeScreen()
                    is ApiState.Failure -> showError(state.message)
                }
            }
        }
    }

    private fun navigateToRegisterScreen() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToHomeScreen() {
        binding.btnSignIn.stopAnimation()
        binding.btnSignIn.revertAnimation()
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showError(message: String) {
        binding.btnSignIn.revertAnimation()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}