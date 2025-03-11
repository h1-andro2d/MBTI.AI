package com.prai.ptest.ai.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prai.ptest.ai.R
import com.prai.ptest.ai.view.api.AnalysisResponse
import com.prai.ptest.ai.view.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val simpleQuestions = listOf(
        Question("n1", R.string.xlt_key_n1, "plus", "N"),
        Question("n2", R.string.xlt_key_n2, "plus", "N"),
        Question("n3", R.string.xlt_key_n3, "plus", "N"),
        Question("n4", R.string.xlt_key_n4, "plus", "N"),
        Question("n5", R.string.xlt_key_n5, "plus", "N"),
        Question("e1", R.string.xlt_key_e1, "plus", "E"),
        Question("e2", R.string.xlt_key_e2, "plus", "E"),
        Question("e3", R.string.xlt_key_e3, "plus", "E"),
        Question("e4", R.string.xlt_key_e4, "plus", "E"),
        Question("e5", R.string.xlt_key_e5, "plus", "E"),
        Question("o1", R.string.xlt_key_o1, "plus", "O"),
        Question("o2", R.string.xlt_key_o2, "plus", "O"),
        Question("o3", R.string.xlt_key_o3, "plus", "O"),
        Question("o4", R.string.xlt_key_o4, "plus", "O"),
        Question("o5", R.string.xlt_key_o5, "plus", "O"),
        Question("a1", R.string.xlt_key_a1, "plus", "A"),
        Question("a2", R.string.xlt_key_a2, "plus", "A"),
        Question("a3", R.string.xlt_key_a3, "minus", "A"),
        Question("a4", R.string.xlt_key_a4, "plus", "A"),
        Question("a5", R.string.xlt_key_a5, "plus", "A"),
        Question("c1", R.string.xlt_key_c1, "plus", "C"),
        Question("c2", R.string.xlt_key_c2, "plus", "C"),
        Question("c3", R.string.xlt_key_c3, "plus", "C"),
        Question("c4", R.string.xlt_key_c4, "plus", "C"),
        Question("c5", R.string.xlt_key_c5, "plus", "C")
    )

    val state = MutableStateFlow(MainViewState.LOGO)
    val isSimple = MutableStateFlow(true)
    val index = MutableStateFlow(0)
    val questions by lazy { MutableStateFlow(simpleQuestions) }
    val choices = mapOf(
        "plus" to listOf(
            Choice("매우 그렇지 않다", 1),
            Choice("그렇지 않다", 2),
            Choice("보통이다", 3),
            Choice("그렇다", 4),
            Choice("매우 그렇다", 5)
        ),
        "minus" to listOf(
            Choice("매우 그렇지 않다", 5),
            Choice("그렇지 않다", 4),
            Choice("보통이다", 3),
            Choice("그렇다", 2),
            Choice("매우 그렇다", 1)
        )
    )
    private val simpleChoices = mutableMapOf<String, Int>()
    private val fullChoices = mutableMapOf<String, Int>()
    val response = MutableStateFlow<AnalysisResponse?>(null)

    init {
        viewModelScope.launch {
            ApiClient.analysisResponse.collect {
                response.value = it
            }
        }
    }

    fun onAnswerClicked(question: Question, selected: Int) { // 매우 그렇다 == 4, 매우 아니다 == 0
        if (isSimple.value) {
            simpleChoices[question.id] = selected
        } else {
            fullChoices[question.id] = selected
        }
        if (index.value + 1 < questions.value.size) {
            index.value += 1
        } else {
            state.value = MainViewState.RESULT
        }
    }

    fun onBackClicked() {
        if (index.value - 1 >= 0) {
            index.value -= 1
        } else {
            goHome()
        }
    }

    fun goHome() {
        state.value = MainViewState.LOGO
        index.value = 0
        simpleChoices.clear()
        fullChoices.clear()
    }

    fun calculateResult(): TestResult {
        val selectedChoices = if (isSimple.value) {
            simpleChoices
        } else {
            fullChoices
        }
        val domains = mutableMapOf("N" to 0, "E" to 0, "O" to 0, "A" to 0, "C" to 0)

        questions.value.forEach { q ->
            val choiceIndex = selectedChoices[q.id] ?: 0
            val choiceList = choices[q.keyed] ?: listOf()
            domains[q.domain] = (domains[q.domain] ?: 0) + choiceList[choiceIndex].score
        }

        val maxScore = (questions.value.size / 5) * 5
        val minScore = (questions.value.size / 5) * 1

        val results = domains.mapValues { (_, value) ->
            ((value - minScore).toDouble() / (maxScore - minScore) * 100).toInt()
        }

        val mbtiType = buildString {
            append(if ((results["E"] ?: 50) > 50) "E" else "I")
            append(if ((results["O"] ?: 50) > 50) "N" else "S")
            append(if ((results["A"] ?: 50) > 50) "F" else "T")
            append(if ((results["C"] ?: 50) > 50) "J" else "P")
        }
        return TestResult(
            mbtiType,
            results["E"] ?: 50,
            results["O"] ?: 50,
            results["A"] ?: 50,
            results["C"] ?: 50,
            results["N"] ?: 50
        )
    }

    fun createTagResource(type: String): Int? {
        return when (type) {
            "ISTJ" -> R.string.xlt_istj
            "ISFJ" -> R.string.xlt_isfj
            "INFJ" -> R.string.xlt_infj
            "INTJ" -> R.string.xlt_intj
            "ISTP" -> R.string.xlt_istp
            "ISFP" -> R.string.xlt_isfp
            "INFP" -> R.string.xlt_infp
            "INTP" -> R.string.xlt_intp
            "ESTP" -> R.string.xlt_estp
            "ESFP" -> R.string.xlt_esfp
            "ENFP" -> R.string.xlt_enfp
            "ENTP" -> R.string.xlt_entp
            "ESTJ" -> R.string.xlt_estj
            "ESFJ" -> R.string.xlt_esfj
            "ENFJ" -> R.string.xlt_enfj
            "ENTJ" -> R.string.xlt_entj
            else -> null
        }
    }

    val explainText =
        "통솔자(ENTJ)는 타고난 리더라고 할 수 있습니다. 이들은 카리스마와 자신감을 지니고 있으며 자신의 권한을 이용해 사람들이 공통된 목표를 위해 함께 노력하도록 이끕니다. 또한 이들은 냉철한 이성을 지닌 것으로 유명하며 자신이 원하는 것을 성취하기 위해 열정과 결단력과 날카로운 지적 능력을 활용합니다. 이들은 전체 인구의 3%에 불과하지만, 다른 많은 성격을 압도하는 존재감을 뽐내며 다양한 비즈니스와 단체를 이끄는 역할을 할 때가 많습니다.\n\n통솔자는 도전을 즐기는 성격으로 충분한 시간과 자원이 주어진다면 어떠한 목표도 달성할 수 있다고 믿습니다. 이들은 훌륭한 사업가가 될 가능성이 높으며, 전략적 사고 능력과 장기적 목표에 집중하고 결단력 있게 계획을 실행하는 능력은 위대한 비즈니스 리더가 되는 데 도움이 됩니다. 이들은 강력한 의지로 다른 사람이 포기하는 상황에서도 굴하지 않고 목표를 추구하며, 외향적(E) 성향은 다른 사람이 목표를 이루는 과정에 협력하도록 하는 데 도움이 됩니다."


    val fullQuestions = listOf(
        // Neuroticism
        Question("n1", R.string.xlt_full_n1, "plus", "N"),
        Question("n2", R.string.xlt_full_n2, "plus", "N"),
        Question("n3", R.string.xlt_full_n3, "plus", "N"),
        Question("n4", R.string.xlt_full_n4, "plus", "N"),
        Question("n5", R.string.xlt_full_n5, "plus", "N"),
        Question("n6", R.string.xlt_full_n6, "plus", "N"),
        Question("n7", R.string.xlt_full_n7, "plus", "N"),
        Question("n8", R.string.xlt_full_n8, "plus", "N"),
        Question("n9", R.string.xlt_full_n9, "plus", "N"),
        Question("n10", R.string.xlt_full_n10, "plus", "N"),
        Question("n11", R.string.xlt_full_n11, "minus", "N"),
        Question("n12", R.string.xlt_full_n12, "plus", "N"),
        Question("n13", R.string.xlt_full_n13, "plus", "N"),
        Question("n14", R.string.xlt_full_n14, "plus", "N"),
        Question("n15", R.string.xlt_full_n15, "plus", "N"),
        Question("n16", R.string.xlt_full_n16, "plus", "N"),
        Question("n17", R.string.xlt_full_n17, "minus", "N"),
        Question("n18", R.string.xlt_full_n18, "plus", "N"),
        Question("n19", R.string.xlt_full_n19, "plus", "N"),
        Question("n20", R.string.xlt_full_n20, "minus", "N"),
        Question("n21", R.string.xlt_full_n21, "minus", "N"),
        Question("n22", R.string.xlt_full_n22, "minus", "N"),
        Question("n23", R.string.xlt_full_n23, "minus", "N"),
        Question("n24", R.string.xlt_full_n24, "minus", "N"),
        Question("n25", R.string.xlt_full_n25, "minus", "N"),

        // Extraversion
        Question("e1", R.string.xlt_full_e1, "plus", "E"),
        Question("e2", R.string.xlt_full_e2, "plus", "E"),
        Question("e3", R.string.xlt_full_e3, "plus", "E"),
        Question("e4", R.string.xlt_full_e4, "plus", "E"),
        Question("e5", R.string.xlt_full_e5, "plus", "E"),
        Question("e6", R.string.xlt_full_e6, "plus", "E"),
        Question("e7", R.string.xlt_full_e7, "plus", "E"),
        Question("e8", R.string.xlt_full_e8, "plus", "E"),
        Question("e9", R.string.xlt_full_e9, "plus", "E"),
        Question("e10", R.string.xlt_full_e10, "plus", "E"),
        Question("e11", R.string.xlt_full_e11, "plus", "E"),
        Question("e12", R.string.xlt_full_e12, "plus", "E"),
        Question("e13", R.string.xlt_full_e13, "minus", "E"),
        Question("e14", R.string.xlt_full_e14, "minus", "E"),
        Question("e15", R.string.xlt_full_e15, "plus", "E"),
        Question("e16", R.string.xlt_full_e16, "plus", "E"),
        Question("e17", R.string.xlt_full_e17, "plus", "E"),
        Question("e18", R.string.xlt_full_e18, "plus", "E"),
        Question("e19", R.string.xlt_full_e19, "minus", "E"),
        Question("e20", R.string.xlt_full_e20, "minus", "E"),
        Question("e21", R.string.xlt_full_e21, "minus", "E"),
        Question("e22", R.string.xlt_full_e22, "minus", "E"),
        Question("e23", R.string.xlt_full_e23, "plus", "E"),
        Question("e24", R.string.xlt_full_e24, "plus", "E"),
        Question("e25", R.string.xlt_full_e25, "minus", "E"),

        // Openness
        Question("o1", R.string.xlt_full_o1, "plus", "O"),
        Question("o2", R.string.xlt_full_o2, "plus", "O"),
        Question("o3", R.string.xlt_full_o3, "plus", "O"),
        Question("o4", R.string.xlt_full_o4, "plus", "O"),
        Question("o5", R.string.xlt_full_o5, "plus", "O"),
        Question("o6", R.string.xlt_full_o6, "plus", "O"),
        Question("o7", R.string.xlt_full_o7, "plus", "O"),
        Question("o8", R.string.xlt_full_o8, "plus", "O"),
        Question("o9", R.string.xlt_full_o9, "plus", "O"),
        Question("o10", R.string.xlt_full_o10, "minus", "O"),
        Question("o11", R.string.xlt_full_o11, "minus", "O"),
        Question("o12", R.string.xlt_full_o12, "plus", "O"),
        Question("o13", R.string.xlt_full_o13, "plus", "O"),
        Question("o14", R.string.xlt_full_o14, "minus", "O"),
        Question("o15", R.string.xlt_full_o15, "minus", "O"),
        Question("o16", R.string.xlt_full_o16, "minus", "O"),
        Question("o17", R.string.xlt_full_o17, "minus", "O"),
        Question("o18", R.string.xlt_full_o18, "minus", "O"),
        Question("o19", R.string.xlt_full_o19, "plus", "O"),
        Question("o20", R.string.xlt_full_o20, "minus", "O"),
        Question("o21", R.string.xlt_full_o21, "minus", "O"),
        Question("o22", R.string.xlt_full_o22, "minus", "O"),
        Question("o23", R.string.xlt_full_o23, "minus", "O"),
        Question("o24", R.string.xlt_full_o24, "minus", "O"),
        Question("o25", R.string.xlt_full_o25, "minus", "O"),

        // Agreeableness
        Question("a1", R.string.xlt_full_a1, "plus", "A"),
        Question("a2", R.string.xlt_full_a2, "minus", "A"),
        Question("a3", R.string.xlt_full_a3, "plus", "A"),
        Question("a4", R.string.xlt_full_a4, "minus", "A"),
        Question("a5", R.string.xlt_full_a5, "minus", "A"),
        Question("a6", R.string.xlt_full_a6, "plus", "A"),
        Question("a7", R.string.xlt_full_a7, "plus", "A"),
        Question("a8", R.string.xlt_full_a8, "minus", "A"),
        Question("a9", R.string.xlt_full_a9, "plus", "A"),
        Question("a10", R.string.xlt_full_a10, "minus", "A"),
        Question("a11", R.string.xlt_full_a11, "minus", "A"),
        Question("a12", R.string.xlt_full_a12, "plus", "A"),
        Question("a13", R.string.xlt_full_a13, "plus", "A"),
        Question("a14", R.string.xlt_full_a14, "minus", "A"),
        Question("a15", R.string.xlt_full_a15, "minus", "A"),
        Question("a16", R.string.xlt_full_a16, "minus", "A"),
        Question("a17", R.string.xlt_full_a17, "minus", "A"),
        Question("a18", R.string.xlt_full_a18, "minus", "A"),
        Question("a19", R.string.xlt_full_a19, "minus", "A"),
        Question("a20", R.string.xlt_full_a20, "minus", "A"),
        Question("a21", R.string.xlt_full_a21, "minus", "A"),
        Question("a22", R.string.xlt_full_a22, "minus", "A"),
        Question("a23", R.string.xlt_full_a23, "minus", "A"),
        Question("a24", R.string.xlt_full_a24, "minus", "A"),
        Question("a25", R.string.xlt_full_a25, "minus", "A"),

        // Conscientiousness
        Question("c1", R.string.xlt_full_c1, "plus", "C"),
        Question("c2", R.string.xlt_full_c2, "plus", "C"),
        Question("c3", R.string.xlt_full_c3, "plus", "C"),
        Question("c4", R.string.xlt_full_c4, "plus", "C"),
        Question("c5", R.string.xlt_full_c5, "plus", "C"),
        Question("c6", R.string.xlt_full_c6, "minus", "C"),
        Question("c7", R.string.xlt_full_c7, "plus", "C"),
        Question("c8", R.string.xlt_full_c8, "minus", "C"),
        Question("c9", R.string.xlt_full_c9, "plus", "C"),
        Question("c10", R.string.xlt_full_c10, "plus", "C"),
        Question("c11", R.string.xlt_full_c11, "plus", "C"),
        Question("c12", R.string.xlt_full_c12, "minus", "C"),
        Question("c13", R.string.xlt_full_c13, "plus", "C"),
        Question("c14", R.string.xlt_full_c14, "minus", "C"),
        Question("c15", R.string.xlt_full_c15, "minus", "C"),
        Question("c16", R.string.xlt_full_c16, "minus", "C"),
        Question("c17", R.string.xlt_full_c17, "minus", "C"),
        Question("c18", R.string.xlt_full_c18, "minus", "C"),
        Question("c19", R.string.xlt_full_c19, "plus", "C"),
        Question("c20", R.string.xlt_full_c20, "minus", "C"),
        Question("c21", R.string.xlt_full_c21, "minus", "C"),
        Question("c22", R.string.xlt_full_c22, "minus", "C"),
        Question("c23", R.string.xlt_full_c23, "minus", "C"),
        Question("c24", R.string.xlt_full_c24, "minus", "C"),
        Question("c25", R.string.xlt_full_c25, "minus", "C")
    )
}