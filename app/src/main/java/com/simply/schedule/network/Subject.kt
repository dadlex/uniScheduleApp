package com.simply.schedule.network

data class Subject(
    val id: Long? = null,
    val title: String,
    val color: String,
    val owner: String? = null,
    val created: String? = null
)