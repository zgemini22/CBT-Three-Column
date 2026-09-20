package com.threecolumn.cbt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.threecolumn.cbt.R
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File

@Database(
    entities = [ThoughtRecord::class, JournalEntry::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thoughtRecordDao(): ThoughtRecordDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        private const val DATABASE_NAME = "three_column_cbt.db"

        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN sortIndex INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE journal_entries SET sortIndex = createdAt")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: run {
                    val appContext = context.applicationContext
                    val passphrase = DatabaseKey.getOrCreate(appContext)
                    encryptExistingPlaintextDatabase(appContext, passphrase)
                    Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                        .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray())))
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .addCallback(SeedJournalCallback(appContext))
                        .build()
                        .also { instance = it }
                }
            }

        /**
         * Installs from before the database was encrypted left a plaintext file behind, which
         * SQLCipher cannot open. On the first run after upgrading, copy its contents into an
         * encrypted file with sqlcipher_export() and swap that in, so the upgrade doesn't look
         * to the user like their records vanished.
         */
        private fun encryptExistingPlaintextDatabase(context: Context, passphrase: String) {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if (!dbFile.exists() || !isPlaintextSqlite(dbFile)) return

            SQLiteDatabase.loadLibs(context)
            val encrypted = File(dbFile.parentFile, "$DATABASE_NAME.encrypting")
            encrypted.delete()

            val plaintext = SQLiteDatabase.openOrCreateDatabase(dbFile, "", null)
            try {
                plaintext.rawExecSQL("ATTACH DATABASE '${encrypted.absolutePath}' AS encrypted KEY '$passphrase';")
                plaintext.rawExecSQL("SELECT sqlcipher_export('encrypted');")
                // sqlcipher_export() copies the schema and rows but not user_version, which is
                // how Room tracks which migrations have already run.
                plaintext.rawExecSQL("PRAGMA encrypted.user_version = ${plaintext.version};")
                plaintext.rawExecSQL("DETACH DATABASE encrypted;")
            } finally {
                plaintext.close()
            }

            val oldPlaintext = File(dbFile.parentFile, "$DATABASE_NAME.plaintext")
            dbFile.renameTo(oldPlaintext)
            encrypted.renameTo(dbFile)
            oldPlaintext.delete()
            // These sidecars describe the plaintext file that no longer exists.
            File("${dbFile.absolutePath}-wal").delete()
            File("${dbFile.absolutePath}-shm").delete()
        }

        /** An unencrypted SQLite file opens with this header; an encrypted one starts with salt. */
        private fun isPlaintextSqlite(file: File): Boolean {
            val header = ByteArray(SQLITE_HEADER.size)
            file.inputStream().use { stream ->
                if (stream.read(header) != header.size) return false
            }
            return header.contentEquals(SQLITE_HEADER)
        }

        private val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
    }
}

/** Gives the Journal a first, pre-written page on a fresh install. */
private class SeedJournalCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val now = System.currentTimeMillis()
        db.execSQL(
            "INSERT INTO journal_entries (createdAt, body, pinned, sortIndex) VALUES (?, ?, ?, ?)",
            arrayOf(now, context.getString(R.string.journal_seed_entry_body), 0, now)
        )
    }
}
