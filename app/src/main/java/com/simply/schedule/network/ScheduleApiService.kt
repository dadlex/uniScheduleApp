package com.simply.schedule.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "http://161.35.134.199"

private val client = OkHttpClient().newBuilder().addInterceptor { chain ->
    val request = chain.request().newBuilder()
        .header("Authorization", ScheduleApi.credentials)
        .build()
    chain.proceed(request)
}.build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .build()

interface ScheduleApiService {
    @GET("subjects/")
    fun getSubjects(): Call<List<Subject>>

    @POST("subjects/")
    fun createSubject(@Body subject: Subject): Call<Subject>

    @GET("class-types/")
    fun getClassTypes(): Call<List<ClassType>>

    @POST("class-types/")
    fun createClassType(@Body classType: ClassType): Call<ClassType>

    @GET("teachers/")
    fun getTeachers(): Call<List<Teacher>>

    @GET("teachers/{id}/")
    fun getTeacher(@Path("id") id: Long): Call<Teacher>

    @POST("teachers/")
    fun createTeacher(@Body teacher: Teacher): Call<Teacher>

    @PUT("teachers/{id}/")
    fun updateTeacher(@Path("id") id: Long, @Body teacher: Teacher): Call<Teacher>

    @DELETE("teachers/{id}/")
    fun deleteTeacher(@Path("id") id: Long): Call<Teacher>

    @GET("classes/{date}")
    fun getClasses(@Path("date") date: LocalDate): Call<ScheduleClass>

    @POST("classes/")
    fun createClass(@Body class_: Class): Call<Class>

    @PUT("classes/{id}/")
    fun updateClass(@Path("id") id: Long, @Body class_: Class): Call<Class>

    @DELETE("classes/{id}/")
    fun deleteClass(@Path("id") id: Long): Call<Class>

    @POST("times/")
    fun createTime(@Body time: Time): Call<Time>

    @GET("schedule/{date}")
    fun getSchedule(@Path("date") date: LocalDate): Call<List<ScheduleClass>>

    @GET("tasks/")
    fun getTasks(): Call<List<Task>>

    @POST("tasks/")
    fun createTask(@Body task: Task): Call<Task>

    @PUT("tasks/{id}/")
    fun updateTask(@Path("id") id: Long, @Body task: Task): Call<Task>

    @DELETE("tasks/{id}/")
    fun deleteTask(@Path("id") id: Long): Call<Task>

    @POST("users/")
    fun createUser(@Body user: User): Call<User>
}

object ScheduleApi {
    val retrofitService: ScheduleApiService by lazy { retrofit.create(ScheduleApiService::class.java) }
    var credentials: String = ""
}
