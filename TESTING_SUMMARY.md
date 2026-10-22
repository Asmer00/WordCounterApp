# Android App Testing Summary: Word Counter Application

**Project Location:** `C:\Users\Administrator\Desktop\WordCounterApp-master`  
**Application Name:** Word Counter App (`com.example.wordcounter`)  
**Build System:** Gradle 8.7 & Android Gradle Plugin (AGP) 8.5.2  
**Target Platform:** Android SDK 34 (Android 14) | Min SDK: 24 (Android 7.0)  
**Programming Language:** Kotlin 1.9.24 | JDK Target: Java 1.8  

---

## 1. Executive Summary & Purpose

### 1.1 Purpose of the Testing Assignment
The primary objective of this testing assignment is to establish an end-to-end quality assurance suite for the **Word Counter** Android application. This entails:
- Validating the core analytical engine (`TextMetricsCalculator`) for accuracy across diverse textual patterns, edge cases, boundaries, and international Unicode scripts.
- Verifying user interface behaviors, input validation rules, and dynamic state updates using automated instrumentation tests on Android runtime.
- Ensuring configuration change resilience (e.g., orientation changes and Activity recreation) and view hierarchy state retention without data loss.
- Measuring and maintaining high code coverage standards using JaCoCo, targeting complete coverage of business logic, models, and data providers.

### 1.2 Summary of Test Results

| Test Category | Suite Count | Total Tests | Passed | Failed | Skipped | Success Rate | Execution Time |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Unit Tests (Local JVM)** | 4 Test Classes | **34** | **34** | **0** | 0 | **100%** | ~0.20 s |
| **UI Tests (Espresso/Device)** | 1 Test Class | **7** | **7** | **0** | 0 | **100%** | ~17.59 s |
| **Total Automated Tests** | **5 Test Suites** | **41** | **41** | **0** | **0** | **100%** | **~17.79 s** |

---

## 2. Unit Testing Report (34 Passed, 0 Failures)

All 34 unit tests run as local JVM tests without dependencies on the Android operating system or emulators. They execute in approximately **0.197 seconds**.

```
Test Summary: 34 tests, 0 failures, 0 ignored, 100% successful
```

### 2.1 Test Suite Breakdown

#### A. `TextMetricsCalculatorTest` (17 Tests Passed)
Package: `com.example.wordcounter`  
Execution Time: `~0.105 s`

