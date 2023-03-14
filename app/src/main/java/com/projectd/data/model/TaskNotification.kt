package com.projectd.data.model

import com.google.gson.annotations.SerializedName

data class TaskNotification(
    @SerializedName("id_task")
    val idTask: Int?,
    @SerializedName("id_user")
    val idUser: String?,
    @SerializedName("project")
    val project: String?,
    @SerializedName("task_name")
    val taskName: String?,
    @SerializedName("project_name")
    val projectName: String?
)