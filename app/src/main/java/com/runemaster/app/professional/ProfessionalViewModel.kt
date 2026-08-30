package com.runemaster.app.professional

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class RuneMasterBackup(
    val format: String = "RuneMasterBackup",
    val version: Int = 2,
    val exportedAt: Long =
        System.currentTimeMillis(),
    val clients: List<ClientEntity>,
    val journal: List<JournalEntity>,
    val formulas: List<FormulaEntity>
)

class ProfessionalViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao =
        ProfessionalDatabase
            .get(application)
            .dao()

    private val json =
        Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    val clients =
        dao.clients().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val journal =
        dao.journal().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val formulas =
        dao.formulas().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addClient(
        name: String,
        notes: String
    ) {
        if (name.isBlank()) return

        viewModelScope.launch {
            dao.insertClient(
                ClientEntity(
                    displayName = name.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    fun deleteClient(
        client: ClientEntity
    ) {
        viewModelScope.launch {
            dao.deleteClient(client)
        }
    }

    fun addJournal(
        clientId: Long,
        request: String,
        analysis: String,
        notes: String
    ) {
        if (request.isBlank()) return

        viewModelScope.launch {
            dao.insertJournal(
                JournalEntity(
                    clientId = clientId,
                    request = request.trim(),
                    analysis = analysis.trim(),
                    practitionerNotes =
                        notes.trim()
                )
            )
        }
    }

    fun saveFormula(
        clientId: Long?,
        title: String,
        intention: String,
        primaryRune: String,
        supportingRunes: List<String>,
        explanation: String,
        compositionJson: String
    ) {
        if (
            title.isBlank() ||
            primaryRune.isBlank()
        ) return

        viewModelScope.launch {
            dao.insertFormula(
                FormulaEntity(
                    clientId = clientId,
                    title = title.trim(),
                    intention =
                        intention.trim(),
                    primaryRune =
                        primaryRune,
                    supportingRunes =
                        supportingRunes
                            .joinToString(","),
                    explanation =
                        explanation.trim(),
                    compositionJson =
                        compositionJson
                )
            )
        }
    }

    fun saveCompleteSession(
        clientName: String,
        clientNotes: String,
        request: String,
        analysis: String,
        practitionerNotes: String,
        formulaTitle: String,
        intention: String,
        primaryRune: String,
        supportingRunes: List<String>,
        formulaExplanation: String,
        compositionJson: String,
        onSaved: () -> Unit
    ) {
        if (
            clientName.isBlank() ||
            request.isBlank()
        ) return

        viewModelScope.launch {

            val clientId =
                dao.insertClient(
                    ClientEntity(
                        displayName =
                            clientName.trim(),
                        notes =
                            clientNotes.trim()
                    )
                )

            val formulaId =
                dao.insertFormula(
                    FormulaEntity(
                        clientId =
                            clientId,
                        title =
                            formulaTitle,
                        intention =
                            intention,
                        primaryRune =
                            primaryRune,
                        supportingRunes =
                            supportingRunes
                                .joinToString(","),
                        explanation =
                            formulaExplanation,
                        compositionJson =
                            compositionJson
                    )
                )

            dao.insertJournal(
                JournalEntity(
                    clientId =
                        clientId,
                    request =
                        request.trim(),
                    analysis =
                        analysis,
                    formulaId =
                        formulaId,
                    practitionerNotes =
                        practitionerNotes.trim()
                )
            )

            onSaved()
        }
    }

    fun exportBackup(
        onReady: (Intent) -> Unit
    ) {
        viewModelScope.launch {

            val backup =
                RuneMasterBackup(
                    clients =
                        dao.allClientsExport(),
                    journal =
                        dao.allJournalExport(),
                    formulas =
                        dao.allFormulasExport()
                )

            val text =
                json.encodeToString(
                    backup
                )

            val context =
                getApplication<Application>()

            val directory =
                File(
                    context.cacheDir,
                    "exports"
                ).apply {
                    mkdirs()
                }

            val date =
                SimpleDateFormat(
                    "yyyyMMdd-HHmmss",
                    Locale.US
                ).format(Date())

            val file =
                File(
                    directory,
                    "RuneMaster-Backup-$date.json"
                )

            withContext(
                Dispatchers.IO
            ) {
                file.writeText(text)
            }

            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.files",
                    file
                )

            val send =
                Intent(
                    Intent.ACTION_SEND
                ).apply {

                    type =
                        "application/json"

                    putExtra(
                        Intent.EXTRA_STREAM,
                        uri
                    )

                    addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

            onReady(
                Intent.createChooser(
                    send,
                    "Экспорт RuneMaster"
                )
            )
        }
    }

    fun importBackup(
        uri: Uri,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {

            try {

                val context =
                    getApplication<Application>()

                val text =
                    withContext(
                        Dispatchers.IO
                    ) {
                        context.contentResolver
                            .openInputStream(uri)
                            ?.bufferedReader()
                            ?.use {
                                it.readText()
                            }
                    }
                    ?: throw Exception(
                        "Файл не удалось прочитать"
                    )

                val backup =
                    json.decodeFromString<
                        RuneMasterBackup
                    >(text)

                if (
                    backup.format !=
                    "RuneMasterBackup"
                ) {
                    throw Exception(
                        "Это не резервная копия RuneMaster"
                    )
                }

                dao.importClients(
                    backup.clients
                )

                dao.importFormulas(
                    backup.formulas
                )

                dao.importJournal(
                    backup.journal
                )

                onResult(
                    true,
                    "Восстановлено: " +
                        "${backup.clients.size} клиентов, " +
                        "${backup.journal.size} записей, " +
                        "${backup.formulas.size} формул"
                )

            } catch (e: Exception) {

                onResult(
                    false,
                    e.message
                        ?: "Ошибка импорта"
                )
            }
        }
    }
}
