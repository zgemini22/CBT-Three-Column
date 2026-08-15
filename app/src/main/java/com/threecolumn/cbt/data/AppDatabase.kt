package com.threecolumn.cbt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.threecolumn.cbt.R

@Database(
    entities = [ThoughtRecord::class, JournalEntry::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thoughtRecordDao(): ThoughtRecordDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "three_column_cbt.db"
                ).addMigrations(MIGRATION_1_2)
                    .addCallback(SeedJournalCallback(context.applicationContext))
                    .build().also { instance = it }
            }
    }
}

/** Gives the Journal a first, pre-written page on a fresh install. */
private class SeedJournalCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.execSQL(
            "INSERT INTO journal_entries (createdAt, body) VALUES (?, ?)",
            arrayOf(System.currentTimeMillis(), context.getString(R.string.journal_seed_entry_body))
        )
    }
}
