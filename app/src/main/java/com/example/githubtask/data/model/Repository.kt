package com.example.githubtask.data.model

data class Repository(
    val id: Int,
    val name: String,
    val description: String?,
    val html_url: String,
    val owner: Owner
)

data class Owner(
    val login: String,
    val avatar_url: String
)
