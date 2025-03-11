package com.prai.ptest.ai.view

import android.content.Context
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h1andro2d.korean.ai.view.color.MainColor
import com.prai.ptest.ai.R
import com.prai.ptest.ai.view.api.ApiClient
import com.prai.ptest.ai.view.api.PersonalityRequest
import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.MainViewState
import com.prai.ptest.ai.view.model.TestResult
import java.text.BreakIterator
import java.text.StringCharacterIterator
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.delay

@Composable
internal fun ResultView(model: MainViewModel = viewModel()) {
    val state = model.state.collectAsStateWithLifecycle()
    var textVisible by remember { mutableStateOf(false) }
    val result = model.calculateResult()
    val context = LocalContext.current
    val isKorean = isKoreanLanguage(context)
    LaunchedEffect(state.value) {
        if (state.value == MainViewState.RESULT) {
            val request = PersonalityRequest(
                result.type, mapOf(
                    "E/I" to result.ei,
                    "N/S" to result.sn,
                    "F/T" to result.tf,
                    "J/P" to result.jp,
                    "안정성/신경성" to result.nscore
                ),
                language = if (isKorean) "ko" else "en"
            )
            ApiClient.updateRequest(request)
            delay(1000L)
            textVisible = true
        } else {
            delay(2000L)
            textVisible = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FF))
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(bottom = 100.dp)
                    .height(0.5.dp)
                    .fillMaxWidth()
                    .background(color = Color.Black)
                    .align(Alignment.BottomCenter)

            )
            Row(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RetryText()
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(R.drawable.dolphin),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .width(150.dp)
                )
            }
            item {
                Box(modifier = Modifier.height(150.dp)) {
                    AnimatedVisibility(
                        visible = textVisible,
                        enter = fadeIn(animationSpec = tween(2000))
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                start = 30.dp,
                                end = 30.dp,
                                bottom = 30.dp,
                                top = 20.dp
                            )
                        ) {
                            Text(
                                result.type,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 50.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                stringResource(
                                    model.createTagResource(result.type) ?: R.string.xlt_empty
                                ),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .background(color = Color(0x2299AAFF), shape = RoundedCornerShape(20.dp))
                        .border(
                            width = 0.5.dp,
                            color = Color(0xFF99AAFF),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val ei = (result.ei.toFloat() / 100)
                    val sn = (result.sn.toFloat() / 100)
                    val ft = (result.ei.toFloat() / 100)
                    val jp = (result.jp.toFloat() / 100)
                    Bar("I", "E", ei, result.ei > 50, 2000L)
                    Bar("N", "S", sn, result.sn <= 50, 4000L)
                    Bar("F", "T", ft, result.tf <= 50, 6000L)
                    Bar("P", "J", jp, result.jp > 50, 8000L)
                }
            }
//            item {
//                Image(
//                    painter = painterResource(R.drawable.finish_text),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .clickable {
//                            model.state.value = MainViewState.LOGO
//                        }
//                        .padding(top = 30.dp)
//                        .width(250.dp)
//                )
//            }
            item {
                TypingAnimationTitle(stringResource(R.string.xlt_ai_report_title))
                TypingAnimationTextContent(result)
//                Text(
//                    "통솔자(ENTJ)는 타고난 리더라고 할 수 있습니다. 이들은 카리스마와 자신감을 지니고 있으며 자신의 권한을 이용해 사람들이 공통된 목표를 위해 함께 노력하도록 이끕니다. 또한 이들은 냉철한 이성을 지닌 것으로 유명하며 자신이 원하는 것을 성취하기 위해 열정과 결단력과 날카로운 지적 능력을 활용합니다. 이들은 전체 인구의 3%에 불과하지만, 다른 많은 성격을 압도하는 존재감을 뽐내며 다양한 비즈니스와 단체를 이끄는 역할을 할 때가 많습니다.",
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium,
//                    lineHeight = 25.sp,
//                    modifier = Modifier
//                        .padding(horizontal = 30.dp)
//                        .fillMaxWidth()
//                )
            }
        }
        Box(
            modifier = Modifier
                .padding(bottom = 100.dp)
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f)
                        )
                    )
                )
        )
    }
}

