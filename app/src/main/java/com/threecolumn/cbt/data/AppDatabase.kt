package com.threecolumn.cbt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.threecolumn.cbt.R

@Database(
    entities = [ThoughtRecord::class, JournalEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun thoughtRecordDao(): ThoughtRecordDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "three_column_cbt.db"
                ).addCallback(SeedJournalCallback(context.applicationContext))
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
