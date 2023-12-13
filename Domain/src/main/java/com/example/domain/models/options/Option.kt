package com.example.domain.models.options

data class Option(
    val child: Boolean,
    val id: Int,
    val name: String,
    val parent: Int,
    val slug: String
)