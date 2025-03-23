package com.example.githubtask.feature.home.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubtask.data.model.Repository
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.network.remote.RetrofitClient
import com.example.githubtask.data.repository.GithubRepositoryImpl
import com.example.githubtask.databinding.ActivityHomeBinding
import com.example.githubtask.feature.home.viewModel.HomeViewModel
import com.example.githubtask.utils.Constants.REQUEST_CODE_WIFI
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repositoryAdapter: RepositoryAdapter
    private var isLoading = false
    private var lastVisibleItem = 0
    private val perPage = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        initViewModel()
        setupRecyclerView()
        observeRepositories()
        setupSearchView()
        observeSearchResults()

        homeViewModel.fetchRepositories(perPage = perPage)
    }
    private fun initViewModel() {
        homeViewModel = HomeViewModel(GithubRepositoryImpl(RetrofitClient.api))
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    homeViewModel.searchRepositories(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    repositoryAdapter.clearRepositories()
                    homeViewModel.fetchRepositories(since = 0, perPage = perPage)
                }
                return false
            }
        })
    }

    private fun observeRepositories() {
        lifecycleScope.launch {
            homeViewModel.repositories.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success<*> -> {
                        showLoading(false)
                        handleSuccess(state)
                    }

                    is ApiState.Failure -> {
                        showLoading(false)
                        handleError(message = state.message)
                    }
                }
                isLoading = false
            }
        }
    }

    private fun observeSearchResults() {
        lifecycleScope.launch {
            homeViewModel.searchResults.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        showLoading(true)
                    }

                    is ApiState.Success<*> -> {
                        showLoading(false)
                        if (state.data is List<*>) {
                            repositoryAdapter.setRepositories(state.data as List<Repository>)
                        }
                    }

                    is ApiState.Failure -> {
                        showLoading(false)
                        handleError(message = state.message)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleSuccess(state: ApiState.Success<*>) {
        if (state.data is List<*>) {
            @Suppress("UNCHECKED_CAST")
            repositoryAdapter.addRepositories(state.data as List<Repository>)
        }
    }

    private fun handleError(message: String) {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val isWifiConnected =
            activeNetwork?.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected

        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { dialog, _ ->
                if (isWifiConnected) {
                    homeViewModel.fetchRepositories(perPage = perPage)
                    dialog.dismiss()
                } else {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivityForResult(intent, REQUEST_CODE_WIFI)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    @Deprecated("This method has been deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WIFI) {
            // Check if Wi-Fi is connected after the user returns from the Wi-Fi settings
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            val isWifiConnected =
                activeNetwork?.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected

            if (isWifiConnected) {
                homeViewModel.fetchRepositories(perPage = perPage)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvRepos.layoutManager = LinearLayoutManager(this)
        repositoryAdapter = RepositoryAdapter(mutableListOf())
        binding.rvRepos.adapter = repositoryAdapter

        binding.rvRepos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handlePagination()
            }
        })
    }

    private fun handlePagination() {
        val layoutManager = binding.rvRepos.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val visibleItemCount = layoutManager.childCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
            isLoading = true
            if (firstVisibleItemPosition > lastVisibleItem) {
                lastVisibleItem = firstVisibleItemPosition
                homeViewModel.fetchRepositories(perPage = perPage)
            }
        }
    }
}