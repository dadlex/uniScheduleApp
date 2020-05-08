package com.simply.schedule.network

data class Class(
    val id: Long? = null,
    val subject: Subject,
    val type: ClassType,
    val teacher: Teacher?,
    val location: String?,
    val owner: String? = null,
    val created: String? = null
)