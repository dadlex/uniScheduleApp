package com.simply.schedule.network

import com.squareup.moshi.Json

data class Time(
    val id: Long? = null,
    @Json(name = "class") val class_id: Long,
    val period: String?,
    @Json(name = "days_of_week") val daysOfWeek: String?,
    @Json(name = "date_start") val dateStart: String?,
    @Json(name = "date_end") val dateEnd: String?,
    @Json(name = "time_start") val timeStart: String?,
    @Json(name = "time_end") val timeEnd: String?,
    val owner: String? = null,
    val created: String? = null
)
