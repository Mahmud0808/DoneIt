package com.drdisagree.doneit.ui.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.drdisagree.doneit.data.Database
import com.drdisagree.doneit.domain.RequestState
import com.drdisagree.doneit.domain.TaskAction
import com.drdisagree.doneit.domain.ToDoTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<RequestState<List<ToDoTask>>>
typealias Tasks = MutableState<RequestState<List<ToDoTask>>>

class HomeViewModel(private val db: Database) : ScreenModel {

    private var _activeTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val activeTasks: Tasks = _activeTasks

    private var _completedTasks: MutableTasks = mutableStateOf(RequestState.Idle)
    val completedTasks: Tasks = _completedTasks

    init {
        _activeTasks.value = RequestState.Loading
        _completedTasks.value = RequestState.Loading

        screenModelScope.launch {
            delay(500)
            db.readActiveTasks().collectLatest { result -> _activeTasks.value = result }
        }

        screenModelScope.launch {
            delay(500)
            db.readCompletedTasks().collectLatest { result -> _completedTasks.value = result }
        }
    }

    fun setAction(action: TaskAction) {
        when (action) {
            is TaskAction.SetCompleted -> setCompleted(action.task, action.completed)
            is TaskAction.SetFavorite -> setFavorite(action.task, action.isFavorite)
            is TaskAction.Delete -> deleteTask(action.task)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun setCompleted(task: ToDoTask, taskCompleted: Boolean) {
        screenModelScope.launch {
            db.setCompleted(task, taskCompleted)
        }
    }

    private fun setFavorite(task: ToDoTask, isFavorite: Boolean) {
        screenModelScope.launch {
            db.setFavorite(task, isFavorite)
        }
    }

    private fun deleteTask(task: ToDoTask) {
        screenModelScope.launch {
            db.deleteTask(task)
        }
    }
}