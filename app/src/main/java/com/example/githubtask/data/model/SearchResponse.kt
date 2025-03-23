package com.example.githubtask.data.model

data class SearchResponse(
    val items: List<Repository>,
    val totalCount: Int
)
