package com.prai.ptest.ai

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.prai.ptest.ai.view.RootView2
import com.prai.ptest.ai.view.api.ApiClient
import com.prai.ptest.ai.view.api.MainApi
import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.MainViewState

class MainActivity : ComponentActivity() {
    private val transparent = Color.Transparent.toArgb()
    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(transparent, transparent),
            navigationBarStyle = SystemBarStyle.light(transparent, transparent)
        )
        setContent {
            RootView2()
        }
        onBackPressedDispatcher.addCallback(this, BackPressCallback())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ApiClient.init()
    }

    private inner class BackPressCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (viewModel.state.value) {
                MainViewState.LOGO -> finish()
                MainViewState.TEST -> viewModel.onBackClicked()
                MainViewState.RESULT -> viewModel.goHome()
            }
        }
    }
}