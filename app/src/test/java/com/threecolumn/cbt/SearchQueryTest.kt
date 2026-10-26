package com.threecolumn.cbt

import com.threecolumn.cbt.util.SearchQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchQueryTest {

    @Test
    fun `blank query is empty and matches everything`() {
        val query = SearchQuery("   ")
        assertTrue(query.isEmpty)
        assertTrue(query.matches(listOf("anything")))
    }

    @Test
    fun `full-width spaces separate terms like ordinary ones`() {
        assertEquals(listOf("工作", "焦虑"), SearchQuery("工作　焦虑").terms)
        assertEquals(listOf("work", "anxious"), SearchQuery("  work \t anxious ").terms)
    }

    @Test
    fun `terms repeated in different case collapse to one`() {
        assertEquals(listOf("Work"), SearchQuery("Work work WORK").terms)
    }

    @Test
    fun `every term must appear, each in any field`() {
        val query = SearchQuery("boss anxious")
        assertTrue(query.matches(listOf("My boss frowned", "I felt anxious")))
        assertFalse(query.matches(listOf("My boss frowned", "I felt fine")))
    }

    @Test
    fun `matching ignores case`() {
        assertTrue(SearchQuery("ANXIOUS").matches(listOf("I felt anxious")))
    }

    @Test
    fun `match ranges cover every occurrence of every term`() {
        val ranges = SearchQuery("ab").matchRanges("ab xx AB")
        assertEquals(listOf(0..1, 6..7), ranges)
    }

    @Test
    fun `overlapping and touching ranges merge`() {
        // "工作" at 0..1 and "作压" at 1..2 overlap; "力" at 3 touches 1..2.
        val ranges = SearchQuery("工作 作压 力").matchRanges("工作压力很大")
        assertEquals(listOf(0..3), ranges)
    }

    @Test
    fun `no match gives no ranges`() {
        assertEquals(emptyList<IntRange>(), SearchQuery("zzz").matchRanges("abc"))
    }

    @Test
    fun `excerpt leaves text alone when the match is already near the start`() {
        val text = "My boss frowned at me"
        assertEquals(text, SearchQuery("boss").excerpt(text, contextBefore = 10))
    }

    @Test
    fun `excerpt leaves text alone when nothing matches`() {
        val text = "a".repeat(100)
        assertEquals(text, SearchQuery("zzz").excerpt(text, contextBefore = 5))
    }

    @Test
    fun `excerpt starts on a word boundary before a deep match`() {
        val text = "one two three four five six seven eight nine ten anxious"
        val excerpt = SearchQuery("anxious").excerpt(text, contextBefore = 10)
        assertEquals("…nine ten anxious", excerpt)
    }

    @Test
    fun `excerpt in unspaced text keeps exactly the requested context`() {
        val text = "一二三四五六七八九十焦虑"
        assertEquals("…九十焦虑", SearchQuery("焦虑").excerpt(text, contextBefore = 2))
    }

    @Test
    fun `excerpt in Chinese starts after the nearest clause break`() {
        val text = "那也只是说明这一份报告可以更好，不代表我整个人能力不行。上个季度我得到过表扬。"
        assertEquals(
            "…不代表我整个人能力不行。上个季度我得到过表扬。",
            SearchQuery("表扬").excerpt(text, contextBefore = 30)
        )
    }

    @Test
    fun `excerpt uses the earliest match of any term`() {
        val text = "aaaaaaaaaa first bbbbbbbbbb second"
        assertTrue(SearchQuery("second first").excerpt(text, contextBefore = 3).startsWith("…"))
        assertTrue(SearchQuery("second first").excerpt(text, contextBefore = 3).contains("first"))
    }

    @Test
    fun `excerpt never splits an emoji`() {
        // "😀" is a surrogate pair at 2..3, so one character of context before "x" at 4 would
        // start on its second half.
        assertEquals("…😀x", SearchQuery("x").excerpt("ab😀x", contextBefore = 1))
    }

    @Test
    fun `excerpt adds no ellipsis when nothing ends up cut`() {
        // Backing up over the emoji lands on index 0, so the whole text is shown as is.
        assertEquals("😀x", SearchQuery("x").excerpt("😀x", contextBefore = 1))
    }
}
