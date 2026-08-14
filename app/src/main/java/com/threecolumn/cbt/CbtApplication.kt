package com.threecolumn.cbt

import android.app.Application
import com.threecolumn.cbt.data.AppDatabase
import com.threecolumn.cbt.data.JournalEntryRepository
import com.threecolumn.cbt.data.ThoughtRecordRepository

class CbtApplication : Application() {
    val thoughtRecordRepository: ThoughtRecordRepository by lazy {
        ThoughtRecordRepository(AppDatabase.getInstance(this).thoughtRecordDao())
    }
    val journalEntryRepository: JournalEntryRepository by lazy {
        JournalEntryRepository(AppDatabase.getInstance(this).journalEntryDao())
    }
}
