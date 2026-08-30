package com.runemaster.app.professional

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val displayName: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "journal",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val request: String,
    val analysis: String = "",
    val formulaId: Long? = null,
    val practitionerNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "formulas",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId")]
)
data class FormulaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long? = null,
    val title: String,
    val intention: String,
    val primaryRune: String,
    val supportingRunes: String,
    val explanation: String = "",
    val activationNotes: String = "",
    val completionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface ProfessionalDao {

    @Query("SELECT * FROM clients ORDER BY displayName COLLATE NOCASE")
    fun clients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM journal ORDER BY createdAt DESC")
    fun journal(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journal WHERE clientId=:clientId ORDER BY createdAt DESC")
    fun journalForClient(clientId: Long): Flow<List<JournalEntity>>

    @Query("SELECT * FROM formulas ORDER BY createdAt DESC")
    fun formulas(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas WHERE clientId=:clientId ORDER BY createdAt DESC")
    fun formulasForClient(clientId: Long): Flow<List<FormulaEntity>>

    @Insert
    suspend fun insertClient(value: ClientEntity): Long

    @Update
    suspend fun updateClient(value: ClientEntity)

    @Delete
    suspend fun deleteClient(value: ClientEntity)

    @Insert
    suspend fun insertJournal(value: JournalEntity): Long

    @Update
    suspend fun updateJournal(value: JournalEntity)

    @Delete
    suspend fun deleteJournal(value: JournalEntity)

    @Insert
    suspend fun insertFormula(value: FormulaEntity): Long

    @Update
    suspend fun updateFormula(value: FormulaEntity)

    @Delete
    suspend fun deleteFormula(value: FormulaEntity)

    @Query("SELECT * FROM clients")
    suspend fun allClientsExport(): List<ClientEntity>

    @Query("SELECT * FROM journal")
    suspend fun allJournalExport(): List<JournalEntity>

    @Query("SELECT * FROM formulas")
    suspend fun allFormulasExport(): List<FormulaEntity>
}

@Database(
    entities = [
        ClientEntity::class,
        JournalEntity::class,
        FormulaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ProfessionalDatabase : RoomDatabase() {

    abstract fun dao(): ProfessionalDao

    companion object {
        @Volatile
        private var INSTANCE: ProfessionalDatabase? = null

        fun get(context: Context): ProfessionalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProfessionalDatabase::class.java,
                    "runemaster-private.db"
                ).build().also {
                    INSTANCE = it
                }
            }
    }
}
