package com.simply.schedule.network

import com.squareup.moshi.Json
import retrofit2.http.Field

data class ScheduleClass(
    val id: Long? = null,
    val subject: Subject,
    val type: ClassType,
    val teacher: Teacher?,
    @Json(name = "time_start") val timeStart: String? = null,
    @Json(name = "time_end") val timeEnd: String? = null,
    val location: String? = null,
    val owner: String? = null,
    val created: String? = null
)