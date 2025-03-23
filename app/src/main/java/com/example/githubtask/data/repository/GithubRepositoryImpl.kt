package com.example.githubtask.data.repository

import com.example.githubtask.data.network.remote.ApiState
import com.example.githubtask.data.network.remote.GitHubApiService

class GithubRepositoryImpl(private val gitHubApiService: GitHubApiService) : GithubRepository {
    override suspend fun getPublicRepositories(since: Int, perPage: Int): ApiState {
        return try {
            val repos = gitHubApiService.getPublicRepositories(since, perPage)
            ApiState.Success(repos)
        } catch (e: Exception) {
            ApiState.Failure(e.localizedMessage ?: "Error fetching repositories")
        }
    }

    override suspend fun searchRepositories(query: String, perPage: Int, page: Int): ApiState {
        return try {
            val response = gitHubApiService.searchRepositories(query, perPage, page)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiState.Success(it.items)
                } ?: ApiState.Failure("No repositories found")
            } else {
                ApiState.Failure("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            ApiState.Failure(e.localizedMessage ?: "Error fetching repositories")
        }
    }
}