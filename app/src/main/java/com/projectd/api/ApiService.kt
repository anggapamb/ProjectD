package com.projectd.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun allProject(
        @Query("page") page: Int
    ): String

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

    @FormUrlEncoded
    @POST("http://206.189.40.49:8111/project/update/{id}")
    suspend fun updateProject(
        @Path("id") idProject: String?,
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

    @GET("application-menus")
    suspend fun addMenus(): String

    @FormUrlEncoded
    @POST("absent/add")
    suspend fun sendAbsent(
        @Field("name") name: String?,
        @Field("reason") reason: String?,
        @Field("id_login") id_login: String?
    ): String

    @POST("absent/list-login-today")
    suspend fun getAbsent(): String

    @GET("absents")
    suspend fun getListAllAbsent(): String

    @FormUrlEncoded
    @POST("absent/update-approved/{idAbsent}")
    suspend fun approvedAbsent(
        @Path("idAbsent") idAbsent: String,
        @Field("approved") approved: String
    ): String

    @FormUrlEncoded
    @POST("task/date")
    suspend fun taskByDate(
        @Field("date") date: String
    ): String

    @FormUrlEncoded
    @POST("project/search")
    suspend fun searchProject(
        @Field("projectName") projectName: String?
    ): String

    @GET("show/doa")
    suspend fun showPrayer(): String

    @GET("tasks/list-user-not-input-task-today")
    suspend fun userNotReady(): String
}