package com.threecolumn.cbt.util

/**
 * What the user typed into a search field, split into terms. A result has to contain every term,
 * each in any of its fields, so "work anxious" finds a record that mentions both even when the
 * words are far apart or in different columns. Matching ignores case.
 */
class SearchQuery(raw: String) {
    val terms: List<String> = raw.split(TERM_SEPARATOR)
        .filter { it.isNotEmpty() }
        .distinctBy { it.lowercase() }

    val isEmpty: Boolean get() = terms.isEmpty()

    /** True when every term occurs in at least one of [fields]; an empty query matches everything. */
    fun matches(fields: List<String>): Boolean =
        terms.all { term -> fields.any { it.contains(term, ignoreCase = true) } }

    /** Every place a term occurs in [text], sorted, with overlapping or touching ranges merged. */
    fun matchRanges(text: String): List<IntRange> {
        val ranges = mutableListOf<IntRange>()
        for (term in terms) {
            var from = 0
            while (true) {
                val start = text.indexOf(term, from, ignoreCase = true)
                if (start < 0) break
                ranges += start until start + term.length
                from = start + term.length
            }
        }
        ranges.sortBy { it.first }
        val merged = mutableListOf<IntRange>()
        for (range in ranges) {
            val last = merged.lastOrNull()
            if (last != null && range.first <= last.last + 1) {
                merged[merged.lastIndex] = last.first..maxOf(last.last, range.last)
            } else {
                merged += range
            }
        }
        return merged
    }

    /**
     * [text] starting shortly before its first match, prefixed with an ellipsis, so a match deep in
     * a long passage is still inside a card's few visible lines. Text whose first match already sits
     * within [contextBefore] characters of the start, or that doesn't match, is returned unchanged.
     */
    fun excerpt(text: String, contextBefore: Int): String {
        val first = terms
            .map { text.indexOf(it, ignoreCase = true) }
            .filter { it >= 0 }
            .minOrNull() ?: return text
        if (first <= contextBefore) return text

        var start = first - contextBefore
        // Start on a word (spaced scripts) or a clause (Chinese, which has no spaces), rather
        // than halfway through one.
        val wordBreak = text.indexOfAny(WORD_BREAKS, start)
        if (wordBreak in start until first) start = wordBreak + 1
        // Never split a surrogate pair (most emoji) in two.
        if (text[start].isLowSurrogate()) start--
        if (start <= 0) return text
        return ELLIPSIS + text.substring(start).trimStart()
    }

    private companion object {
        // Chinese and Japanese IMEs type U+3000, the full-width space, which \s doesn't cover.
        val TERM_SEPARATOR = Regex("[\\s\\u3000]+")
        val WORD_BREAKS = charArrayOf(' ', '\n', '\t', '，', '。', '！', '？', '；', '：', '、')
        const val ELLIPSIS = "…"
    }
}
