package com.prai.ptest.ai.view.model


data class Choice(val text: String, val score: Int)
data class Question(val id: String, val text: Int, val keyed: String, val domain: String)
data class TestResult(
    val type: String, val ei: Int, val sn: Int, val tf: Int, val jp: Int, val nscore: Int
)