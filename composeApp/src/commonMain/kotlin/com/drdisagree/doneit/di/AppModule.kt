package com.drdisagree.doneit.di

import com.drdisagree.doneit.data.Database
import com.drdisagree.doneit.ui.screen.home.HomeViewModel
import com.drdisagree.doneit.ui.screen.task.TaskViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Database() }
    factory { HomeViewModel(get()) }
    factory { TaskViewModel(get()) }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}