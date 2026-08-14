package com.threecolumn.cbt

import android.app.Application
import com.threecolumn.cbt.data.AppDatabase
import com.threecolumn.cbt.data.HobbyIdeaRepository
import com.threecolumn.cbt.data.ThoughtRecordRepository

class CbtApplication : Application() {
    val thoughtRecordRepository: ThoughtRecordRepository by lazy {
        ThoughtRecordRepository(AppDatabase.getInstance(this).thoughtRecordDao())
    }
    val hobbyIdeaRepository: HobbyIdeaRepository by lazy {
        HobbyIdeaRepository(AppDatabase.getInstance(this).hobbyIdeaDao())
    }
}
