package com.threecolumn.cbt.data

import kotlinx.coroutines.flow.Flow

class ThoughtRecordRepository(private val dao: ThoughtRecordDao) {
    fun observeAll(): Flow<List<ThoughtRecord>> = dao.observeAll()
    suspend fun getById(id: Long): ThoughtRecord? = dao.getById(id)
    suspend fun save(record: ThoughtRecord): Long = dao.upsert(record)
    suspend fun delete(record: ThoughtRecord) = dao.delete(record)
}

class HobbyIdeaRepository(private val dao: HobbyIdeaDao) {
    fun observeAll(): Flow<List<HobbyIdea>> = dao.observeAll()
    suspend fun save(idea: HobbyIdea): Long = dao.upsert(idea)
    suspend fun delete(idea: HobbyIdea) = dao.delete(idea)
}
