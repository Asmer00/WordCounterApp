package com.example.wordcounter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [TextMetrics] data class verifying property accessors,
 * data class contract (equals, hashCode, toString, copy), and destructuring.
 */
class TextMetricsTest {

    @Test
    fun testPropertyGetters() {
        val metrics = TextMetrics(
            sentenceCount = 3,
            wordCount = 15,
            punctuationCount = 4,
            numberCount = 2
        )

        assertEquals(3, metrics.sentenceCount)
        assertEquals(15, metrics.wordCount)
        assertEquals(4, metrics.punctuationCount)
        assertEquals(2, metrics.numberCount)
    }

    @Test
    fun testEqualsAndHashCode() {
        val base = TextMetrics(1, 2, 3, 4)
        val identical = TextMetrics(1, 2, 3, 4)
        val diffSentences = TextMetrics(99, 2, 3, 4)
        val diffWords = TextMetrics(1, 99, 3, 4)
        val diffPunctuation = TextMetrics(1, 2, 99, 4)
        val diffNumbers = TextMetrics(1, 2, 3, 99)

        // Reflexive & Symmetric
        assertEquals(base, base)
        assertEquals(base, identical)
        assertEquals(identical, base)
        assertEquals(base.hashCode(), identical.hashCode())

        // Inequality across each property
        assertNotEquals(base, diffSentences)
        assertNotEquals(base, diffWords)
        assertNotEquals(base, diffPunctuation)
        assertNotEquals(base, diffNumbers)

        // Compare with null and different type
        assertFalse(base.equals(null))
        assertFalse(base.equals("some string"))
    }

    @Test
    fun testToString() {
        val metrics = TextMetrics(2, 10, 5, 1)
        val str = metrics.toString()

        assertTrue(str.contains("sentenceCount=2"))
        assertTrue(str.contains("wordCount=10"))
        assertTrue(str.contains("punctuationCount=5"))
        assertTrue(str.contains("numberCount=1"))
    }

    @Test
    fun testCopyFunctionality() {
        val original = TextMetrics(1, 2, 3, 4)

        val exactCopy = original.copy()
        assertEquals(original, exactCopy)

        val modifiedCopy = original.copy(wordCount = 50, numberCount = 10)
        assertEquals(1, modifiedCopy.sentenceCount)
        assertEquals(50, modifiedCopy.wordCount)
        assertEquals(3, modifiedCopy.punctuationCount)
        assertEquals(10, modifiedCopy.numberCount)
    }

    @Test
    fun testDestructuringDeclaration() {
        val metrics = TextMetrics(10, 20, 30, 40)
        val (s, w, p, n) = metrics

        assertEquals(10, s)
        assertEquals(20, w)
        assertEquals(30, p)
        assertEquals(40, n)
    }

    @Test
    fun testBoundaryValues() {
        val zeroes = TextMetrics(0, 0, 0, 0)
        assertEquals(0, zeroes.sentenceCount)
        assertEquals(0, zeroes.wordCount)
        assertEquals(0, zeroes.punctuationCount)
        assertEquals(0, zeroes.numberCount)

        val largeValues = TextMetrics(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, largeValues.sentenceCount)
        assertEquals(Int.MAX_VALUE, largeValues.wordCount)
        assertEquals(Int.MAX_VALUE, largeValues.punctuationCount)
        assertEquals(Int.MAX_VALUE, largeValues.numberCount)
    }
}
