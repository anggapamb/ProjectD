package com.projectd.ui.task.list

import com.crocodic.core.helper.DateTimeHelper
import com.projectd.api.ApiService
import com.projectd.base.viewmodel.BaseViewModel
import com.projectd.data.model.TaskDay
import java.util.*

class TaskViewModel(private val apiService: ApiService): BaseViewModel() {

    fun generate30Days(): List<TaskDay?> {
        val days = ArrayList<TaskDay?>()

        val today = Calendar.getInstance().apply {
            add(Calendar.DATE, -15)
        }

        val maxDay = (today.clone() as Calendar).apply {
            add(Calendar.DATE, 30)
        }

        while (today.before(maxDay)) {
            days.add(TaskDay(DateTimeHelper().fromDate(today.time), DateTimeHelper().fromDate(today.time) == DateTimeHelper().dateNow()))
            today.add(Calendar.DATE, 1)
        }

        return days
    }

}