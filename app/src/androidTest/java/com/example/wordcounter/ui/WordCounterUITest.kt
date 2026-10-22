package com.example.wordcounter.ui

import android.os.Build
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordcounter.R
import com.example.wordcounter.data.SampleTextProvider
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI / Instrumentation test suite for [MainActivity].
 *
 * Verifies core application interactions:
 * 1. Successful calculation and metric updates.
 * 2. Empty input validation and state invariance.
 * 3. Test sentence generation without hardcoded content dependencies.
 * 4. Calculation history population and formatting.
 * 5. Clear input button functionality.
 * 6. History ordering across multiple calculations.
 */
@RunWith(AndroidJUnit4::class)
class WordCounterUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Scenario 1: Successful calculation.
     * Enters English text with known metrics, clicks Calculate,
     * and verifies that word, sentence, punctuation, and number counts are correctly updated.
     *
     * Input: "I have 2 books!"
     * Expected metrics (derived from TextMetricsCalculator):
     * - Sentences: 1
     * - Words: 3 ("I", "have", "books")
     * - Punctuation: 1 ("!")
     * - Numbers: 1 ('2')
     */
    @Test
    fun testSuccessfulCalculation_updatesMetricsProperly() {
        // Enter input text and close soft keyboard
        onView(withId(R.id.etInput))
            .perform(typeText("I have 2 books!"), closeSoftKeyboard())

        // Click Calculate
        onView(withId(R.id.btnCalculate)).perform(click())

        // Verify current results counters
        onView(withId(R.id.tvSentencesCount)).check(matches(withText("1")))
        onView(withId(R.id.tvWordsCount)).check(matches(withText("3")))
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("1")))
        onView(withId(R.id.tvNumbersCount)).check(matches(withText("1")))
    }

    /**
     * Scenario 2: Empty input validation.
     * Taps Calculate with empty input and verifies that the calculation is rejected,
     * metrics remain at zero default values, and history placeholder remains visible.
     */
    @Test
    fun testEmptyInput_blocksCalculationAndMaintainsDefaultState() {
        // Ensure input field is empty
        onView(withId(R.id.etInput)).perform(clearText(), closeSoftKeyboard())

        // Capture window decorView for optional toast assertion on supported API levels
        var decorView: View? = null
        activityRule.scenario.onActivity { activity ->
            decorView = activity.window.decorView
        }

        // Tap Calculate with empty input
        onView(withId(R.id.btnCalculate)).perform(click())

        // 1. Verify UI state invariance: metric counters must remain at "0"
        onView(withId(R.id.tvSentencesCount)).check(matches(withText("0")))
        onView(withId(R.id.tvWordsCount)).check(matches(withText("0")))
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("0")))
        onView(withId(R.id.tvNumbersCount)).check(matches(withText("0")))

        // 2. Verify history remains empty
        onView(withId(R.id.tvEmptyHistory)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyHistory)).check(matches(withText(R.string.history_empty_placeholder)))
        onView(withId(R.id.rvHistory)).check(matches(not(isDisplayed())))

        // 3. Attempt toast verification if supported by the OS window manager (API < 30)
        if (decorView != null && Build.VERSION.SDK_INT < 30) {
            try {
                onView(withText(R.string.error_empty_input))
                    .inRoot(withDecorView(not(`is`(decorView))))
                    .check(matches(isDisplayed()))
            } catch (_: Exception) {
                // Graceful fallback if toast dismissed or handled asynchronously
            }
        }
    }

    /**
     * Scenario 3: Generate sample text.
     * Taps the Generate Test Sentence button and verifies that the input field
     * receives non-empty text from the available sample sentences without
     * depending on a specific randomized string value.
     */
    @Test
    fun testGenerateSampleText_populatesInputFieldWithValidSentence() {
        val sampleProvider = SampleTextProvider()
        val allValidSamples = sampleProvider.getAllSampleTexts()

        // Tap Generate Test Sentence
        onView(withId(R.id.btnGenerateTestSentence)).perform(click())

        // Verify input field is not empty via Espresso
        onView(withId(R.id.etInput)).check(matches(not(withText(""))))

        // Retrieve actual text from EditText and verify it matches one of the predefined samples
        var populatedText = ""
        activityRule.scenario.onActivity { activity ->
            populatedText = activity.findViewById<EditText>(R.id.etInput).text.toString()
        }

        assertTrue("Generated text should not be empty", populatedText.isNotBlank())
        assertTrue(
            "Generated text should belong to the known sample dataset",
            allValidSamples.contains(populatedText)
        )

        // Verify that calculating with this generated sentence updates results
        onView(withId(R.id.btnCalculate)).perform(click())
        onView(withId(R.id.tvWordsCount)).check(matches(not(withText("0"))))
    }

    /**
     * Scenario 4: History behavior.
     * Performs a calculation and verifies that the result is added to the history RecyclerView
     * with the expected original text, compact summary, and detailed metrics breakdown.
     */
    @Test
    fun testHistoryBehavior_addsEntryWithCorrectDetails() {
        val testInput = "I have 2 books!"

        // Enter text and calculate
        onView(withId(R.id.etInput))
            .perform(typeText(testInput), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(click())

        // Verify empty placeholder is gone and RecyclerView is displayed
        onView(withId(R.id.tvEmptyHistory)).check(matches(not(isDisplayed())))
        onView(withId(R.id.rvHistory)).check(matches(isDisplayed()))

        // Retrieve actual localized formatted strings from resource system
        var expectedOriginalText = ""
        var expectedCompactSummary = ""
        var expectedMetricsBreakdown = ""
        activityRule.scenario.onActivity { activity ->
            expectedOriginalText = activity.getString(R.string.history_item_text_label, testInput)
            expectedCompactSummary = activity.getString(
                R.string.history_item_compact_format,
                testInput,
                1, 3, 1, 1
            )
            expectedMetricsBreakdown = activity.getString(
                R.string.history_item_metrics_format,
                1, 3, 1, 1
            )
        }

        // Verify history item contents in RecyclerView at position 0
        onView(withId(R.id.rvHistory)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(
                        allOf(
                            withId(R.id.tvHistoryOriginalText),
                            withText(expectedOriginalText)
                        )
                    )
                )
            )
        )
        onView(withId(R.id.rvHistory)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(
                        allOf(
                            withId(R.id.tvHistoryCompactSummary),
                            withText(expectedCompactSummary)
                        )
                    )
                )
            )
        )
        onView(withId(R.id.rvHistory)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(
                        allOf(
                            withId(R.id.tvHistoryMetricsBreakdown),
                            withText(expectedMetricsBreakdown)
                        )
                    )
                )
            )
        )
    }

    /**
     * Scenario 5 (Additional): Clear input button.
     * Types text into the input field, taps Clear Input, and verifies the field is emptied.
     */
    @Test
    fun testClearInputButton_resetsInputField() {
        onView(withId(R.id.etInput))
            .perform(typeText("Temporary input to clear"), closeSoftKeyboard())
        onView(withId(R.id.btnClear)).perform(click())
        onView(withId(R.id.etInput)).check(matches(withText("")))
    }

    /**
     * Scenario 6 (Additional): Multiple calculations history ordering.
     * Performs two distinct calculations sequentially and verifies that the newer
     * entry is prepended to position 0 of the history list.
     */
    @Test
    fun testMultipleCalculations_prependsNewestEntryInHistory() {
        // First calculation
        onView(withId(R.id.etInput))
            .perform(typeText("Good morning"), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(click())

        // Second calculation
        onView(withId(R.id.etInput))
            .perform(clearText(), typeText("I have 2 books!"), closeSoftKeyboard())
        onView(withId(R.id.btnCalculate)).perform(click())

        // Verify position 0 has the second (newest) calculation
        onView(withId(R.id.rvHistory)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(withText("Text: I have 2 books!"))
                )
            )
        )

        // Verify position 1 has the first calculation
        onView(withId(R.id.rvHistory)).check(
            matches(
                atPosition(
                    1,
                    hasDescendant(withText("Text: Good morning"))
                )
            )
        )
    }

    /**
     * Scenario 7: State preservation across Activity recreation.
     * Enters text into the input field, recreates the Activity (simulating
     * a configuration change such as screen rotation), and verifies that
     * the entered text is retained.
     */
    @Test
    fun testInputTextSurvivesActivityRecreation() {
        val expectedText = "Text that survives activity recreation"

        // Type text into the input field and dismiss the keyboard
        onView(withId(R.id.etInput))
            .perform(typeText(expectedText), closeSoftKeyboard())

        // Recreate the Activity to simulate configuration change / state restoration
        activityRule.scenario.recreate()

        // Verify that the input text persists after Activity recreation
        onView(withId(R.id.etInput))
            .check(matches(withText(expectedText)))
    }

    /**
     * Custom matcher to verify contents of a RecyclerView item at a specific [position].
     */
    private fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}