Tests the isolated text analysis engine for all four calculated metrics:
1. `testEmptyAndBlankInput`: Confirms that `null`, empty strings `""`, and whitespace strings (`" \n\t "`) produce `TextMetrics(0, 0, 0, 0)`.
2. `testSimplePhraseWithoutTrailingPunctuation`: Validates that phrases without terminal punctuation (e.g., `"Good morning"`) correctly evaluate to 1 sentence, 2 words, 0 punctuation, and 0 digits.
3. `testSentenceWithNumberAndPunctuation`: Tests input `"I have 2 books!"` -> verifies 1 sentence, 3 words (`"I"`, `"have"`, `"books"`), 1 punctuation (`"!"`), 1 number (`'2'`).
4. `testMultiSentenceWithDiverseMetrics`: Evaluates `"Hello world! In 2024, did you read 15 books? Yes, I did."` -> verifies 3 sentences, 10 words, 5 punctuation marks, and 6 numeric digits.
5. `testHyphenatedAndContractionWords`: Tests compound and contracted words like `"Don't stop the state-of-the-art process."` -> correctly identified as 5 words (treating `"state-of-the-art"` and `"Don't"` as single words), 5 punctuation marks (`'`, `-`, `-`, `-`, `.`), and 1 sentence.
6. `testUnicodePunctuationSupport`: Evaluates Spanish inverted punctuation (`"¿Cómo estás? ¡Muy bien!"`) -> 2 sentences, 4 words, 4 punctuation marks (`¿`, `?`, `¡`, `!`).
7. `testPureDigitsInput`: Evaluates `"12345 67890"` -> verifies 10 numbers, 0 words, 1 sentence, 0 punctuation.
8. `testPurePunctuationInput`: Evaluates `"... !? ;;; --- :::"` -> verifies 0 sentences, 0 words, 14 punctuation marks, 0 numbers.
9. `testMultipleConsecutiveSentenceDelimiters`: Tests clustered terminators (`"Wait... What?! Really?? Yes!!!"`) -> 4 sentences, 4 words, 10 punctuation marks.
10. `testNewlineSeparatedSentences`: Verifies multiline inputs without punctuation (`"First line\nSecond line with dot.\nThird line!"`) -> 3 sentences, 10 words, 2 punctuation marks.
11. `testTypographicQuotesAndContractions`: Verifies curly quotes and typographer apostrophes (`“`, `’`, `”`) in `"“Don’t worry, it’s all right,” she whispered."` -> 1 sentence, 7 words, 7 punctuation marks.
12. `testAllUnicodePunctuationCategories`: Validates all Unicode categories handled by `isPunctuation` (`CONNECTOR`, `DASH`, `START`, `END`, `INITIAL_QUOTE`, `FINAL_QUOTE`, `OTHER_PUNCTUATION`).
13. `testEnglishSentenceWithNumbersAndPunctuation`: Evaluates `"Hello world! Testing 2026!"` -> 2 sentences, 3 words, 2 punctuation marks, 4 numbers.
14. `testIsolatedCountSentences`: Direct unit testing of `countSentences()` across null, empty, blank, single phrase, and multi-sentence cases.
15. `testIsolatedCountWords`: Direct unit testing of `countWords()` across empty, whitespace, pure digits, hyphens, and contractions.
16. `testIsolatedCountPunctuationMarks`: Direct unit testing of `countPunctuationMarks()`, confirming math/currency symbols (`$`, `+`, `=`) are excluded.
17. `testIsolatedCountNumbers`: Direct unit testing of `countNumbers()` across blank strings, word-only strings, pure digits, and mixed sentences.

#### B. `SampleTextProviderTest` (5 Tests Passed)
Package: `com.example.wordcounter.data`  
Execution Time: `~0.087 s`

Verifies the sample sentence generation repository:
1. `testDefaultConstructorAndGetAllSampleTexts`: Validates that the provider contains 6 non-blank, distinct predefined test sentences.
2. `testGetRandomSampleTextReturnsValidItem`: Repeats 20 randomized calls, ensuring every generated string exists within the verified corpus.
3. `testRandomCollisionAvoidanceWithControlledRandom`: Injects a deterministic `Random` implementation to verify the consecutive selection avoidance algorithm (`lastIndex != newIndex`).
4. `testSampleTextsContainDiverseContent`: Asserts that each configured sample sentence contains letters, numbers, and punctuation marks.
5. `testGetRandomSampleTextSingleItemBoundary`: Verifies boundary protection when the sample list size is $\le 1$.

#### C. `HistoryEntryTest` (6 Tests Passed)
Package: `com.example.wordcounter.model`  
Execution Time: `~0.003 s`

Verifies the calculation history data model:
1. `testConstructorWithExplicitTimestamp`: Verifies all fields are assigned properly when passing custom timestamps.
2. `testConstructorDefaultTimestamp`: Validates that the default constructor auto-populates `System.currentTimeMillis()`.
3. `testEqualsAndHashCode`: Asserts reflexivity, symmetry, property sensitivity (id, originalText, metrics, timestamp), and null/type mismatch handling.
4. `testToString`: Verifies informative string serialization.
5. `testCopyFunctionality`: Tests partial and complete data class copying.
6. `testDestructuring`: Ensures component destructuring declarations unpack attributes in correct order.

#### D. `TextMetricsTest` (6 Tests Passed)
Package: `com.example.wordcounter.model`  
Execution Time: `~0.002 s`

