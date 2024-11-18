package com.drdisagree.doneit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.drdisagree.doneit.ui.components.AppTheme
import com.drdisagree.doneit.ui.screen.home.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Navigator(HomeScreen()) {
            SlideTransition(it)
        }
    }
}
