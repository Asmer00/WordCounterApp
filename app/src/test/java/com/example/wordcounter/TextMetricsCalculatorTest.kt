package com.example.wordcounter

import com.example.wordcounter.analyzer.TextMetricsCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TextMetricsCalculator] verifying accurate calculation
 * of sentences, words, punctuation marks, and numbers.
 */
class TextMetricsCalculatorTest {

    private lateinit var calculator: TextMetricsCalculator

    @Before
    fun setUp() {
        calculator = TextMetricsCalculator()
    }

    @Test
    fun testEmptyAndBlankInput() {
        val emptyResult = calculator.calculate("")
        assertEquals(0, emptyResult.sentenceCount)
        assertEquals(0, emptyResult.wordCount)
        assertEquals(0, emptyResult.punctuationCount)
        assertEquals(0, emptyResult.numberCount)

        val nullResult = calculator.calculate(null)
        assertEquals(0, nullResult.sentenceCount)
        assertEquals(0, nullResult.wordCount)
        assertEquals(0, nullResult.punctuationCount)
        assertEquals(0, nullResult.numberCount)

        val whitespaceResult = calculator.calculate("   \n\t  ")
        assertEquals(0, whitespaceResult.sentenceCount)
        assertEquals(0, whitespaceResult.wordCount)
        assertEquals(0, whitespaceResult.punctuationCount)
        assertEquals(0, whitespaceResult.numberCount)
    }

    @Test
    fun testSimplePhraseWithoutTrailingPunctuation() {
        // "Good morning" has 1 sentence, 2 words, 0 punctuation, 0 numbers
        val result = calculator.calculate("Good morning")
        assertEquals(1, result.sentenceCount)
        assertEquals(2, result.wordCount)
        assertEquals(0, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testSentenceWithNumberAndPunctuation() {
        // "I have 2 books!" has 1 sentence, 3 words (I, have, books), 1 punctuation (!), 1 number (2)
        val result = calculator.calculate("I have 2 books!")
        assertEquals(1, result.sentenceCount)
        assertEquals(3, result.wordCount)
        assertEquals(1, result.punctuationCount)
        assertEquals(1, result.numberCount)
    }

    @Test
    fun testMultiSentenceWithDiverseMetrics() {
        val text = "Hello world! In 2024, did you read 15 books? Yes, I did."
        val result = calculator.calculate(text)

        // Sentences: "Hello world", "In 2024, did you read 15 books", "Yes, I did" -> 3
        assertEquals(3, result.sentenceCount)

        // Words: Hello, world, In, did, you, read, books, Yes, I, did -> 10 words
        assertEquals(10, result.wordCount)

        // Punctuation marks: !, ,, ?, ,, . -> 5
        assertEquals(5, result.punctuationCount)

        // Numbers (individual digits 0-9): '2', '0', '2', '4', '1', '5' -> 6
        assertEquals(6, result.numberCount)
    }

    @Test
    fun testHyphenatedAndContractionWords() {
        // "Don't stop the state-of-the-art process."
        val text = "Don't stop the state-of-the-art process."
        val result = calculator.calculate(text)

        assertEquals(1, result.sentenceCount)
        // Words: Don't, stop, the, state-of-the-art, process -> 5 words
        assertEquals(5, result.wordCount)
        // Punctuation: ' (apostrophe), - (3 hyphens), . (period) -> 5
        assertEquals(5, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testUnicodePunctuationSupport() {
        // Spanish inverted punctuation
        val text = "¿Cómo estás? ¡Muy bien!"
        val result = calculator.calculate(text)

        assertEquals(2, result.sentenceCount)
        assertEquals(4, result.wordCount)
        // Punctuation: ¿, ?, ¡, ! -> 4
        assertEquals(4, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }
}
