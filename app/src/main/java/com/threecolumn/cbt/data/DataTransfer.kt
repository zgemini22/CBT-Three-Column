package com.threecolumn.cbt.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * JSON export/import for backup and batch-add. See parseImport's format below —
 * the same shape is shown to the user before they pick an import file.
 */
object DataTransfer {
    private const val KEY_THOUGHT_RECORDS = "thoughtRecords"
    private const val KEY_JOURNAL_ENTRIES = "journalEntries"

    class ImportException(message: String) : Exception(message)

    data class ImportResult(
        val thoughtRecords: List<ThoughtRecord>,
        val journalEntries: List<JournalEntry>
    )

    fun export(thoughtRecords: List<ThoughtRecord>, journalEntries: List<JournalEntry>): String {
        val root = JSONObject()

        val recordsArray = JSONArray()
        thoughtRecords.forEach { record ->
            recordsArray.put(
                JSONObject().apply {
                    put("createdAt", record.createdAt)
                    put("situation", record.situation)
                    put("automaticThought", record.automaticThought)
                    put("distortions", JSONArray(record.distortionKeys))
                    put("rationalResponse", record.rationalResponse)
                    put("beliefBefore", record.beliefBefore)
                    put("beliefAfter", record.beliefAfter)
                }
            )
        }
        root.put(KEY_THOUGHT_RECORDS, recordsArray)

        val entriesArray = JSONArray()
        journalEntries.forEach { entry ->
            entriesArray.put(
                JSONObject().apply {
                    put("createdAt", entry.createdAt)
                    put("body", entry.body)
                    put("pinned", entry.pinned)
                }
            )
        }
        root.put(KEY_JOURNAL_ENTRIES, entriesArray)

        return root.toString(2)
    }

    fun parseImport(json: String): ImportResult {
        val root = try {
            JSONObject(json)
        } catch (e: Exception) {
            throw ImportException("Not valid JSON.")
        }

        val records = mutableListOf<ThoughtRecord>()
        root.optJSONArray(KEY_THOUGHT_RECORDS)?.let { array ->
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val automaticThought = obj.optString("automaticThought")
                if (automaticThought.isBlank()) continue
                val distortions = obj.optJSONArray("distortions")?.let { codes ->
                    (0 until codes.length()).mapNotNull { index ->
                        CognitiveDistortion.fromStorageKey(codes.optString(index))
                    }
                }.orEmpty()
                records += ThoughtRecord(
                    createdAt = if (obj.has("createdAt")) obj.optLong("createdAt") else System.currentTimeMillis(),
                    situation = obj.optString("situation"),
                    automaticThought = automaticThought,
                    distortionKeys = distortions.map { it.name },
                    rationalResponse = obj.optString("rationalResponse"),
                    beliefBefore = obj.optInt("beliefBefore", 0),
                    beliefAfter = obj.optInt("beliefAfter", 0)
                )
            }
        }

        val entries = mutableListOf<JournalEntry>()
        root.optJSONArray(KEY_JOURNAL_ENTRIES)?.let { array ->
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val body = obj.optString("body")
                if (body.isBlank()) continue
                entries += JournalEntry(
                    createdAt = if (obj.has("createdAt")) obj.optLong("createdAt") else System.currentTimeMillis(),
                    body = body,
                    pinned = obj.optBoolean("pinned", false)
                )
            }
        }

        if (records.isEmpty() && entries.isEmpty()) {
            throw ImportException("No usable records found in this file.")
        }

        return ImportResult(records, entries)
    }
}