Verifies the core metric result model:
1. `testPropertyGetters`: Asserts correct values for `sentenceCount`, `wordCount`, `punctuationCount`, and `numberCount`.
2. `testEqualsAndHashCode`: Verifies value equality semantics across all four fields.
3. `testToString`: Validates readable string format.
4. `testCopyFunctionality`: Tests immutability and partial copies with updated parameters.
5. `testDestructuringDeclaration`: Tests positional destructuring: `val (s, w, p, n) = metrics`.
6. `testBoundaryValues`: Validates all-zero models and upper boundary limits (`Int.MAX_VALUE`).

---

## 3. UI / Instrumentation Testing Report (7 Passed, 0 Failures)

The UI test suite (`WordCounterUITest`) runs using **AndroidX Test** and **Espresso 3.6.1** on an active Android runtime environment (emulator or physical device).

```
Test Results: 7 tests, 0 failures, 0 skipped, 100% successful
Duration: 17.585 seconds
```

### 3.1 Test Scenarios & Verifications

| Test Method | Interaction / Scenario | Key Assertions & Verifications | Result |
| :--- | :--- | :--- | :--- |
| `testSuccessfulCalculation_updatesMetricsProperly` | Types `"I have 2 books!"` into `etInput` and clicks `btnCalculate`. | Confirms `tvSentencesCount = 1`, `tvWordsCount = 3`, `tvPunctuationCount = 1`, `tvNumbersCount = 1`. | **Passed** |
| `testEmptyInput_blocksCalculationAndMaintainsDefaultState` | Leaves `etInput` empty and clicks `btnCalculate`. | Verifies input rejection, metrics remain `0`, history remains empty, and `tvEmptyHistory` placeholder remains displayed. | **Passed** |
| `testGenerateSampleText_populatesInputFieldWithValidSentence` | Clicks `btnGenerateTestSentence`. | Verifies `etInput` is populated with a non-empty string belonging to the 6 predefined samples, and subsequent calculation succeeds. | **Passed** |
| `testHistoryBehavior_addsEntryWithCorrectDetails` | Inputs text, performs calculation, and inspects `rvHistory`. | Placeholder disappears; RecyclerView is visible. Item at position 0 matches original text, compact format (`"Text" -> S, W, P, N`), and breakdown format. | **Passed** |
| `testClearInputButton_resetsInputField` | Enters text into `etInput` and clicks `btnClear`. | Asserts `etInput` text is cleared (`""`). | **Passed** |
| `testMultipleCalculations_prependsNewestEntryInHistory` | Calculates `"Good morning"` followed by `"I have 2 books!"`. | Asserts newest calculation is prepended to position 0 and older calculation is shifted to position 1. | **Passed** |
| `testInputTextSurvivesActivityRecreation` | Enters `"Text that survives activity recreation"`, executes `activityRule.scenario.recreate()`. | **Configuration change / state preservation**: Verifies that user input in `etInput` persists intact across Activity recreation. | **Passed** |

---

## 4. JaCoCo Code Coverage Analysis

JaCoCo code coverage was gathered for debug unit test execution using the Gradle `jacocoTestReport` task.

### 4.1 Overall Coverage Metrics

| Metric Dimension | Missed | Total | Coverage Percentage |
| :--- | :---: | :---: | :---: |
| **Classes** | 0 | 4 | **100.00%** |
| **Methods** | 0 | 23 | **100.00%** |
| **Instructions** | 2 | 361 | **99.45%** |
| **Lines** | 1 | 65 | **98.46%** |
| **Branches** | 1 | 36 | **97.22%** |
| **Cyclomatic Complexity** | 1 | 41 | **97.56%** |

### 4.2 Package-by-Package Breakdown

```
-------------------------------------------------------------------------------------------------------------------
Package                              Missed/Total Instr.  Coverage   Missed/Total Branches  Missed/Total Lines
-------------------------------------------------------------------------------------------------------------------
com.example.wordcounter.analyzer                   2 / 195     98.97%                  1 / 32                 1 / 37
com.example.wordcounter.data                       0 / 94     100.00%                  0 / 4                  0 / 17
com.example.wordcounter.model                      0 / 72     100.00%                  0 / 0                  0 / 11
-------------------------------------------------------------------------------------------------------------------
Total                                              2 / 361     99.45%                  1 / 36                 1 / 65
-------------------------------------------------------------------------------------------------------------------
```

