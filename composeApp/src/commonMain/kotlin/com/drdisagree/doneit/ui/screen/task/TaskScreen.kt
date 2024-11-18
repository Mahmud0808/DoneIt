package com.drdisagree.doneit.ui.screen.task

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.drdisagree.doneit.domain.TaskAction
import com.drdisagree.doneit.domain.ToDoTask

data class TaskScreen(val task: ToDoTask? = null) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TaskViewModel>()
        var currentTitle by remember { mutableStateOf(task?.title ?: "") }
        var currentDescription by remember { mutableStateOf(task?.description ?: "") }
        val titleInteractionSource = remember { MutableInteractionSource() }
        val descriptionInteractionSource = remember { MutableInteractionSource() }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            singleLine = true,
                            value = currentTitle,
                            onValueChange = { currentTitle = it },
                            interactionSource = titleInteractionSource,
                            decorationBox = @Composable { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = currentTitle,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = titleInteractionSource,
                                    placeholder = {
                                        Text(
                                            text = "Enter task title",
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                            modifier = Modifier.alpha(0.5f)
                                        )
                                    },
                                    colors = transparentTextField()
                                )
                            },
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotEmpty() && currentDescription.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.setAction(
                                if (task != null) {
                                    TaskAction.Update(
                                        ToDoTask().apply {
                                            _id = task._id
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                } else {
                                    TaskAction.Add(
                                        ToDoTask().apply {
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                }
                            )
                            navigator.pop()
                        },
                        shape = RoundedCornerShape(size = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Save"
                        )
                    }
                }
            }
        ) { paddingValues ->
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(all = 24.dp),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                ),
                value = currentDescription,
                onValueChange = { currentDescription = it },
                interactionSource = descriptionInteractionSource,
                decorationBox = @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = currentDescription,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = false,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = descriptionInteractionSource,
                        placeholder = {
                            Text(
                                text = "Enter task description",
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                modifier = Modifier.alpha(0.5f)
                            )
                        },
                        colors = transparentTextField()
                    )
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun transparentTextField() = TextFieldDefaults.colors().copy(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent
)