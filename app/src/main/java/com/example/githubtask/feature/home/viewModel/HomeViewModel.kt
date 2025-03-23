package com.example.githubtask.feature.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val gitHubRepo: GithubRepository) : ViewModel() {
    private val _repositories = MutableStateFlow<ApiState>(ApiState.Loading)
    val repositories: MutableStateFlow<ApiState> = _repositories

    private val _searchResults = MutableStateFlow<ApiState>(ApiState.Loading)
    val searchResults: MutableStateFlow<ApiState> = _searchResults

    private var currentPage = 0

    fun fetchRepositories(since: Int = currentPage, perPage: Int) {
        viewModelScope.launch {
            _repositories.value = ApiState.Loading
            when (val result = gitHubRepo.getPublicRepositories(since, perPage)) {
                is ApiState.Success<*> -> {
                    _repositories.value = ApiState.Success(result.data)
                    currentPage = since + perPage
                }

                is ApiState.Failure -> {
                    _repositories.value = ApiState.Failure(result.message)
                }

                else -> {}
            }
        }
    }

    fun searchRepositories(query: String, perPage: Int = 50, page: Int = 1) {
        viewModelScope.launch {
            when (val result = gitHubRepo.searchRepositories(query, perPage, page = page)) {
                is ApiState.Success<*> -> {
                    _searchResults.value = ApiState.Success(result.data)
                }

                is ApiState.Failure -> {
                    _searchResults.value = ApiState.Failure(result.message)
                }

                else -> {}
            }
        }
    }
}