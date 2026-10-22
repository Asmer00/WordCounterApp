package com.example.wordcounter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for [SampleTextProvider] covering sample text retrieval,
 * random selection, and collision avoidance on consecutive calls.
 */
class SampleTextProviderTest {

    @Test
    fun testDefaultConstructorAndGetAllSampleTexts() {
        val provider = SampleTextProvider()
        val allTexts = provider.getAllSampleTexts()

        assertNotNull(allTexts)
        assertEquals(6, allTexts.size)
        // Ensure all sample texts are non-blank and distinct
        val distinctTexts = allTexts.distinct()
        assertEquals(6, distinctTexts.size)
        for (text in allTexts) {
            assertTrue("Sample text should not be blank", text.isNotBlank())
        }
    }

    @Test
    fun testGetRandomSampleTextReturnsValidItem() {
        val provider = SampleTextProvider()
        val allTexts = provider.getAllSampleTexts()

        // Call multiple times to verify every returned sample text belongs to predefined pool
        repeat(20) {
            val sample = provider.getRandomSampleText()
            assertTrue("Returned text should belong to predefined samples", allTexts.contains(sample))
        }
    }

    @Test
    fun testRandomCollisionAvoidanceWithControlledRandom() {
        // Sequence of numbers produced by nextInt:
        // First call: returns index 1
        // Second call: nextInt returns 1 (matches lastIndex 1), so while-loop triggers and takes nextInt -> 3
        val controlledRandom = object : Random() {
            private val sequence = listOf(1, 1, 3)
            private var idx = 0

            override fun nextBits(bitCount: Int): Int = 0

            override fun nextInt(until: Int): Int {
                val value = sequence[idx]
                if (idx < sequence.size - 1) idx++
                return value
            }
        }

        val provider = SampleTextProvider(controlledRandom)
        val allTexts = provider.getAllSampleTexts()

        val firstText = provider.getRandomSampleText()
        assertEquals(allTexts[1], firstText)

        val secondText = provider.getRandomSampleText()
        assertEquals(allTexts[3], secondText)
        assertNotEquals("Consecutive selections should not repeat the same index", firstText, secondText)
    }

    @Test
    fun testSampleTextsContainDiverseContent() {
        val provider = SampleTextProvider()
        val texts = provider.getAllSampleTexts()

        for (text in texts) {
            // Each sample text should contain letters, digits, and punctuation
            assertTrue(text.any { it.isLetter() })
            assertTrue(text.any { it.isDigit() })
            assertTrue(text.any { !it.isLetterOrDigit() && !it.isWhitespace() })
        }
    }

    @Test
    fun testGetRandomSampleTextSingleItemBoundary() {
        val provider = SampleTextProvider()
        val singleItem = listOf("Single test sentence for boundary check.")

        // Reflectively set the private 'sampleTexts' field because the production constructor
        // only accepts 'random' and does not expose a list injection parameter.
        val field = SampleTextProvider::class.java.getDeclaredField("sampleTexts")
        field.isAccessible = true
        field.set(provider, singleItem)

        // Verifies the boundary branch: if (sampleTexts.size <= 1) return sampleTexts.first()
        val result = provider.getRandomSampleText()
        assertEquals(singleItem.first(), result)
    }
}
