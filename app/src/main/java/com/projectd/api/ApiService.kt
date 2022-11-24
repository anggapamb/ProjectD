package com.projectd.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): String

    @GET("tasks")
    suspend fun taskToday(): String

    @GET("projects")
    suspend fun allProject(): String

    @FormUrlEncoded
    @POST("project/add")
    suspend fun addProject(
        @Field("project_name") project_name: String?,
        @Field("description") description: String?,
        @Field("start_date") start_date: String?,
        @Field("end_date") end_date: String?,
        @Field("project_director") project_director: String?,
        @Field("difficulty") difficulty: String?
    ): String
}