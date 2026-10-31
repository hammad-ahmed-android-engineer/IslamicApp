package com.ne.muslimprayers.qiblafinder

import android.app.Application
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ArabicFontManager.init(this)
    }
}