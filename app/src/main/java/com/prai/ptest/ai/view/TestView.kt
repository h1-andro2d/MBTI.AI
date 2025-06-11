package com.prai.ptest.ai.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h1andro2d.korean.ai.view.color.MainColor
import com.prai.ptest.ai.R
import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.Question
import java.text.BreakIterator
import java.text.StringCharacterIterator
import kotlinx.coroutines.delay

@Composable
internal fun TestView(model: MainViewModel = viewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    val index = model.index.collectAsStateWithLifecycle()
    val questions = model.questions.collectAsStateWithLifecycle()
    val data = questions.value[index.value]
    val animatedProgress by remember { mutableStateOf(Animatable(initialValue = 0f)) }

    LaunchedEffect(index.value) {
        animatedProgress.animateTo(
            targetValue = (index.value + 1).toFloat() / (model.questions.value.size),
            animationSpec = tween(durationMillis = 700)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor.bg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                            model.onBackClicked()
                        }
                        .width(30.dp)
                )
                Text(
                    text = "${index.value + 1}/${questions.value.size}",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                )
                Image(
                    painter = painterResource(R.drawable.home),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                            model.goHome()
                        }
                        .width(30.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .padding(bottom = 30.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0x33889BEA),
                        drawStopIndicator = {},
                        gapSize = 0.dp
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress.value },
                        modifier = Modifier
                            .padding(bottom = 30.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0xFF889BEA),
                        trackColor = Color.Transparent,
                        drawStopIndicator = {
                        },
                        gapSize = 0.dp
                    )
                }
                AnimatedText2("Q${index.value + 1}. ${stringResource(data.text)}")
                Spacer(modifier = Modifier.weight(0.4f))
                Image(
                    painter = painterResource(R.drawable.whale),
                    contentDescription = "",
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column {

                    ColorAnimationText(stringResource(R.string.xlt_strongly_agree), 4, data)
                    ColorAnimationText(stringResource(R.string.xlt_agree), 3, data)
                    ColorAnimationText(stringResource(R.string.xlt_neutral), 2, data)
                    ColorAnimationText(stringResource(R.string.xlt_disagree), 1, data)
                    ColorAnimationText(stringResource(R.string.xlt_strongly_disagree), 0, data)
                }
            }
        }
    }
}

@Composable
private fun TypingAnimationText(text: String) {
    val breakIterator = remember(text) { BreakIterator.getCharacterInstance() }
    val typingDelayInMs = 100L

    var substringText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(text) {
        delay(10)
        breakIterator.text = StringCharacterIterator(text)
        var nextIndex = breakIterator.next()
        while (nextIndex != BreakIterator.DONE) {
            substringText = text.subSequence(0, nextIndex).toString()
            nextIndex = breakIterator.next()
            delay(typingDelayInMs)
        }
    }
    Text(
        text = substringText,
        style = TextStyle(
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Center,
        ),
        modifier = Modifier
            .height(60.dp)
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun AnimatedText2(text: String) {
    val animatedProgress by remember { mutableStateOf(Animatable(initialValue = 0f)) }
    LaunchedEffect(text) {
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Center,
        ),
        modifier = Modifier
            .alpha(animatedProgress.value)
            .height(60.dp)
            .padding(horizontal = 20.dp)
    )
}

@Composable
private fun ColorAnimationText(
    text: String,
    index: Int,
    question: Question,
    model: MainViewModel = viewModel()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val textColor by animateColorAsState(
        targetValue = if (isPressed) Color.White else Color(0xFF4455FF), label = ""
    )

    val boxColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF889BFF) else Color.Transparent, label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null) {
                model.onAnswerClicked(question, index)
            }
            .background(color = boxColor, shape = RoundedCornerShape(20.dp))
            .border(width = 0.5.dp, color = Color(0xFF99AAFF), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 50.dp, vertical = 15.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        )
    }
}


@Preview(showBackground = true, widthDp = 800, heightDp = 720)
@Composable
private fun Preview() {
    TestView()
}