package com.example.wordcounter.data

import kotlin.random.Random

/**
 * Provides diverse predefined test sentences containing sentences, words,
 * punctuation marks, and numbers.
 */
class SampleTextProvider(private val random: Random = Random.Default) {

    private val sampleTexts: List<String> = listOf(
        "Hello world! This is a test sentence with 3 apples, 10 bananas, and 2 pears. How wonderful is that?",
        "In 2026, artificial intelligence processed 1,000,000 queries per second; incredible, isn't it?",
        "Good morning! Are you ready for room 402? Don't forget your 3 notebooks and 2 pens.",
        "The quick brown fox jumps over 12 lazy dogs! Is that really 100% true? Yes, it is.",
        "Chapter 1: The journey begins at 08:30. Bring 5 tickets, (2) passports, and 1 backpack!",
        "Alpha, Bravo, and Charlie collected 42 points in game 7. Did they win? Yes!"
    )

    private var lastIndex: Int = -1

    /**
     * Returns a randomly chosen sample text. Ensures that consecutive calls
     * prefer a different sample text when multiple options are available.
     */
    fun getRandomSampleText(): String {
        if (sampleTexts.size <= 1) return sampleTexts.first()

        var nextIndex = random.nextInt(sampleTexts.size)
        while (nextIndex == lastIndex) {
            nextIndex = random.nextInt(sampleTexts.size)
        }
        lastIndex = nextIndex
        return sampleTexts[nextIndex]
    }

    /**
     * Returns all available sample texts.
     */
    fun getAllSampleTexts(): List<String> = sampleTexts
}
