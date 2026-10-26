package com.threecolumn.cbt

import com.threecolumn.cbt.data.DataTransfer
import com.threecolumn.cbt.data.JournalTitleStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JournalTitleTest {

    private val default = "Why Fear of Opposition and Criticism Is Irrational and Unnecessary"

    @Test
    fun `a new title is kept, trimmed`() {
        assertEquals("My own topic", JournalTitleStore.normalize("  My own topic  ", default))
    }

    @Test
    fun `blank title falls back to the built-in one`() {
        assertNull(JournalTitleStore.normalize("   ", default))
        assertNull(JournalTitleStore.normalize(null, default))
    }

    @Test
    fun `saving the built-in title unchanged stores nothing, so it keeps following the language`() {
        assertNull(JournalTitleStore.normalize(default, default))
        assertNull(JournalTitleStore.normalize("  $default\n", default))
    }

    @Test
    fun `pasted line breaks become single spaces`() {
        assertEquals("First line second line", JournalTitleStore.normalize("First line\n\n  second line\r\n", default))
    }

    @Test
    fun `export round-trips a custom title`() {
        val json = DataTransfer.export(emptyList(), emptyList(), journalTitle = "我的主题")
        assertEquals("我的主题", DataTransfer.parseImport(json).journalTitle)
    }

    @Test
    fun `export leaves the title out when the journal was never renamed`() {
        val json = DataTransfer.export(emptyList(), emptyList(), journalTitle = null)
        assertTrue("journalTitle" !in json)
    }

    @Test
    fun `a file that only renames the journal is a valid import`() {
        val result = DataTransfer.parseImport("""{ "journalTitle": "New title" }""")
        assertEquals("New title", result.journalTitle)
        assertTrue(result.thoughtRecords.isEmpty() && result.journalEntries.isEmpty())
    }

    @Test
    fun `a file without a title leaves the journal's title alone`() {
        val result = DataTransfer.parseImport("""{ "journalEntries": [ { "body": "A page" } ] }""")
        assertNull(result.journalTitle)
    }

    @Test(expected = DataTransfer.ImportException::class)
    fun `a blank title alone is not a usable import`() {
        DataTransfer.parseImport("""{ "journalTitle": "   " }""")
    }
}