- **`com.example.wordcounter.model`**: 100% instructions (72/72), 100% lines (11/11), 100% methods (11/11).
- **`com.example.wordcounter.data`**: 100% instructions (94/94), 100% branches (4/4), 100% lines (17/17), 100% methods (4/4).
- **`com.example.wordcounter.analyzer`**: 98.97% instructions (193/195), 96.88% branches (31/32), 97.30% lines (36/37), 100% methods (8/8).

---

## 5. Tools and Testing Methodology

### 5.1 Tooling Stack
- **Testing Framework:** JUnit 4 (`4.13.2`) for JVM unit testing.
- **AndroidX Test Suite:**
  - `androidx.test.ext:junit:1.2.1` (AndroidX test runner and rule support)
  - `androidx.test:runner:1.6.1` & `androidx.test:rules:1.6.1`
  - `androidx.test.espresso:espresso-core:3.6.1` (UI interaction and assertion framework)
- **Lifecycle Management:** `ActivityScenarioRule<MainActivity>` allowing direct simulation of lifecycle events and configuration restarts (`scenario.recreate()`).
- **Code Coverage Tool:** JaCoCo `0.8.11` integrated via custom Gradle reporting task with filters excluding generated bytecode (`R`, `BuildConfig`, `databinding`).

### 5.2 Architectural & Testing Approaches
1. **Decoupled Architecture (Clean Architecture Principles):**
   - The analysis core (`TextMetricsCalculator`) has zero dependencies on Android framework classes (`android.*`). This allows the entire text calculation engine to be tested directly on the host JVM in milliseconds without requiring Robolectric or emulator startup overhead.
2. **Deterministic UI Assertions:**
   - Instead of matching hardcoded UI strings, tests resolve string resource IDs dynamically (`activity.getString(R.string...)`), ensuring that UI tests remain resilient against localization and string resource updates.
3. **RecyclerView Custom View Matching:**
   - Implemented a custom `BoundedMatcher<View, RecyclerView>` (`atPosition()`) to evaluate list items at specific adapter indices, verifying original text, compact summaries, and formatted breakdown strings independently.
4. **Resilience to Randomness:**
   - UI tests for the sample generator dynamically verify that the populated input belongs to the recognized sample dataset (`allValidSamples.contains(populatedText)`) rather than coupling to a single randomized sentence.

---

## 6. Development Requirements & Implementation Highlights

During development, several core technical specifications and user requests were addressed:

1. **Accurate Four-Metric Calculation Rules:**
   - **Sentences:** Delimited by sentence-ending punctuation (`.`, `!`, `?` or clusters) or newlines (`\n`). Single phrases without terminal punctuation (e.g., `"Good morning"`) evaluate to 1 sentence. Segments must contain at least one letter or digit.
   - **Words:** Extracted via Unicode regular expression `\b[\p{L}\p{M}]+(?:['’\-][\p{L}\p{M}]+)*\b`. Supports non-English alphabets, accented characters, contractions (`don't`), and hyphenated compound words (`state-of-the-art`).
   - **Punctuation Marks:** Comprehensive Unicode classification using `Character.getType(c)` covering connectors, dashes, start/end punctuation, quotes, and symbols.
   - **Numbers:** Individual numeric digits (`0-9`) counted per technical requirement specification (e.g., `"2026"` counts as 4 digits).
2. **Empty Input Validation:**
   - Blank or whitespace-only inputs are intercepted before calculation. A Toast message (`R.string.error_empty_input`) alerts the user, metric counters remain at zero, and no empty entries enter history.
