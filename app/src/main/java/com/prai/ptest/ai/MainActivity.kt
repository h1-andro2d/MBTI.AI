package com.prai.ptest.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.prai.ptest.ai.view.RootView2
import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.MainViewState

class MainActivity : ComponentActivity() {
    private val transparent = Color.Transparent.toArgb()
    private val viewModel: MainViewModel by viewModels()

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