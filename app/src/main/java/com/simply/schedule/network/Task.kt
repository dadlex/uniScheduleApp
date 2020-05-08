package com.simply.schedule.network

import com.squareup.moshi.Json
import retrofit2.http.Field

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String?,
    val priority: Int?,
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "class") val class_: Class? = null,
    @Json(name = "due_date") val dueDate: String? = null,
    @Json(name = "completed_at") val completedAt: String? = null,
    val owner: String? = null,
    val created: String? = null
)