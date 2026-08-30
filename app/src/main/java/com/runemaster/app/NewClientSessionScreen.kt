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
import com.runemaster.app.semantic.RuneSolutionEngine

private val SessionBg = Color(0xFF080706)
private val SessionCard = Color(0xFF17130D)
private val SessionGold = Color(0xFFD6A94C)
private val SessionLight = Color(0xFFF6DA8A)
private val SessionText = Color(0xFFF3E8CC)
private val SessionMuted = Color(0xFFBBAE8B)

@Composable
fun NewClientSessionScreen(
    initial: ClientSessionDraft? = null,
    runeLookup: (String) -> RuneInfo?,
    onBack: () -> Unit,
    onRune: (RuneInfo, ClientSessionDraft?) -> Unit,
    onFormula: (ClientSessionDraft) -> Unit
) {
    var clientName by remember {
        mutableStateOf(
            initial?.clientName ?: ""
        )
    }

    var clientNotes by remember {
        mutableStateOf(
            initial?.clientNotes ?: ""
        )
    }

    var problem by remember {
        mutableStateOf(
            initial?.problem ?: ""
        )
    }

    var practitionerNotes by remember {
        mutableStateOf(
            initial?.practitionerNotes ?: ""
        )
    }

    var analyzed by remember {
        mutableStateOf(
            initial != null
        )
    }

    val solution =
        remember(problem, analyzed) {
            if (
                analyzed &&
                problem.isNotBlank()
            )
                RuneSolutionEngine
                    .solve(problem)
            else null
        }

    fun currentDraft(
        formula: EditorFormulaInput
    ) =
        ClientSessionDraft(
            clientName =
                clientName,
            clientNotes =
                clientNotes,
            problem =
                problem,
            practitionerNotes =
                practitionerNotes,
            formula =
                formula
        )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(SessionBg)
            .padding(horizontal = 18.dp),
        contentPadding =
            PaddingValues(
                bottom = 50.dp
            )
    ) {
        item {
            TextButton(
                onClick = onBack
            ) {
                Text(
                    "‹ ЖУРНАЛ",
                    color = SessionGold
                )
            }

            Text(
                "НОВЫЙ КЛИЕНТ",
                color = SessionLight,
                fontSize = 23.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                "Карточка → проблема → решение → став → Editor → сохранение",
                color = SessionMuted,
                fontSize = 11.sp
            )

            Spacer(
                Modifier.height(18.dp)
            )

            OutlinedTextField(
                value = clientName,
                onValueChange = {
                    clientName = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Имя / псевдоним"
                    )
                }
            )

            Spacer(
                Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = clientNotes,
                onValueChange = {
                    clientNotes = it
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Общие сведения"
                    )
                },
                minLines = 2
            )

            Spacer(
                Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = problem,
                onValueChange = {
                    problem = it
                    analyzed = false
                },
                modifier =
                    Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Проблема / запрос"
                    )
                },
                placeholder = {
                    Text(
                        "Опишите ситуацию обычными словами"
                    )
                },
                minLines = 4
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Button(
                modifier =
                    Modifier.fillMaxWidth(),
                enabled =
                    clientName.isNotBlank() &&
                    problem.isNotBlank(),
                onClick = {
                    analyzed = true
                },
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                SessionGold,
                            contentColor =
                                Color.Black
                        )
            ) {
                Text(
                    "ПОДОБРАТЬ РЕШЕНИЕ",
                    fontWeight =
                        FontWeight.Bold
                )
            }

            solution?.let { result ->

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    "ОПРЕДЕЛЕНО",
                    color = SessionGold,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Card(
                    colors =
                        CardDefaults
                            .cardColors(
                                containerColor =
                                    SessionCard
                            )
                ) {
                    Column(
                        Modifier.padding(
                            14.dp
                        )
                    ) {
                        Text(
                            "Сферы: " +
                                result.analysis
                                    .domains
                                    .joinToString(),
                            color =
                                SessionText,
                            fontSize =
                                12.sp
                        )

                        Text(
                            "Проблемы: " +
                                result.analysis
                                    .problems
                                    .joinToString(),
                            color =
                                SessionText,
                            fontSize =
                                12.sp
                        )

                        Text(
                            "Цели: " +
                                result.analysis
                                    .intents
                                    .joinToString(),
                            color =
                                SessionText,
                            fontSize =
                                12.sp
                        )
                    }
                }

                Spacer(
                    Modifier.height(18.dp)
                )

                Text(
                    "ПОДОБРАННЫЕ РУНЫ",
                    color =
                        SessionGold,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    "Нажмите на руну, чтобы посмотреть полное описание и практику.",
                    color =
                        SessionMuted,
                    fontSize = 11.sp
                )

                result.runes
                    .take(8)
                    .forEach {
                        recommendation ->

                        val rune =
                            runeLookup(
                                recommendation.rune
                            )

                        if (rune != null) {

                            Card(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical =
                                                4.dp
                                        )
                                        .clickable {
                                            val temp =
                                                EditorFormulaInput(
                                                    title =
                                                        "Текущий подбор",
                                                    intention =
                                                        problem,
                                                    primaryRune =
                                                        result
                                                            .runes
                                                            .first()
                                                            .rune,
                                                    supportingRunes =
                                                        result
                                                            .runes
                                                            .drop(1)
                                                            .take(4)
                                                            .map {
                                                                it.rune
                                                            }
                                                )

                                            onRune(
                                                rune,
                                                currentDraft(
                                                    temp
                                                )
                                            )
                                        },
                                colors =
                                    CardDefaults
                                        .cardColors(
                                            containerColor =
                                                SessionCard
                                        )
                            ) {
                                Row(
                                    Modifier.padding(
                                        14.dp
                                    ),
                                    verticalAlignment =
                                        Alignment
                                            .CenterVertically
                                ) {
                                    Text(
                                        rune.symbol,
                                        color =
                                            SessionLight,
                                        fontSize =
                                            37.sp,
                                        modifier =
                                            Modifier
                                                .width(
                                                    58.dp
                                                )
                                    )

                                    Column {
                                        Text(
                                            "${rune.name} • ${rune.russian}",
                                            color =
                                                SessionLight,
                                            fontWeight =
                                                FontWeight
                                                    .Bold
                                        )

                                        Text(
                                            recommendation
                                                .reason,
                                            color =
                                                SessionText,
                                            fontSize =
                                                12.sp
                                        )

                                        Text(
                                            "ПРАКТИКА →",
                                            color =
                                                SessionGold,
                                            fontSize =
                                                10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                Spacer(
                    Modifier.height(20.dp)
                )

                Text(
                    "СТАВЫ ДЛЯ ЭТОГО ЗАПРОСА",
                    color =
                        SessionGold,
                    fontSize = 11.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                result.formulas
                    .forEach { formula ->

                        val primary =
                            formula.primary

                        if (
                            primary != null
                        ) {

                            val input =
                                EditorFormulaInput(
                                    title =
                                        formula.type,
                                    intention =
                                        problem,
                                    primaryRune =
                                        primary.rune,
                                    supportingRunes =
                                        formula
                                            .supporting
                                            .map {
                                                it.rune
                                            }
                                )

                            Card(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical =
                                                6.dp
                                        ),
                                colors =
                                    CardDefaults
                                        .cardColors(
                                            containerColor =
                                                SessionCard
                                        )
                            ) {
                                Column(
                                    Modifier.padding(
                                        15.dp
                                    )
                                ) {
                                    Text(
                                        formula.type,
                                        color =
                                            SessionLight,
                                        fontWeight =
                                            FontWeight
                                                .Bold
                                    )

                                    Spacer(
                                        Modifier.height(
                                            7.dp
                                        )
                                    )

                                    val names =
                                        listOf(
                                            primary.rune
                                        ) +
                                        formula
                                            .supporting
                                            .map {
                                                it.rune
                                            }

                                    names
                                        .chunked(4)
                                        .forEach {
                                            row ->

                                            Row(
                                                Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement =
                                                    Arrangement
                                                        .Center
                                            ) {
                                                row.forEach {
                                                    name ->

                                                    runeLookup(
                                                        name
                                                    )
                                                        ?.let {
                                                            rune ->

                                                            Text(
                                                                rune.symbol,
                                                                color =
                                                                    SessionGold,
                                                                fontSize =
                                                                    31.sp,
                                                                modifier =
                                                                    Modifier
                                                                        .padding(
                                                                            7.dp
                                                                        )
                                                            )
                                                        }
                                                }
                                            }
                                        }

                                    Text(
                                        formula
                                            .explanation,
                                        color =
                                            SessionMuted,
                                        fontSize =
                                            11.sp
                                    )

                                    Spacer(
                                        Modifier.height(
                                            8.dp
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            onFormula(
                                                currentDraft(
                                                    input
                                                )
                                            )
                                        },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth(),
                                        colors =
                                            ButtonDefaults
                                                .buttonColors(
                                                    containerColor =
                                                        SessionGold,
                                                    contentColor =
                                                        Color.Black
                                                )
                                    ) {
                                        Text(
                                            "ВЫБРАТЬ И ОТКРЫТЬ В EDITOR",
                                            fontSize =
                                                11.sp,
                                            fontWeight =
                                                FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                Spacer(
                    Modifier.height(15.dp)
                )

                OutlinedTextField(
                    value =
                        practitionerNotes,
                    onValueChange = {
                        practitionerNotes =
                            it
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Описание / заметки мастера"
                        )
                    },
                    minLines = 3
                )
            }
        }
    }
}
