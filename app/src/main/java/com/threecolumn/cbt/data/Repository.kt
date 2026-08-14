package com.threecolumn.cbt.data

import kotlinx.coroutines.flow.Flow

class ThoughtRecordRepository(private val dao: ThoughtRecordDao) {
    fun observeAll(): Flow<List<ThoughtRecord>> = dao.observeAll()
    suspend fun getById(id: Long): ThoughtRecord? = dao.getById(id)
    suspend fun save(record: ThoughtRecord): Long = dao.upsert(record)
    suspend fun delete(record: ThoughtRecord) = dao.delete(record)
}

class JournalEntryRepository(private val dao: JournalEntryDao) {
    fun observeAll(): Flow<List<JournalEntry>> = dao.observeAll()
    suspend fun getById(id: Long): JournalEntry? = dao.getById(id)
    suspend fun save(entry: JournalEntry): Long = dao.upsert(entry)
    suspend fun delete(entry: JournalEntry) = dao.delete(entry)
}
