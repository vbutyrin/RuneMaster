package com.runemaster.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runemaster.app.semantic.Domain
import com.runemaster.app.semantic.Intent as SemanticIntent
import com.runemaster.app.semantic.ProblemType
import com.runemaster.app.semantic.RuneFunction
import com.runemaster.app.semantic.RuneSolutionEngine

private val SemanticBg = Color(0xFF080706)
private val SemanticGold = Color(0xFFD6A94C)
private val SemanticGoldLight = Color(0xFFF6DA8A)
private val SemanticCard = Color(0xFF17130D)
private val SemanticText = Color(0xFFF3E8CC)
private val SemanticMuted = Color(0xFFBBAE8B)

@Composable
fun SemanticAnalysisScreen(
    text: String,
    runeLookup: (String) -> RuneInfo?,
    onBack: () -> Unit,
    onRune: (RuneInfo) -> Unit,
    onOpenFormula: (EditorFormulaInput) -> Unit
) {
    val solution = remember(text) {
        RuneSolutionEngine.solve(text)
    }

    val analysis = solution.analysis

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SemanticBg)
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(bottom = 45.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text("‹ ГЛАВНАЯ", color = SemanticGold)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ᛉ",
                    color = SemanticGoldLight,
                    fontSize = 45.sp
                )

                Text(
                    "АНАЛИЗ ЗАПРОСА",
                    color = SemanticGoldLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Semantic Engine 2.0",
                    color = SemanticMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SemanticCard
                )
            ) {
                Column(Modifier.padding(17.dp)) {
                    Text(
                        "ИСХОДНЫЙ ЗАПРОС",
                        color = SemanticGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        "«$text»",
                        color = SemanticText,
                        lineHeight = 21.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = {
                            analysis.confidence
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = SemanticGold,
                        trackColor =
                            SemanticGold.copy(alpha = .12f)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        "Уверенность разбора: " +
                            "${(analysis.confidence * 100).toInt()}%",
                        color = SemanticMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            SemanticTitle(
                "КАК ПРОГРАММА ПОНЯЛА СИТУАЦИЮ"
            )

            SemanticBlock(
                "Сферы",
                analysis.domains
                    .joinToString(" • ") {
                        domainName(it)
                    }
            )

            SemanticBlock(
                "Проблемы",
                analysis.problems
                    .joinToString(" • ") {
                        problemName(it)
                    }
            )

            SemanticBlock(
                "Цели / действия",
                analysis.intents
                    .joinToString(" • ") {
                        intentName(it)
                    }
            )

            if (analysis.entities.isNotEmpty()) {
                SemanticBlock(
                    "Объекты",
                    analysis.entities
                        .joinToString(" • ")
                )
            }

            Spacer(Modifier.height(20.dp))

            SemanticTitle(
                "ФУНКЦИИ ДЛЯ РЕШЕНИЯ"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF131B17)
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    solution.requiredFunctions.forEach {
                        Text(
                            "• ${functionName(it)}",
                            color = SemanticText,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            SemanticTitle("ПОДБОР РУН")

            Text(
                "Руны ранжируются по функциям всего запроса, а не по одному совпавшему слову.",
                color = SemanticMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(Modifier.height(7.dp))

            solution.runes.take(8).forEachIndexed {
                index,
                recommendation ->

                val rune =
                    runeLookup(recommendation.rune)

                if (rune != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onRune(rune)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (index == 0)
                                    Color(0xFF2A200E)
                                else SemanticCard
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(15.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Text(
                                rune.symbol,
                                color = SemanticGoldLight,
                                fontSize = 43.sp,
                                modifier = Modifier.width(65.dp)
                            )

                            Column {
                                Text(
                                    if (index == 0)
                                        "${rune.name} • ГЛАВНЫЙ КАНДИДАТ"
                                    else
                                        "${rune.name} • ${rune.russian}",
                                    color = SemanticGoldLight,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    recommendation.reason,
                                    color = SemanticText,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            SemanticTitle(
                "ИНДИВИДУАЛЬНЫЕ ФОРМУЛЫ"
            )

            Text(
                "Предлагаются три уровня сложности. Больше рун не означает автоматически более подходящую формулу.",
                color = SemanticMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            solution.formulas.forEach { formula ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 7.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SemanticCard
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            formula.type,
                            color = SemanticGoldLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(10.dp))

                        formula.primary?.let { primary ->
                            val rune =
                                runeLookup(primary.rune)

                            if (rune != null) {
                                FormulaRuneLine(
                                    rune = rune,
                                    role = "ГЛАВНАЯ",
                                    reason = primary.reason,
                                    main = true
                                )
                            }
                        }

                        formula.supporting.forEach {
                            support ->

                            val rune =
                                runeLookup(support.rune)

                            if (rune != null) {
                                FormulaRuneLine(
                                    rune = rune,
                                    role = "ПОДДЕРЖКА",
                                    reason = support.reason,
                                    main = false
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            formula.explanation,
                            color = SemanticMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val primary =
                                    formula.primary
                                        ?: return@Button

                                onOpenFormula(
                                    EditorFormulaInput(
                                        title = formula.type,
                                        intention = text,
                                        primaryRune =
                                            primary.rune,
                                        supportingRunes =
                                            formula.supporting
                                                .map { it.rune }
                                    )
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        SemanticGold,
                                    contentColor =
                                        Color.Black
                                )
                        ) {
                            Text(
                                "ОТКРЫТЬ В EDITOR",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF131B17)
                )
            ) {
                Text(
                    "Подбор предназначен для структурирования современной символической рунической практики. Он не гарантирует событие и не устанавливает факты о намерениях другого человека.",
                    modifier = Modifier.padding(16.dp),
                    color = SemanticMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun FormulaRuneLine(
    rune: RuneInfo,
    role: String,
    reason: String,
    main: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            rune.symbol,
            color =
                if (main)
                    SemanticGoldLight
                else SemanticGold,
            fontSize =
                if (main) 38.sp
                else 29.sp,
            modifier = Modifier.width(55.dp)
        )

        Column {
            Text(
                "${rune.name} — $role",
                color = SemanticGoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                reason,
                color = SemanticText,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun SemanticTitle(
    value: String
) {
    Text(
        value,
        color = SemanticGold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(
            top = 4.dp,
            bottom = 7.dp
        )
    )
}

@Composable
private fun SemanticBlock(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = SemanticCard
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                title.uppercase(),
                color = SemanticGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                value,
                color = SemanticText,
                fontSize = 14.sp,
                lineHeight = 19.sp
            )
        }
    }
}

private fun domainName(
    value: Domain
): String =
    when (value) {
        Domain.FAMILY -> "Семья"
        Domain.RELATIONSHIP -> "Отношения"
        Domain.LOVE -> "Любовь"
        Domain.FINANCE -> "Финансы"
        Domain.WORK -> "Работа"
        Domain.CAREER -> "Карьера"
        Domain.BUSINESS -> "Бизнес"
        Domain.DOCUMENTS -> "Документы / договоры"
        Domain.EDUCATION -> "Обучение"
        Domain.HOME -> "Дом / жильё"
        Domain.SOCIAL -> "Окружение"
        Domain.EMOTIONAL -> "Эмоциональная сфера"
        Domain.CREATIVITY -> "Творчество"
        Domain.TRAVEL -> "Дорога / поездки"
        Domain.CHANGE -> "Перемены"
        Domain.PROTECTION -> "Защита / границы"
        Domain.LEGAL -> "Юридическая сфера"
        else -> "Не определено"
    }

private fun intentName(
    value: SemanticIntent
): String =
    when (value) {
        SemanticIntent.GET -> "Получить"
        SemanticIntent.FIND -> "Найти"
        SemanticIntent.INCREASE -> "Увеличить"
        SemanticIntent.DECREASE -> "Уменьшить"
        SemanticIntent.KEEP -> "Сохранить"
        SemanticIntent.RESTORE -> "Восстановить"
        SemanticIntent.IMPROVE -> "Улучшить"
        SemanticIntent.END -> "Завершить"
        SemanticIntent.REMOVE -> "Устранить"
        SemanticIntent.PROTECT -> "Защитить"
        SemanticIntent.CLARIFY -> "Прояснить"
        SemanticIntent.ACCELERATE -> "Ускорить"
        SemanticIntent.CHANGE -> "Изменить"
        SemanticIntent.AGREE -> "Согласовать"
        SemanticIntent.COMMUNICATE -> "Наладить общение"
        SemanticIntent.DEVELOP -> "Развить"
        SemanticIntent.STABILIZE -> "Стабилизировать"
        else -> "Не определено"
    }

private fun problemName(
    value: ProblemType
): String =
    when (value) {
        ProblemType.LACK -> "Недостаток"
        ProblemType.LOSS -> "Потеря"
        ProblemType.CONFLICT -> "Конфликт"
        ProblemType.DELAY -> "Задержка"
        ProblemType.BLOCK -> "Препятствие"
        ProblemType.INSTABILITY -> "Нестабильность"
        ProblemType.EXCESS -> "Избыток / перерасход"
        ProblemType.UNCERTAINTY -> "Неопределённость"
        ProblemType.REJECTION -> "Отказ / несогласие"
        ProblemType.STAGNATION -> "Застой"
        ProblemType.SEPARATION -> "Расставание"
        ProblemType.COMMUNICATION ->
            "Проблема коммуникации"
        ProblemType.FEAR -> "Страх"
        ProblemType.PRESSURE -> "Давление"
        ProblemType.FAILURE -> "Неудача"
        ProblemType.CHANGE -> "Изменение"
        else -> "Требует уточнения"
    }

private fun functionName(
    value: RuneFunction
): String =
    when (value) {
        RuneFunction.RESOURCE -> "Материальный / практический ресурс"
        RuneFunction.COMMUNICATION -> "Коммуникация"
        RuneFunction.AGREEMENT -> "Согласование интересов"
        RuneFunction.MOVEMENT -> "Движение процесса"
        RuneFunction.DIRECTION -> "Направленность действий"
        RuneFunction.RESULT -> "Результат"
        RuneFunction.GROWTH -> "Развитие"
        RuneFunction.STABILITY -> "Устойчивость"
        RuneFunction.BOUNDARY -> "Границы"
        RuneFunction.CLARITY -> "Прояснение"
        RuneFunction.RELATIONSHIP -> "Взаимодействие"
        RuneFunction.HARMONY -> "Гармонизация"
        RuneFunction.TRANSITION -> "Переход"
        RuneFunction.ENDURANCE -> "Стойкость"
        RuneFunction.PERSON -> "Позиция человека"
        RuneFunction.EMOTION -> "Эмоциональная составляющая"
        else -> "Не определено"
    }
