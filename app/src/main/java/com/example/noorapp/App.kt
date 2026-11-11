package com.example.noorapp

import android.app.Application
import com.example.noorapp.core.theme.ArabicFontManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ArabicFontManager.init(this)
    }
}