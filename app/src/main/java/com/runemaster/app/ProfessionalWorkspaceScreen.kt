package com.runemaster.app

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.runemaster.app.professional.ClientEntity
import com.runemaster.app.professional.ProfessionalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ProBg =
    Color(0xFF080706)

private val ProCard =
    Color(0xFF17130D)

private val ProGold =
    Color(0xFFD6A94C)

private val ProLight =
    Color(0xFFF6DA8A)

private val ProText =
    Color(0xFFF3E8CC)

private val ProMuted =
    Color(0xFFBBAE8B)

private enum class WorkspaceTab {
    CLIENTS,
    JOURNAL,
    FORMULAS
}

@Composable
fun ProfessionalWorkspaceScreen(
    onBack: () -> Unit,
    onNewClientSession: () -> Unit
) {
    val vm:
        ProfessionalViewModel =
        viewModel()

    val clients by
        vm.clients.collectAsState()

    val journal by
        vm.journal.collectAsState()

    val formulas by
        vm.formulas.collectAsState()

    var tab by remember {
        mutableStateOf(
            WorkspaceTab.CLIENTS
        )
    }

    var selectedClient by remember {
        mutableStateOf<ClientEntity?>(
            null
        )
    }

    var showNewClient by remember {
        mutableStateOf(false)
    }

    var showJournalDialog by remember {
        mutableStateOf(false)
    }

    var importMessage by remember {
        mutableStateOf<String?>(null)
    }

    val context =
        androidx.compose.ui.platform
            .LocalContext.current

    val importer =
        rememberLauncherForActivityResult(
            ActivityResultContracts
                .OpenDocument()
        ) { uri ->

            if (uri != null) {
                vm.importBackup(uri) {
                    _,
                    message ->

                    importMessage =
                        message
                }
            }
        }

    Column(
        Modifier
            .fillMaxSize()
            .background(ProBg)
            .padding(14.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = onBack
            ) {
                Text(
                    "‹ ГЛАВНАЯ",
                    color = ProGold
                )
            }

            Text(
                "РАБОЧЕЕ МЕСТО",
                color = ProLight,
                fontWeight =
                    FontWeight.Bold,
                modifier =
                    Modifier.padding(
                        top = 14.dp
                    )
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            ProTabButton(
                "КЛИЕНТЫ",
                tab ==
                    WorkspaceTab.CLIENTS
            ) {
                tab =
                    WorkspaceTab.CLIENTS
            }

            ProTabButton(
                "ЖУРНАЛ",
                tab ==
                    WorkspaceTab.JOURNAL
            ) {
                tab =
                    WorkspaceTab.JOURNAL
            }

            ProTabButton(
                "ФОРМУЛЫ",
                tab ==
                    WorkspaceTab.FORMULAS
            ) {
                tab =
                    WorkspaceTab.FORMULAS
            }
        }

        Spacer(
            Modifier.height(7.dp)
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {

            Button(
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    vm.exportBackup {
                        intent: Intent ->

                        context.startActivity(
                            intent
                        )
                    }
                },
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                Color(
                                    0xFF2B2110
                                ),
                            contentColor =
                                ProLight
                        )
            ) {
                Text(
                    "ЭКСПОРТ",
                    fontSize = 11.sp
                )
            }

            Button(
                modifier =
                    Modifier.weight(1f),
                onClick = {
                    importer.launch(
                        arrayOf(
                            "application/json",
                            "text/plain",
                            "*/*"
                        )
                    )
                },
                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                Color(
                                    0xFF2B2110
                                ),
                            contentColor =
                                ProLight
                        )
            ) {
                Text(
                    "ИМПОРТ",
                    fontSize = 11.sp
                )
            }
        }

        importMessage?.let {
            Text(
                it,
                color = ProMuted,
                fontSize = 11.sp,
                modifier =
                    Modifier.padding(
                        vertical = 5.dp
                    )
            )
        }

        when (tab) {

            WorkspaceTab.CLIENTS -> {

                Text(
                    "КАРТОТЕКА",
                    color = ProGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 5.dp
                    )
                )

                Button(
                    modifier =
                        Modifier.fillMaxWidth(),
                    onClick = {
                        onNewClientSession()
                    },
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    ProGold,
                                contentColor =
                                    Color.Black
                            )
                ) {
                    Text(
                        "+ ДОБАВИТЬ КЛИЕНТА",
                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    Modifier.height(6.dp)
                )

                LazyColumn(
                    Modifier.weight(1f)
                ) {
                    items(
                        clients,
                        key = { it.id }
                    ) { client ->

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical =
                                            4.dp
                                    )
                                    .clickable {
                                        selectedClient =
                                            client
                                    },
                            colors =
                                CardDefaults
                                    .cardColors(
                                        containerColor =
                                            if (
                                                selectedClient
                                                    ?.id ==
                                                client.id
                                            )
                                                Color(
                                                    0xFF30230D
                                                )
                                            else
                                                ProCard
                                    )
                        ) {
                            Column(
                                Modifier.padding(
                                    15.dp
                                )
                            ) {
                                Text(
                                    client.displayName,
                                    color =
                                        ProLight,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                if (
                                    client.notes
                                        .isNotBlank()
                                ) {
                                    Text(
                                        client.notes,
                                        color =
                                            ProMuted,
                                        fontSize =
                                            12.sp
                                    )
                                }

                                Spacer(
                                    Modifier.height(
                                        7.dp
                                    )
                                )

                                TextButton(
                                    onClick = {
                                        selectedClient =
                                            client

                                        showJournalDialog =
                                            true
                                    }
                                ) {
                                    Text(
                                        "+ НОВЫЙ СЕАНС",
                                        color =
                                            ProGold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            WorkspaceTab.JOURNAL -> {

                LazyColumn(
                    Modifier.weight(1f)
                ) {

                    items(
                        journal,
                        key = { it.id }
                    ) { entry ->

                        val client =
                            clients.find {
                                it.id ==
                                    entry.clientId
                            }

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical =
                                            4.dp
                                    ),
                            colors =
                                CardDefaults
                                    .cardColors(
                                        containerColor =
                                            ProCard
                                    )
                        ) {
                            Column(
                                Modifier.padding(
                                    15.dp
                                )
                            ) {

                                Text(
                                    client
                                        ?.displayName
                                        ?: "Клиент",
                                    color =
                                        ProLight,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    formatDate(
                                        entry.createdAt
                                    ),
                                    color =
                                        ProGold,
                                    fontSize =
                                        10.sp
                                )

                                Spacer(
                                    Modifier.height(
                                        7.dp
                                    )
                                )

                                Text(
                                    entry.request,
                                    color =
                                        ProText,
                                    fontSize =
                                        13.sp
                                )

                                if (
                                    entry.analysis
                                        .isNotBlank()
                                ) {
                                    Spacer(
                                        Modifier.height(
                                            6.dp
                                        )
                                    )

                                    Text(
                                        "Анализ: " +
                                            entry.analysis,
                                        color =
                                            ProMuted,
                                        fontSize =
                                            12.sp
                                    )
                                }

                                if (
                                    entry.practitionerNotes
                                        .isNotBlank()
                                ) {
                                    Spacer(
                                        Modifier.height(
                                            6.dp
                                        )
                                    )

                                    Text(
                                        "Заметки: " +
                                            entry
                                                .practitionerNotes,
                                        color =
                                            ProMuted,
                                        fontSize =
                                            12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            WorkspaceTab.FORMULAS -> {

                LazyColumn(
                    Modifier.weight(1f)
                ) {

                    items(
                        formulas,
                        key = { it.id }
                    ) { formula ->

                        val client =
                            clients.find {
                                it.id ==
                                    formula.clientId
                            }

                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical =
                                            4.dp
                                    ),
                            colors =
                                CardDefaults
                                    .cardColors(
                                        containerColor =
                                            ProCard
                                    )
                        ) {
                            Column(
                                Modifier.padding(
                                    15.dp
                                )
                            ) {

                                Text(
                                    formula.title,
                                    color =
                                        ProLight,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    client
                                        ?.displayName
                                        ?: "Без клиента",
                                    color =
                                        ProGold,
                                    fontSize =
                                        11.sp
                                )

                                Spacer(
                                    Modifier.height(
                                        5.dp
                                    )
                                )

                                Text(
                                    "Главная: " +
                                        formula.primaryRune,
                                    color =
                                        ProText,
                                    fontSize =
                                        12.sp
                                )

                                Text(
                                    "Поддержка: " +
                                        formula.supportingRunes,
                                    color =
                                        ProMuted,
                                    fontSize =
                                        12.sp
                                )

                                Text(
                                    formatDate(
                                        formula.createdAt
                                    ),
                                    color =
                                        ProMuted,
                                    fontSize =
                                        10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewClient) {

        var name by remember {
            mutableStateOf("")
        }

        var notes by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                showNewClient = false
            },
            title = {
                Text("Новый клиент")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = {
                            Text(
                                "Имя или псевдоним"
                            )
                        }
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                        },
                        label = {
                            Text("Заметка")
                        },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.addClient(
                            name,
                            notes
                        )
                        showNewClient =
                            false
                    }
                ) {
                    Text("СОХРАНИТЬ")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showNewClient =
                            false
                    }
                ) {
                    Text("ОТМЕНА")
                }
            }
        )
    }

    if (
        showJournalDialog &&
        selectedClient != null
    ) {

        var request by remember {
            mutableStateOf("")
        }

        var analysis by remember {
            mutableStateOf("")
        }

        var notes by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {
                showJournalDialog =
                    false
            },
            title = {
                Text(
                    selectedClient!!
                        .displayName
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = request,
                        onValueChange = {
                            request = it
                        },
                        label = {
                            Text("Запрос")
                        },
                        minLines = 2
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    OutlinedTextField(
                        value = analysis,
                        onValueChange = {
                            analysis = it
                        },
                        label = {
                            Text(
                                "Результат анализа"
                            )
                        },
                        minLines = 2
                    )

                    Spacer(
                        Modifier.height(6.dp)
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            notes = it
                        },
                        label = {
                            Text(
                                "Заметки мастера"
                            )
                        },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        vm.addJournal(
                            clientId =
                                selectedClient!!
                                    .id,
                            request =
                                request,
                            analysis =
                                analysis,
                            notes =
                                notes
                        )

                        showJournalDialog =
                            false
                    }
                ) {
                    Text("СОХРАНИТЬ")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJournalDialog =
                            false
                    }
                ) {
                    Text("ОТМЕНА")
                }
            }
        )
    }
}

@Composable
private fun RowScope.ProTabButton(
    title: String,
    selected: Boolean,
    click: () -> Unit
) {
    Button(
        modifier = Modifier.weight(1f),
        onClick = click,
        contentPadding =
            PaddingValues(
                horizontal = 2.dp
            ),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    if (selected)
                        ProGold
                    else
                        ProCard,

                contentColor =
                    if (selected)
                        Color.Black
                    else
                        ProLight
            )
    ) {
        Text(
            title,
            fontSize = 10.sp
        )
    }
}

private fun formatDate(
    time: Long
): String =
    SimpleDateFormat(
        "dd.MM.yyyy HH:mm",
        Locale.getDefault()
    ).format(
        Date(time)
    )
