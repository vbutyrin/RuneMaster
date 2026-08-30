package com.runemaster.app.professional

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class RuneMasterBackup(
    val format: String = "RuneMasterBackup",
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val clients: List<ClientEntity>,
    val journal: List<JournalEntity>,
    val formulas: List<FormulaEntity>
)

class ProfessionalViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dao =
        ProfessionalDatabase.get(application).dao()

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

    fun saveFormula(
        clientId: Long?,
        title: String,
        intention: String,
        primary: String,
        supporting: List<String>,
        explanation: String
    ) {
        if (
            title.isBlank() ||
            intention.isBlank() ||
            primary.isBlank()
        ) return

        viewModelScope.launch {
            dao.insertFormula(
                FormulaEntity(
                    clientId = clientId,
                    title = title.trim(),
                    intention = intention.trim(),
                    primaryRune = primary,
                    supportingRunes =
                        supporting.joinToString(","),
                    explanation = explanation.trim()
                )
            )
        }
    }

    fun saveJournal(
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
                    practitionerNotes = notes.trim()
                )
            )
        }
    }

    fun exportBackup(
        onReady: (Intent) -> Unit
    ) {
        viewModelScope.launch {
            val backup = RuneMasterBackup(
                clients = dao.allClientsExport(),
                journal = dao.allJournalExport(),
                formulas = dao.allFormulasExport()
            )

            val json = Json {
                prettyPrint = true
                encodeDefaults = true
            }.encodeToString(backup)

            val context = getApplication<Application>()

            val dir = File(
                context.cacheDir,
                "exports"
            ).apply { mkdirs() }

            val stamp =
                SimpleDateFormat(
                    "yyyyMMdd-HHmmss",
                    Locale.US
                ).format(Date())

            val file =
                File(
                    dir,
                    "RuneMaster-$stamp.json"
                )

            file.writeText(json)

            val uri =
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.files",
                    file
                )

            val intent =
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
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
                    intent,
                    "Экспорт RuneMaster"
                )
            )
        }
    }
}
