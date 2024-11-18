package com.drdisagree.doneit

import androidx.compose.ui.window.ComposeUIViewController
import com.drdisagree.doneit.di.initializeKoin

fun MainViewController() = ComposeUIViewController {
    initializeKoin()
    val isDarkTheme =
        UIScreen.mainScreen.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark

    App(
        darkTheme = isDarkTheme,
        dynamicColor = false,
    )
}