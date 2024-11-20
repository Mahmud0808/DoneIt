package com.drdisagree.doneit.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.drdisagree.doneit.domain.RequestState
import com.drdisagree.doneit.domain.TaskAction
import com.drdisagree.doneit.domain.ToDoTask
import com.drdisagree.doneit.ui.components.ErrorScreen
import com.drdisagree.doneit.ui.components.LoadingScreen
import com.drdisagree.doneit.ui.components.TaskView
import com.drdisagree.doneit.ui.components.bottomFadingEdge
import com.drdisagree.doneit.ui.components.topFadingEdge
import com.drdisagree.doneit.ui.screen.task.TaskScreen
import doneit.composeapp.generated.resources.Res
import doneit.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()
        val activeTasks by viewModel.activeTasks
        val completedTasks by viewModel.completedTasks

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.app_name)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(TaskScreen()) },
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(top = 24.dp)
            ) {
                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = activeTasks,
                    onSelect = { task -> navigator.push(TaskScreen(task)) },
                    onFavorite = { task, favorite ->
                        viewModel.setAction(
                            action = TaskAction.SetFavorite(task, favorite)
                        )
                    },
                    onComplete = { task, completed ->
                        viewModel.setAction(
                            action = TaskAction.SetCompleted(task, completed)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = completedTasks,
                    showActive = false,
                    onComplete = { task, completed ->
                        viewModel.setAction(
                            action = TaskAction.SetCompleted(task, completed)
                        )
                    },
                    onDelete = { task ->
                        viewModel.setAction(
                            action = TaskAction.Delete(task)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun DisplayTasks(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<ToDoTask>>,
    showActive: Boolean = true,
    onSelect: ((ToDoTask) -> Unit)? = null,
    onFavorite: ((ToDoTask, Boolean) -> Unit)? = null,
    onComplete: ((ToDoTask, Boolean) -> Unit)? = null,
    onDelete: ((ToDoTask) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = if (showActive) "Active Tasks" else "Completed Tasks",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        tasks.DisplayResult(
            onLoading = { LoadingScreen() },
            onError = { ErrorScreen(message = it) },
            onSuccess = {
                if (it.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .topFadingEdge(color = MaterialTheme.colorScheme.surface)
                            .bottomFadingEdge(color = MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 24.dp)
                    ) {
                        items(
                            items = it,
                            key = { task -> task._id.toHexString() }
                        ) { task ->
                            TaskView(
                                task = task,
                                showActive = showActive,
                                onSelect = { onSelect?.invoke(task) },
                                onComplete = { selectedTask, completed ->
                                    onComplete?.invoke(
                                        selectedTask,
                                        completed
                                    )
                                },
                                onFavorite = { selectedTask, favorite ->
                                    onFavorite?.invoke(
                                        selectedTask,
                                        favorite
                                    )
                                },
                                onDelete = { selectedTask ->
                                    taskToDelete = selectedTask
                                    showDialog = true
                                }
                            )
                        }
                    }
                } else {
                    ErrorScreen(message = "No tasks found")
                }
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            title = {
                Text(
                    text = "Delete",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${taskToDelete?.title}'?",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete?.invoke(taskToDelete!!)
                        taskToDelete = null
                        showDialog = false
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = {
                taskToDelete = null
                showDialog = false
            }
        )
    }
}