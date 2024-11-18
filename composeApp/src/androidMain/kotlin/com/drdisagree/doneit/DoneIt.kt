package com.drdisagree.doneit

import android.app.Application
import com.drdisagree.doneit.di.initializeKoin

class DoneIt : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeKoin()
    }
}