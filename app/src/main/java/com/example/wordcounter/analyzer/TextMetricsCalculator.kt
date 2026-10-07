package com.example.wordcounter.analyzer

import com.example.wordcounter.model.TextMetrics

/**
 * Reusable analyzer that calculates text metrics:
 * 1. Number of sentences
 * 2. Number of words
 * 3. Number of punctuation marks (Unicode classification)
 * 4. Number of numeric digits (0-9)
 *
 * This class has no Android framework dependencies, making it portable and easy to unit-test.
 */
class TextMetricsCalculator {

    companion object {
        private val WORD_REGEX = Regex("""\b[\p{L}\p{M}]+(?:['’\-][\p{L}\p{M}]+)*\b""")
        private val SENTENCE_DELIMITER_REGEX = Regex("""[.!?]+(?:\s+|\z)|\n+""")
    }

    /**
     * Analyzes the provided text and returns the calculated [TextMetrics].
     *
     * @param input Raw input text to analyze.
     * @return [TextMetrics] containing counts for sentences, words, punctuation marks, and numbers.
     */
    fun calculate(input: String?): TextMetrics {
        if (input.isNullOrBlank()) {
            return TextMetrics(
                sentenceCount = 0,
                wordCount = 0,
                punctuationCount = 0,
                numberCount = 0
            )
        }

        val sentences = countSentences(input)
        val words = countWords(input)
        val punctuation = countPunctuationMarks(input)
        val numbers = countNumbers(input)

        return TextMetrics(
            sentenceCount = sentences,
            wordCount = words,
            punctuationCount = punctuation,
            numberCount = numbers
        )
    }

    /**
     * Counts the number of sentences in the text.
     * Text is delimited by sentence terminators (. ! ?) and newlines.
     * Any non-empty fragment containing at least one alphanumeric or letter character
     * is counted as a sentence.
     */
    fun countSentences(text: String): Int {
        if (text.isBlank()) return 0

        // Split text by sentence terminators or line breaks
        val parts = text.split(SENTENCE_DELIMITER_REGEX)
        val validSentences = parts.filter { segment ->
            segment.any { it.isLetterOrDigit() }
        }

        // If the entire text contains letter/digit content but no terminator split matched,
        // it counts as 1 sentence (e.g. "Good morning")
        return if (validSentences.isEmpty() && text.any { it.isLetterOrDigit() }) {
            1
        } else {
            validSentences.size
        }
    }

    /**
     * Counts the number of words in the text.
     * A word is defined as a sequence of Unicode letter/mark characters,
     * allowing inner apostrophes or hyphens (e.g., "don't", "state-of-the-art").
     */
    fun countWords(text: String): Int {
        if (text.isBlank()) return 0
        return WORD_REGEX.findAll(text).count()
    }

    /**
     * Counts the number of punctuation marks in the text using standard
     * Unicode character category classification (\p{P}).
     * Includes characters such as . , ! ? : ; ' " - ( ) [ ] { } etc.
     */
    fun countPunctuationMarks(text: String): Int {
        return text.count { char -> isPunctuation(char) }
    }

    /**
     * Counts each numeric digit (0-9) as one number, per requirement specifications.
     */
    fun countNumbers(text: String): Int {
        return text.count { char -> char in '0'..'9' }
    }

    /**
     * Checks if a character belongs to any Unicode punctuation category.
     */
    private fun isPunctuation(c: Char): Boolean {
        val type = Character.getType(c).toByte()
        return type == Character.CONNECTOR_PUNCTUATION ||
                type == Character.DASH_PUNCTUATION ||
                type == Character.START_PUNCTUATION ||
                type == Character.END_PUNCTUATION ||
                type == Character.INITIAL_QUOTE_PUNCTUATION ||
                type == Character.FINAL_QUOTE_PUNCTUATION ||
                type == Character.OTHER_PUNCTUATION
    }
}
