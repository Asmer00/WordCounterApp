package com.example.wordcounter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [HistoryEntry] data class verifying property accessors,
 * default timestamp generation, data class contract (equals, hashCode, toString, copy),
 * and destructuring declarations.
 */
class HistoryEntryTest {

    private val sampleMetrics = TextMetrics(1, 5, 2, 0)

    @Test
    fun testConstructorWithExplicitTimestamp() {
        val entry = HistoryEntry(
            id = 42L,
            originalText = "Sample text for analysis.",
            metrics = sampleMetrics,
            timestampMillis = 1700000000000L
        )

        assertEquals(42L, entry.id)
        assertEquals("Sample text for analysis.", entry.originalText)
        assertEquals(sampleMetrics, entry.metrics)
        assertEquals(1700000000000L, entry.timestampMillis)
    }

    @Test
    fun testConstructorDefaultTimestamp() {
        val before = System.currentTimeMillis()
        val entry = HistoryEntry(
            id = 1L,
            originalText = "Testing default timestamp",
            metrics = sampleMetrics
        )
        val after = System.currentTimeMillis()

        assertTrue(entry.timestampMillis in before..after)
    }

    @Test
    fun testEqualsAndHashCode() {
        val metricsA = TextMetrics(1, 2, 3, 4)
        val metricsB = TextMetrics(5, 6, 7, 8)

        val base = HistoryEntry(1L, "Alpha", metricsA, 1000L)
        val identical = HistoryEntry(1L, "Alpha", metricsA, 1000L)
        val diffId = HistoryEntry(2L, "Alpha", metricsA, 1000L)
        val diffText = HistoryEntry(1L, "Beta", metricsA, 1000L)
        val diffMetrics = HistoryEntry(1L, "Alpha", metricsB, 1000L)
        val diffTimestamp = HistoryEntry(1L, "Alpha", metricsA, 2000L)

        // Reflexive & Symmetric
        assertEquals(base, base)
        assertEquals(base, identical)
        assertEquals(identical, base)
        assertEquals(base.hashCode(), identical.hashCode())

        // Inequality per property
        assertNotEquals(base, diffId)
        assertNotEquals(base, diffText)
        assertNotEquals(base, diffMetrics)
        assertNotEquals(base, diffTimestamp)

        // Null and type checks
        assertFalse(base.equals(null))
        assertFalse(base.equals("non-entry"))
    }

    @Test
    fun testToString() {
        val entry = HistoryEntry(10L, "Quick test", sampleMetrics, 5000L)
        val str = entry.toString()

        assertTrue(str.contains("id=10"))
        assertTrue(str.contains("originalText=Quick test"))
        assertTrue(str.contains("metrics="))
        assertTrue(str.contains("timestampMillis=5000"))
    }

    @Test
    fun testCopyFunctionality() {
        val original = HistoryEntry(1L, "Hello", sampleMetrics, 12345L)

        val exactCopy = original.copy()
        assertEquals(original, exactCopy)

        val modifiedCopy = original.copy(
            id = 99L,
            originalText = "Updated text"
        )
        assertEquals(99L, modifiedCopy.id)
        assertEquals("Updated text", modifiedCopy.originalText)
        assertEquals(sampleMetrics, modifiedCopy.metrics)
        assertEquals(12345L, modifiedCopy.timestampMillis)
    }

    @Test
    fun testDestructuringDeclaration() {
        val entry = HistoryEntry(7L, "Destructuring check", sampleMetrics, 9999L)
        val (id, text, metrics, timestamp) = entry

        assertEquals(7L, id)
        assertEquals("Destructuring check", text)
        assertEquals(sampleMetrics, metrics)
        assertEquals(9999L, timestamp)
    }
}
