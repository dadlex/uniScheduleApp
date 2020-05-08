package com.simply.schedule.network

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Long? = null,
    val name: String,
    val phone: String,
    val email: String,
    val owner: String? = null,
    val created: String? = null
)