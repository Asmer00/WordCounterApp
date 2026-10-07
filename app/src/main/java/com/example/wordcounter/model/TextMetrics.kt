package com.example.wordcounter.model

/**
 * Data class representing the four calculated text metrics.
 *
 * @property sentenceCount Number of sentences in the analyzed text.
 * @property wordCount Number of words in the analyzed text.
 * @property punctuationCount Number of punctuation marks in the analyzed text.
 * @property numberCount Number of numeric digits in the analyzed text.
 */
data class TextMetrics(
    val sentenceCount: Int,
    val wordCount: Int,
    val punctuationCount: Int,
    val numberCount: Int
)
