package com.example.wordcounter

import com.example.wordcounter.analyzer.TextMetricsCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [TextMetricsCalculator] verifying accurate calculation
 * of sentences, words, punctuation marks, and numbers across standard,
 * edge, Unicode punctuation, contraction, and isolated method invocations.
 */
class TextMetricsCalculatorTest {

    private lateinit var calculator: TextMetricsCalculator

    @Before
    fun setUp() {
        calculator = TextMetricsCalculator()
    }

    // --- Original baseline tests ---

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

    // --- Extended boundary and comprehensive tests ---

    @Test
    fun testPureDigitsInput() {
        // "12345 67890" has 10 digits, 0 words (digits only, no letters), 1 sentence, 0 punctuation
        val result = calculator.calculate("12345 67890")
        assertEquals(1, result.sentenceCount)
        assertEquals(0, result.wordCount)
        assertEquals(0, result.punctuationCount)
        assertEquals(10, result.numberCount)
    }

    @Test
    fun testPurePunctuationInput() {
        // Only punctuation marks with no alphanumeric characters
        val result = calculator.calculate("... !? ;;; --- :::")
        assertEquals(0, result.sentenceCount)
        assertEquals(0, result.wordCount)
        assertEquals(14, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testMultipleConsecutiveSentenceDelimiters() {
        // "Wait... What?! Really?? Yes!!!"
        val result = calculator.calculate("Wait... What?! Really?? Yes!!!")
        assertEquals(4, result.sentenceCount)
        assertEquals(4, result.wordCount)
        // Punctuation: ... (3) + ?! (2) + ?? (2) + !!! (3) = 10
        assertEquals(10, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testNewlineSeparatedSentences() {
        val text = "First line without period\nSecond line with dot.\nThird line!"
        val result = calculator.calculate(text)
        assertEquals(3, result.sentenceCount)
        assertEquals(10, result.wordCount)
        assertEquals(2, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testTypographicQuotesAndContractions() {
        // Using Unicode curly apostrophe ’ and curly quotes “ ”
        val text = "“Don’t worry, it’s all right,” she whispered."
        val result = calculator.calculate(text)
        assertEquals(1, result.sentenceCount)
        // Words: Don’t, worry, it’s, all, right, she, whispered -> 7 words
        assertEquals(7, result.wordCount)
        // Punctuation: “ (initial quote), ’ (final quote), , (other), ’ (final quote), , (other), ” (final quote), . (other) -> 7
        assertEquals(7, result.punctuationCount)
        assertEquals(0, result.numberCount)
    }

    @Test
    fun testAllUnicodePunctuationCategories() {
        // Test characters representing every Unicode category checked in isPunctuation:
        // CONNECTOR_PUNCTUATION: _
        // DASH_PUNCTUATION: - (hyphen), — (em-dash), – (en-dash)
        // START_PUNCTUATION: (, [, {, «
        // END_PUNCTUATION: ), ], }, »
        // INITIAL_QUOTE_PUNCTUATION: ‘, “
        // FINAL_QUOTE_PUNCTUATION: ’, ”
        // OTHER_PUNCTUATION: ., ,, !, ?, :, ;, /, \
        val testPunctuation = "_ - — – ( [ { « ) ] } » ‘ “ ’ ” . , ! ? : ; / \\"
        val count = calculator.countPunctuationMarks(testPunctuation)
        assertEquals(24, count)
    }

    @Test
    fun testEnglishSentenceWithNumbersAndPunctuation() {
        val text = "Hello world! Testing 2026!"
        val result = calculator.calculate(text)
        assertEquals(2, result.sentenceCount)
        assertEquals(3, result.wordCount)
        assertEquals(2, result.punctuationCount)
        assertEquals(4, result.numberCount)
    }

    @Test
    fun testIsolatedCountSentences() {
        assertEquals(0, calculator.countSentences(""))
        assertEquals(0, calculator.countSentences("   \n\t "))
        assertEquals(0, calculator.countSentences("... !? ---"))
        assertEquals(1, calculator.countSentences("Just one sentence without terminator"))
        assertEquals(1, calculator.countSentences("One sentence with period."))
        assertEquals(2, calculator.countSentences("Sentence one. Sentence two!"))
        assertEquals(3, calculator.countSentences("One.\nTwo!\nThree?"))
    }

    @Test
    fun testIsolatedCountWords() {
        assertEquals(0, calculator.countWords(""))
        assertEquals(0, calculator.countWords("   \t\n  "))
        assertEquals(0, calculator.countWords("12345 67890"))
        assertEquals(0, calculator.countWords("... --- !!!"))
        assertEquals(1, calculator.countWords("Word"))
        assertEquals(2, calculator.countWords("Hello World"))
        assertEquals(1, calculator.countWords("state-of-the-art"))
        assertEquals(2, calculator.countWords("They're don't"))
    }

    @Test
    fun testIsolatedCountPunctuationMarks() {
        assertEquals(0, calculator.countPunctuationMarks(""))
        assertEquals(0, calculator.countPunctuationMarks("   "))
        assertEquals(0, calculator.countPunctuationMarks("Alphabet123"))
        assertEquals(4, calculator.countPunctuationMarks("a, b; c: d."))
        assertEquals(8, calculator.countPunctuationMarks("«[({ })]»"))
        // Check non-punctuation symbols (currency, math symbols) are not counted as punctuation
        // $ and + and = are CURRENCY_SYMBOL or MATH_SYMBOL, not PUNCTUATION
        assertEquals(0, calculator.countPunctuationMarks("$ + = ^ ~ `"))
    }

    @Test
    fun testIsolatedCountNumbers() {
        assertEquals(0, calculator.countNumbers(""))
        assertEquals(0, calculator.countNumbers("   "))
        assertEquals(0, calculator.countNumbers("No digits here at all!"))
        assertEquals(5, calculator.countNumbers("12345"))
        assertEquals(6, calculator.countNumbers("Apartment 402, floor 3, room 19"))
    }
}
