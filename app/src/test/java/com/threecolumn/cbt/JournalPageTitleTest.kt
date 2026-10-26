package com.threecolumn.cbt

import com.threecolumn.cbt.data.DataTransfer
import com.threecolumn.cbt.data.JournalEntry
import com.threecolumn.cbt.util.SearchQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalPageTitleTest {

    @Test
    fun `a page title survives export and import`() {
        val page = JournalEntry(createdAt = 1_000L, title = "周一的会议", body = "A page")
        val result = DataTransfer.parseImport(DataTransfer.export(emptyList(), listOf(page), journalTitle = null))
        assertEquals("周一的会议", result.journalEntries.single().title)
        assertEquals("A page", result.journalEntries.single().body)
    }

    @Test
    fun `an untitled page is exported without a title key`() {
        val page = JournalEntry(createdAt = 1_000L, body = "A page")
        val json = DataTransfer.export(emptyList(), listOf(page), journalTitle = null)
        assertFalse(json.contains("\"title\""))
    }

    @Test
    fun `files from before page titles import as untitled`() {
        val result = DataTransfer.parseImport("""{ "journalEntries": [ { "body": "Old page" } ] }""")
        assertEquals("", result.journalEntries.single().title)
    }

    @Test
    fun `a page with only a title is kept, and one with neither is skipped`() {
        val result = DataTransfer.parseImport(
            """{ "journalEntries": [ { "title": "Just a title" }, { "title": "  ", "body": " " } ] }"""
        )
        assertEquals(listOf("Just a title"), result.journalEntries.map { it.title })
    }

    @Test
    fun `line breaks in a title become single spaces`() {
        assertEquals("Two lines here", JournalEntry.cleanTitle("  Two lines\n\n  here \r\n"))
    }

    @Test
    fun `editing a page with copy keeps its manual position`() {
        val page = JournalEntry(createdAt = 1_000L, body = "A page", sortIndex = 7L)
        assertEquals(7L, page.copy(title = "New", body = "Edited").sortIndex)
    }

    @Test
    fun `search finds a page by words split across its title and body`() {
        val query = SearchQuery("meeting anxious")
        assertTrue(query.matches(listOf("Monday meeting", "I felt anxious all day")))
    }
}
