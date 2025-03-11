package com.prai.ptest.ai.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h1andro2d.korean.ai.view.color.MainColor
import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.MainViewState
import kotlinx.coroutines.launch

@Composable
internal fun RootView(model: MainViewModel = viewModel()) {
    val state = model.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .background(MainColor.bg)
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AnimatedVisibility(
            visible = state.value == MainViewState.LOGO,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
        ) {
            LogoView()
        }
        AnimatedVisibility(
            visible = state.value == MainViewState.TEST,
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
        ) {
            TestView()
        }
        AnimatedVisibility(
            visible = state.value == MainViewState.RESULT,
            enter = slideInVertically { y -> y },
            exit = slideOutVertically { y -> y }
        ) {
            ResultView()
        }
    }
}


@Composable
internal fun RootView2(model: MainViewModel = viewModel()) {
    val viewState = model.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(0, 0f) { 3 }

    LaunchedEffect(viewState.value) {
        val targetPage = when (viewState.value) {
            MainViewState.LOGO -> 0
            MainViewState.TEST -> 1
            MainViewState.RESULT -> 2
        }
        pagerState.animateScrollToPage(
            page = targetPage,
            animationSpec = tween(durationMillis = 500)
        )
    }

    Box(
        modifier = Modifier
            .background(MainColor.bg)
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                    },
                contentAlignment = Alignment.Center
            ) {
                when (page) {
                    0 -> LogoView()
                    1 -> TestView()
                    2 -> ResultView()
                }
            }
        }
    }
}

@Preview
@Composable
fun FullScreenViewPager() {
    val pagerState = rememberPagerState(0, 0f) { 5 }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF + (page * 10000))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Page: $page",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun ControlPagerWithCode() {
    val pagerState = rememberPagerState(0, 0f) { 5 }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF + page * 100000))
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, _ ->
                            // 드래그 무시 - 기본 스와이프 비활성화
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page: $page",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 페이지 전환을 위한 버튼
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        maxOf(0, pagerState.currentPage - 1),
                        animationSpec = tween(durationMillis = 1000)
                    )
                }
            }) {
                Text("Previous")
            }
            Button(onClick = {
                coroutineScope.launch {
                    val targetPage = when (pagerState.currentPage) {
                        4 -> 0 // 끝으로 가면 처음 페이지로
                        else -> pagerState.currentPage + 1
                    }
                    pagerState.animateScrollToPage(
                        page = targetPage,
                        animationSpec = tween(durationMillis = 500)
                    )
                }
            }) {
                Text("Next")
            }
        }
    }
}