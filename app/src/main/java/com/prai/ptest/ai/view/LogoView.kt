package com.prai.ptest.ai.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h1andro2d.korean.ai.view.color.MainColor
import com.prai.ptest.ai.R

import com.prai.ptest.ai.view.model.MainViewModel
import com.prai.ptest.ai.view.model.MainViewState

@Composable
internal fun LogoView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.whale),
                contentDescription = "",
                modifier = Modifier.width(250.dp)
            )
            Text(
                stringResource(R.string.xlt_main_title),
                style = TextStyle(letterSpacing = 3.sp),
                color = Color(0xFF889BFF),
                fontSize = 35.sp, fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.xlt_main_sub_title),
                style = TextStyle(letterSpacing = 9.sp),
                color = MainColor.fontSub,
                fontSize = 23.sp, fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column {
                ColorAnimationTextSimple()
                Spacer(modifier = Modifier.height(15.dp))
                ColorAnimationTextFull()
            }
        }
    }
}

@Composable
private fun ColorAnimationTextSimple(model: MainViewModel = viewModel()) {
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
            .padding(horizontal = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                model.state.value = MainViewState.TEST
                model.isSimple.value = true
                model.questions.value = model.simpleQuestions.shuffled()
                model.index.value = 0
            }
            .fillMaxWidth()

            .background(color = boxColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 50.dp, vertical = 15.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.xlt_simple_test_title),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = " (${stringResource(R.string.xlt_simple_test_time)})",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}


@Composable
private fun ColorAnimationTextFull(model: MainViewModel = viewModel()) {
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
            .padding(horizontal = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) {
                model.state.value = MainViewState.TEST
                model.isSimple.value = true
                model.questions.value = model.fullQuestions.shuffled()
                model.index.value = 0
            }
            .fillMaxWidth()
            .background(color = boxColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 50.dp, vertical = 15.dp)
    ) {
        Row {
            Text(
                text = stringResource(R.string.xlt_full_test_title),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = " (${stringResource(R.string.xlt_full_test_time)})",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    LogoView()
}