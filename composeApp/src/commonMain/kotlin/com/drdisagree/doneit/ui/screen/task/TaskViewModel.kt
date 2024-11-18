package com.drdisagree.doneit.ui.screen.task

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.drdisagree.doneit.data.Database
import com.drdisagree.doneit.domain.TaskAction
import com.drdisagree.doneit.domain.ToDoTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class TaskViewModel(private val db: Database) : ScreenModel {

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.Add -> addTask(action.task)
            is TaskAction.Update -> updateTask(action.task)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun addTask(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            db.addTask(task)
        }
    }

    private fun updateTask(task: ToDoTask) {
        screenModelScope.launch(Dispatchers.IO) {
            db.updateTask(task)
        }
    }
}