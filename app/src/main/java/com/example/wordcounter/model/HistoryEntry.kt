package com.example.wordcounter.model

/**
 * Data class representing an entry in the calculation history.
 *
 * @property id Unique identifier for the history entry.
 * @property originalText The original text submitted by the user.
 * @property metrics The calculated metrics for the original text.
 * @property timestampMillis Timestamp when the calculation took place.
 */
data class HistoryEntry(
    val id: Long,
    val originalText: String,
    val metrics: TextMetrics,
    val timestampMillis: Long = System.currentTimeMillis()
)
