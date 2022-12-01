package com.projectd.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
        @Field("difficulty") difficulty: String?,
        @Field("createdBy") createdBy: String?,
    ): String

    @GET("users/managers")
    suspend fun managers(): String

    @FormUrlEncoded
    @POST("task/add")
    suspend fun addTask(
        @Field("task_name") task_name: String?,
        @Field("project") project: String?,
        @Field("start_date") start_date: String?,
        @Field("end_date") end_date: String?,
        @Field("load") load: String?,
        @Field("createdBy") createdBy: String?,
        @Field("photo") photo: String?
    ): String

    @FormUrlEncoded
    @POST("task/update-status/{idTask}")
    suspend fun updateTask(
        @Path("idTask") idTask: String?,
        @Field("status") status: String?,
        @Field("description") description: String?
    ): String

    @FormUrlEncoded
    @POST("task/update-verified/{idTask}")
    suspend fun verifyTask(
        @Path("idTask") idTask: String?,
        @Field("token") token: String?
    ): String
}