@Composable
private fun Bar(
    left: String,
    right: String,
    progress: Float,
    red: Boolean,
    delay: Long,
    model: MainViewModel = viewModel()
) {
    val state = model.state.collectAsStateWithLifecycle()
    val input = when {
        state.value == MainViewState.RESULT -> {
            if (progress > 0.5f) {
                progress
            } else {
                1f - progress
            }
        }

        else -> {
            0f
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(5.dp)
    ) {
        Text(
            left,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (red.not()) {
                Color(0xFF6677FF)
            } else {
                Color(0x336677FF)
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.width(20.dp)
        )
        Box(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (red) {
                IndicatorRed(input, delay)
            } else {
                IndicatorBlue(input, delay)
            }
        }
        Text(
            right,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (red) {
                Color(0xFFD65BA6)
            } else {
                Color(0x33D65BA6)
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
private fun IndicatorBlue(progress: Float, delay: Long, model: MainViewModel = viewModel()) {
    val animatedProgress by remember { mutableStateOf(Animatable(initialValue = 0f)) }
    val state = model.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value) {
        if (state.value == MainViewState.RESULT) {
            delay(delay)
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(durationMillis = 2000)
            )
        }
    }
    LinearProgressIndicator(
        progress = { 1.0f },
        modifier = Modifier.height(5.dp),
        color = Color(0x33889BEA),
        drawStopIndicator = {},
        gapSize = 0.dp
    )
    LinearProgressIndicator(
        progress = { animatedProgress.value },
        modifier = Modifier
            .height(5.dp),
        color = Color(0xFF889BEA),
        trackColor = Color.Transparent,
        drawStopIndicator = {},
        gapSize = 0.dp
    )
    Text(
        "${abs(animatedProgress.value * 100).toInt()}%",
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                color = Color(0x99000000),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(3.dp)
    )
}

@Composable
private fun IndicatorRed(progress: Float, delay: Long, model: MainViewModel = viewModel()) {
    val state = model.state.collectAsStateWithLifecycle()
    val animatedProgress by remember { mutableStateOf(Animatable(initialValue = 0f)) }

    LaunchedEffect(state.value) {
        if (state.value == MainViewState.RESULT) {
            delay(delay)
            animatedProgress.animateTo(
                targetValue = progress,
                animationSpec = tween(durationMillis = 2000)
            )
        }
    }
    LinearProgressIndicator(
        progress = { 1.0f },
        modifier = Modifier
            .height(5.dp),
        color = Color(0x33C25BA6),
        drawStopIndicator = {},
        gapSize = 0.dp
    )
    LinearProgressIndicator(
        progress = { 1f - animatedProgress.value },
        modifier = Modifier
            .height(5.dp),
        color = Color.Transparent,
        trackColor = Color(0xBBC25BA6),
        drawStopIndicator = {},
        gapSize = 0.dp
    )
    Text(
        "${abs(animatedProgress.value * 100).toInt()}%",
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(
                color = Color(0x99000000),
                shape = RoundedCornerShape(5.dp)
            )
            .padding(3.dp)
    )
}

@Composable
private fun ColorAnimationText() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else MainColor.bg, label = ""
    )

    val boxColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF889BFF) else MainColor.fontMain, label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 70.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
            }
            .background(color = boxColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 50.dp, vertical = 20.dp)
    ) {
        Text(
            text = "AI 기반 성격 검사 완료",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun TypingAnimationTitle(text: String, model: MainViewModel = viewModel()) {
    val state = model.state.collectAsStateWithLifecycle()
    val breakIterator = remember(text) { BreakIterator.getCharacterInstance() }
    val typingDelayInMs = 50L
    var substringText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(state.value) {
        if (state.value == MainViewState.RESULT) {
            delay(10000)
            breakIterator.text = StringCharacterIterator(text)
            var nextIndex = breakIterator.next()
            while (nextIndex != BreakIterator.DONE) {
                substringText = text.subSequence(0, nextIndex).toString()
                nextIndex = breakIterator.next()
                delay(typingDelayInMs)
            }
        }
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Text(
            text = substringText,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, bottom = 10.dp)
                .fillMaxWidth()
        )
    }
}

fun isKoreanLanguage(context: Context): Boolean {
    val locale: Locale =
        context.resources.configuration.locales.get(0)

    return locale.language == "ko"
}

@Composable
private fun TypingAnimationTextContent(result: TestResult, model: MainViewModel = viewModel()) {
    val response = model.response.collectAsStateWithLifecycle()
    val input: String = if (response.value != null && result.type == response.value?.mbti_type) {
        response.value?.analysis ?: stringResource(R.string.xlt_main_network_error)
    } else {
        stringResource(R.string.xlt_main_network_error)
    }
    val state = model.state.collectAsStateWithLifecycle()
    val breakIterator = remember(input) { BreakIterator.getCharacterInstance() }
    val typingDelayInMs = 50L

    var substringText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(state.value) {
        if (state.value == MainViewState.RESULT) {
            delay(12000)
            breakIterator.text = StringCharacterIterator(input)
            var nextIndex = breakIterator.next()
            while (nextIndex != BreakIterator.DONE) {
                substringText = input.subSequence(0, nextIndex).toString()
                nextIndex = breakIterator.next()
                delay(typingDelayInMs)
            }
        }
    }
    Text(
        text = substringText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 25.sp,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun RetryText(model: MainViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else MainColor.bg, label = ""
    )

    val boxColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF889BFF) else MainColor.fontMain, label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null) {
                model.goHome()
            }
            .background(color = boxColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 50.dp, vertical = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.xlt_retry),
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ResultView()
}