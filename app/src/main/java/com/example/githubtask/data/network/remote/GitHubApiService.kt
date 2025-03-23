package com.example.githubtask.data.network.remote

import com.example.githubtask.data.model.Repository
import com.example.githubtask.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("repositories")
    suspend fun getPublicRepositories(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int
    ): List<Repository>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>
}