3. **Generate Test Sentence Feature:**
   - A dedicated button populates the input editor with one of 6 pre-configured sample sentences exercising all four metrics simultaneously, ensuring consecutive clicks do not repeat the same sentence.
4. **Runtime Calculation History:**
   - Scrollable `RecyclerView` holding current session calculations in reverse-chronological order. Each card displays original text, compact summary (`"Text" -> S, W, P, N`), and a detailed breakdown.
5. **Configuration Change State Preservation:**
   - The primary input field (`EditText` with ID `@+id/etInput`) retains its content and cursor position across device rotations and Activity recreation via view hierarchy state restoration.
6. **Zero Hardcoded Strings & Localization Readiness:**
   - All UI labels, error toasts, and formatted strings are defined in `res/values/strings.xml` using positional parameters (`%1$s`, `%1$d`, `%2$d`), ready for international translations.

---

## 7. Known Limitations & Technical Analysis

### 7.1 Uncovered Branch in `TextMetricsCalculator.kt`
The JaCoCo coverage report indicates **1 missed branch**, **1 missed line**, and **2 missed instructions** inside `TextMetricsCalculator.kt`:

#### Code Under Review:
```kotlin
fun countSentences(text: String): Int {
    if (text.isBlank()) return 0

    // Split text by sentence terminators or line breaks
    val parts = text.split(SENTENCE_DELIMITER_REGEX)
    val validSentences = parts.filter { segment ->
        segment.any { it.isLetterOrDigit() }
    }

    // Line 67:
    return if (validSentences.isEmpty() && text.any { it.isLetterOrDigit() }) {
        1 // Line 68 (Unreachable)
    } else {
        validSentences.size
    }
}
```

#### Root Cause Analysis:
1. When input `text` contains alphanumeric characters but has **no terminal punctuation** (e.g., `"Good morning"`), `text.split(SENTENCE_DELIMITER_REGEX)` finds no delimiter matches and returns a single-element list containing the full string: `["Good morning"]`.
2. The subsequent filter (`segment.any { it.isLetterOrDigit() }`) evaluates to `true` for that segment, so `validSentences` contains `["Good morning"]`.
3. Consequently, `validSentences.isEmpty()` evaluates to **`false`**. The function immediately executes the `else` branch, returning `validSentences.size` (which is `1`).
4. If `text` contains **no** letters or digits (e.g., pure punctuation `"... !?"`), `validSentences.isEmpty()` is `true`, but `text.any { it.isLetterOrDigit() }` is **`false`**.
5. As a consequence, the condition `(validSentences.isEmpty() && text.any { it.isLetterOrDigit() })` can **never evaluate to `true`**. Line 68 (`return 1`) represents unreachable dead code.
6. This results in JaCoCo flagging 1 missed decision branch (out of 4 bytecode branches generated for the boolean expression) and 1 missed line of code, capping instruction coverage at 99.45% and branch coverage at 97.22%.

### 7.2 Other Architecture Limitations
- **In-Memory History Lifecycle:** Session history is maintained in an in-memory list within `MainActivity`. History resets upon application process termination and is not persisted in a local database (Room / SQLite) or DataStore.
- **Digit Counting Definition:** By design specification, numeric digits (`0-9`) are counted individually rather than as integer tokens (e.g., `"Room 402"` contributes 3 to the number count).
- **Unit Test Filter Scope:** Activity and Adapter classes (`MainActivity`, `HistoryAdapter`) depend directly on Android framework classes and are tested comprehensively via Espresso UI tests rather than JVM unit tests; they are appropriately excluded from the JaCoCo unit test task filter.

---

## 8. Conclusion

The Word Counter testing suite demonstrates rigorous quality assurance and engineering standards:
- **100% of automated tests pass** (34 JVM unit tests + 7 Espresso UI tests = 41 total tests).
- **JaCoCo instruction coverage reaches 99.45%**, with 100% class and method coverage across all non-UI application layers.
- Core functional requirements, empty input handling, international Unicode support, and lifecycle state preservation across Activity recreation have been conclusively verified.
