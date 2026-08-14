package com.threecolumn.cbt

import com.threecolumn.cbt.data.CognitiveDistortion
import com.threecolumn.cbt.data.Converters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `round trips an empty distortion list`() {
        val stored = converters.fromDistortionList(emptyList())
        assertEquals(emptyList<String>(), converters.toDistortionList(stored))
    }

    @Test
    fun `round trips multiple distortions`() {
        val original = listOf(
            CognitiveDistortion.ALL_OR_NOTHING.name,
            CognitiveDistortion.LABELING.name
        )
        val stored = converters.fromDistortionList(original)
        assertEquals(original, converters.toDistortionList(stored))
    }

    @Test
    fun `every distortion resolves from its own storage key`() {
        CognitiveDistortion.entries.forEach { distortion ->
            assertEquals(distortion, CognitiveDistortion.fromStorageKey(distortion.name))
        }
    }

    @Test
    fun `unknown storage key resolves to null`() {
        assertTrue(CognitiveDistortion.fromStorageKey("NOT_A_DISTORTION") == null)
    }
}
