# Word Counter App (Android)

A clean, modern Android application written in Kotlin that analyzes text in real time and calculates four distinct metrics: **sentences**, **words**, **punctuation marks**, and **numbers/digits**. Built with clean architecture principles, Material 3 design, complete architectural separation between UI and business logic, and a 100% localization-ready interface.

---

## Table of Contents

- [Overview](#overview)
- [Main Features](#main-features)
- [Calculated Metrics](#calculated-metrics)
- [Input Validation & Error Handling](#input-validation--error-handling)
- [Generate Test Sentence Feature](#generate-test-sentence-feature)
- [Runtime Calculation History](#runtime-calculation-history)
- [State Preservation Across Configuration Changes](#state-preservation-across-configuration-changes)
- [Unicode & International Language Support](#unicode--international-language-support)
- [Reusable TextMetricsCalculator Engine](#reusable-textmetricscalculator-engine)
- [String Resources & Localization-Ready UI](#string-resources--localization-ready-ui)
- [Project Structure](#project-structure)
- [Technologies & Dependencies](#technologies--dependencies)
- [How to Open and Run in Android Studio](#how-to-open-and-run-in-android-studio)
- [How to Use the Application (Step by Step)](#how-to-use-the-application-step-by-step)
- [Testing & Build Information](#testing--build-information)

---

## Overview

The **Word Counter App** provides an intuitive interface for evaluating text content. Users can type or paste custom text or load realistic test samples with one click. Upon calculation, the app updates four visual metric cards and records the calculation in a scrollable, in-memory session history feed.

---

## Main Features

- **Multi-Line Text Input & Immediate Calculation**: Type or paste arbitrary text into a flexible multi-line text editor with vertical scrolling.
- **Visual Metric Dashboard**: Four prominent, color-accented summary cards instantly display sentence, word, punctuation, and digit counts.
- **Empty Input Validation**: Prevents blank evaluations and notifies the user via an Android Toast alert.
- **Test Sentence Generator**: Includes a dedicated "Generate Test Sentence" button that cycles randomly through diverse pre-configured sample sentences.
- **Input Reset**: A dedicated "Clear Input" button to quickly empty the editor.
- **Runtime Calculation History**: Dynamically tracks calculations performed during the session in a clean, scrollable RecyclerView list, displaying both compact summaries and detailed breakdowns.
- **Configuration Change Resilience**: The input field preserves user input and cursor position through screen rotations and orientation changes via Android view hierarchy state restoration.
- **Decoupled Architecture**: All text analysis logic is encapsulated within a standalone Kotlin class (`TextMetricsCalculator`) that has zero dependencies on Android framework classes.
- **100% Resource-Backed UI Text**: Zero hardcoded strings in Kotlin code. All titles, button labels, badges, placeholders, formats, and messages reside in `res/values/strings.xml`.

---

## Calculated Metrics

The application accurately computes four specific metrics according to well-defined text-processing rules:

| Metric | Rule & Delimiter Logic | Example | Result |
| :--- | :--- | :--- | :--- |
| **Sentences** | Delimited by sentence-ending punctuation (`.`, `!`, `?` or clusters like `...`, `!?`) followed by whitespace/end-of-text, or newline characters (`\n`). Segments are only counted if they contain at least one letter or digit (`isLetterOrDigit()`). Phrases without terminal punctuation (e.g. `"Good morning"`) correctly evaluate to 1 sentence. | `"Hello world! How are you?"` | **2** sentences |
| **Words** | Extracted using Unicode regex `\b[\p{L}\p{M}]+(?:['’\-][\p{L}\p{M}]+)*\b`. Supports full Unicode letter categories (`\p{L}`) and marks (`\p{M}`). Hyphenated compound words (e.g., `state-of-the-art`) and contractions (e.g., `don't`, `isn't`) are counted as single words. | `"Don't stop the state-of-the-art run."` | **5** words |
| **Punctuation Marks** | Every punctuation mark is counted individually using standard Unicode character category classification (`Character.getType(c)`). Covers connectors (`_`), dashes (`-`), open/start punctuation (`(`, `[`, `{`, `¿`, `¡`), close/end punctuation (`)`, `]`, `}`), quotation marks (`'`, `"`, `«`, `»`), and other punctuation (`.`, `,`, `!`, `?`, `:`, `;`). | `"¿Cómo estás? ¡Muy bien!"` | **4** punctuation marks (`¿`, `?`, `¡`, `!`) |
| **Numbers / Digits** | Counts each individual numeric digit (`0-9`) as one number (`char in '0'..'9'`), matching the technical requirement specifications. | `"In 2026, room 402 had 3 pens."` | **8** digits (`2, 0, 2, 6, 4, 0, 2, 3`) |

---

## Empty Input Validation

When a user taps **Calculate**, the application validates the input:

```kotlin
val rawInput = binding.etInput.text?.toString() ?: ""

if (rawInput.trim().isEmpty()) {
    Toast.makeText(
        this,
        getString(R.string.error_empty_input),
        Toast.LENGTH_SHORT
    ).show()
    return
}
```

- If the input contains only whitespace or is empty, no calculation is triggered and no empty history card is added.
- The user receives an Android Toast notification: `"Please enter some text before calculating."` (loaded from `R.string.error_empty_input`).

---

## Generate Test Sentence Feature

To allow immediate verification without manual typing, the application includes a **Generate Test Sentence** button backed by `SampleTextProvider`:

- **Diverse Test Corpus**: Pre-configured with 6 sentences that exercise all four metrics simultaneously (multiple sentences, numbers, contractions, punctuation marks, percentages, and parentheses):
  1. `"Hello world! This is a test sentence with 3 apples, 10 bananas, and 2 pears. How wonderful is that?"`
  2. `"In 2026, artificial intelligence processed 1,000,000 queries per second; incredible, isn't it?"`
  3. `"Good morning! Are you ready for room 402? Don't forget your 3 notebooks and 2 pens."`
  4. `"The quick brown fox jumps over 12 lazy dogs! Is that really 100% true? Yes, it is."`
  5. `"Chapter 1: The journey begins at 08:30. Bring 5 tickets, (2) passports, and 1 backpack!"`
  6. `"Alpha, Bravo, and Charlie collected 42 points in game 7. Did they win? Yes!"`
- **Smart Random Selection**: Consecutive clicks avoid selecting the same test sentence consecutively.
- **Convenience**: Populates the `EditText`, automatically positions the cursor at the end of the text, and displays a Toast: `"Sample test sentence loaded."` (`R.string.test_sentence_loaded`).

---

## Runtime Calculation History

The app maintains an in-memory session history of all calculations performed during the current run:

- **RecyclerView Implementation**: Implemented with `HistoryAdapter` and `item_history.xml`.
- **Chronological Stacking**: New calculation records are prepended to the top of the list (`addEntry(0, entry)`).
- **Dual Representation per Entry**:
  - **Original Text**: The exact text analyzed (`"Text: %1$s"`).
  - **Compact Summary**: Formatted representation matching the pattern `"Original text" -> S, W, P, N` (e.g., `"Good morning" -> 1, 2, 0, 0`).
  - **Detailed Breakdown**: Formatted breakdown string: `Sentences: %1$d | Words: %2$d | Punctuation: %3$d | Numbers: %4$d`.
- **Empty State Placeholder**: When no calculations have been executed yet, a friendly placeholder TextView displays `"No calculations performed yet."` (`R.string.history_empty_placeholder`). Once the first calculation occurs, the placeholder hides and the RecyclerView appears.
- **Session Lifecycle**: History is kept in memory during the app's process lifetime and resets when the application is restarted.

---

## State Preservation Across Configuration Changes

The application handles Android configuration changes (such as device orientation changes between portrait and landscape mode):

- **Input Text Retention**: The multi-line input field (`EditText` with ID `@+id/etInput`) retains its content and cursor selection across activity recreation automatically via Android's built-in view hierarchy state restoration (`onSaveInstanceState`/`onRestoreInstanceState`).
- **Responsive Layout**: Wrapped inside a `NestedScrollView` to ensure that both the input card, metric badges, and history list remain fully accessible in landscape mode and on smaller screen sizes.

---

## Unicode & International Language Support

Text analysis in `TextMetricsCalculator` is built from the ground up for Unicode compliance:

- **Universal Word Counting**: Uses Unicode character classes `\p{L}` (any Unicode letter) and `\p{M}` (Unicode mark/accent characters) instead of standard ASCII `\w` or `[a-zA-Z]`.
  - Works with Spanish, French, German, and Portuguese accented characters (e.g., `¿Cómo estás?`, `très bien`, `Überraschung`).
  - Works with non-Latin scripts including Cyrillic (e.g., `Привет мир`), Greek (e.g., `Γειά σου κόσμε`), Hebrew, Arabic, and other Unicode alphabets.
- **International Punctuation**: Evaluates characters via `Character.getType(c)`:
  - Supports opening/inverted punctuation: `¿` (`START_PUNCTUATION`), `¡` (`OTHER_PUNCTUATION`).
  - Supports typographer's quotes (`‘`, `’`, `“`, `”`, `«`, `»`) and dashes (`—`, `–`).

---

## Reusable TextMetricsCalculator Engine

The core text analysis engine is completely isolated from Android-specific components:

- **Class**: `com.example.wordcounter.analyzer.TextMetricsCalculator`
- **Zero Android Dependencies**: Does not import `android.*` packages, enabling effortless unit testing on local JVMs without Robolectric or an emulator.
- **Portable**: Can be directly reused in Kotlin Multiplatform, backend JVM services, or CLI utilities.
- **Public API**:
  - `calculate(input: String?): TextMetrics` – Main entry point returning an immutable `TextMetrics` data class.
  - `countSentences(text: String): Int` – Calculates sentence count.
  - `countWords(text: String): Int` – Calculates word count with contraction and hyphenation support.
  - `countPunctuationMarks(text: String): Int` – Calculates Unicode punctuation count.
  - `countNumbers(text: String): Int` – Counts individual numeric digits (`0-9`).

---

## String Resources & Localization-Ready UI

All user-visible text is centralized in `app/src/main/res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">Word Counter App</string>
    <string name="input_label">Input Text</string>
    <string name="input_hint">Enter or paste your text here…</string>
    <string name="btn_calculate">Calculate</string>
    <string name="btn_generate_test_sentence">Generate Test Sentence</string>
    <string name="btn_clear_input">Clear Input</string>

    <string name="header_current_results">Current Results</string>
    <string name="metric_sentences_label">Sentences</string>
    <string name="metric_words_label">Words</string>
    <string name="metric_punctuation_label">Punctuation</string>
    <string name="metric_numbers_label">Numbers</string>
    <string name="metric_default_value">0</string>

    <string name="header_history">Calculation History (Current Session)</string>
    <string name="history_empty_placeholder">No calculations performed yet.</string>
    <string name="history_item_text_label">Text: %1$s</string>
    <string name="history_item_metrics_format">Sentences: %1$d | Words: %2$d | Punctuation: %3$d | Numbers: %4$d</string>
    <string name="history_item_compact_format">&quot;%1$s&quot; -&gt; %2$d, %3$d, %4$d, %5$d</string>

    <string name="error_empty_input">Please enter some text before calculating.</string>
    <string name="test_sentence_loaded">Sample test sentence loaded.</string>
</resources>
```

- **Positional Format Specifiers**: History formatting strings use `%1$s`, `%1$d`, `%2$d`, etc., guaranteeing safe translations where word orders vary across languages.
- **Zero Hardcoded Strings**: Neither `MainActivity.kt` nor `HistoryAdapter.kt` contains hardcoded English text.
- **Ready for Translation**: Translators can create `values-es`, `values-de`, or `values-fr` resource directories without touching application logic.

---

## Project Structure

```text
C:\Users\Administrator\Documents\WordCounterApp/
├── build.gradle.kts                          # Top-level Gradle configuration (AGP 8.5.2, Kotlin 1.9.24)
├── settings.gradle.kts                       # Repository management and project inclusion
├── gradle.properties                         # Gradle JVM options and AndroidX flags
├── gradlew / gradlew.bat                     # Gradle wrapper executable scripts
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties             # Configured for Gradle 8.7
└── app/
    ├── build.gradle.kts                      # App module build script (ViewBinding, dependencies)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml           # App manifest and launcher configuration
        │   ├── java/com/example/wordcounter/
        │   │   ├── analyzer/
        │   │   │   └── TextMetricsCalculator.kt  # Standalone, reusable Kotlin text analysis engine
        │   │   ├── data/
        │   │   │   └── SampleTextProvider.kt     # Predefined sample test sentences provider
        │   │   ├── model/
        │   │   │   ├── TextMetrics.kt            # Data model holding the 4 metrics
        │   │   │   └── HistoryEntry.kt           # Data model representing a history item
        │   │   └── ui/
        │   │       ├── MainActivity.kt           # Primary Activity and UI controller
        │   │       └── HistoryAdapter.kt         # RecyclerView Adapter for calculation history
        │   └── res/
        │       ├── drawable/
        │       │   ├── bg_card.xml               # Rounded surface card drawable
        │       │   └── bg_metric_badge.xml       # Rounded metric badge container drawable
        │       ├── layout/
        │       │   ├── activity_main.xml         # Main screen layout (NestedScrollView, cards, grid, list)
        │       │   └── item_history.xml          # Calculation history item card layout
        │       ├── values/
        │       │   ├── colors.xml                # Color palette (Primary Indigo, Slate neutrals)
        │       │   ├── strings.xml               # Centralized, localized UI string resources
        │       │   └── themes.xml                # Material 3 light application theme
        │       └── values-night/
        │           └── themes.xml                # Material 3 dark application theme
        └── test/java/com/example/wordcounter/
            └── TextMetricsCalculatorTest.kt      # Unit test suite covering calculation logic & Unicode
```

---

## Technologies & Dependencies

- **Language**: [Kotlin](https://kotlinlang.org/) `1.9.24`
- **JDK Target**: Java `1.8` compatible (built with JDK 17/21)
- **Minimum SDK**: Android API `24` (Android 7.0 Nougat)
- **Target & Compile SDK**: Android API `34` (Android 14 UpsideDownCake)
- **Build System**: [Gradle](https://gradle.org/) `8.7` with Android Gradle Plugin (AGP) `8.5.2`
- **UI & Layout**:
  - Android ViewBinding
  - [Google Material Components 3](https://github.com/material-components/material-components-android) (`com.google.android.material:material:1.12.0`)
  - AndroidX AppCompat (`androidx.appcompat:appcompat:1.7.0`)
  - AndroidX Core KTX (`androidx.core:core-ktx:1.13.1`)
  - AndroidX ConstraintLayout (`androidx.constraintlayout:constraintlayout:2.1.4`)
  - AndroidX RecyclerView (`androidx.recyclerview:recyclerview:1.3.2`)
- **Unit Testing**: JUnit 4 (`junit:junit:4.13.2`)

---

## How to Open and Run in Android Studio

1. **Launch Android Studio**.
2. Click **Open** (or choose **File -> Open...** from the top menu).
3. Navigate to and select the project root folder:
   ```text
   C:\Users\Administrator\Documents\WordCounterApp
   ```
4. Click **OK** to open the project.
5. **Gradle Sync**: Android Studio will automatically recognize the Gradle build files and sync dependencies. Ensure that the Gradle JDK in Android Studio is configured to JDK 17 or JDK 21:
   - Go to **File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle**.
   - Under **Gradle JDK**, select a valid **JDK 17** or **JDK 21** (e.g. Android Studio's bundled runtime or OpenJDK 21).
6. **Select Target Device**: From the device dropdown on the top toolbar, select an Android Virtual Device (AVD Emulator running API 24+) or a physical device connected with USB debugging enabled.
7. **Run the App**: Click the green **Run ▶** button (or press `Shift + F10`).
8. The application will compile, deploy, and launch on your target device.

---

## How to Use the Application (Step by Step)

1. **Enter Text**:
   - Tap on the **Input Text** field.
   - Type or paste the content you wish to analyze.
2. **Or Generate a Test Sentence**:
   - Alternatively, tap the **Generate Test Sentence** button.
   - A pre-configured sample sentence containing sentences, words, numbers, and punctuation will instantly populate the input area.
3. **Calculate Metrics**:
   - Tap the blue **Calculate** button.
   - If the input is empty or contains only spaces, a notification will appear reminding you to enter text.
   - If the text is valid, the calculation executes immediately.
4. **View the 4 Metrics**:
   - Inspect the **Current Results** dashboard:
     - **Sentences**: Count of identified sentences.
     - **Words**: Count of recognized words (including contractions and hyphenated compounds).
     - **Punctuation**: Total count of all punctuation symbols.
     - **Numbers**: Total count of individual numeric digits (0-9).
5. **Inspect Session History**:
   - Scroll down to the **Calculation History (Current Session)** section.
   - Each analysis is recorded as a card showing:
     - The original text.
     - A compact format: `"Text" -> Sentences, Words, Punctuation, Numbers`.
     - A detailed breakdown with labels.
6. **Clear or Continue**:
   - Tap **Clear Input** to empty the text field and start fresh with new text.
   - Prior calculation cards remain preserved in the history list for your reference during the session.

---

## Testing & Build Information

### Running Unit Tests

The test suite in `app/src/test/java/com/example/wordcounter/TextMetricsCalculatorTest.kt` verifies the calculation logic against various edge cases:
- Empty, null, and blank whitespace inputs.
- Single phrases without terminal punctuation (e.g. `"Good morning"`).
- Mixed sentences with numbers and punctuation marks.
- Hyphenated words and contractions (`"state-of-the-art"`, `"Don't"`).
- International/Unicode punctuation and accents (`"¿Cómo estás? ¡Muy bien!"`).

#### Run Tests via Android Studio:
- In the Project view, navigate to `app/src/test/java/com/example/wordcounter/TextMetricsCalculatorTest.kt`.
- Right-click the file and select **Run 'TextMetricsCalculatorTest'** (or press `Ctrl + Shift + F10`).

#### Run Tests via Command Line:
Open PowerShell or Command Prompt, ensure `JAVA_HOME` points to JDK 17 or JDK 21, and run:
```powershell
cd C:\Users\Administrator\Documents\WordCounterApp
.\gradlew test
```

### Building the APK

To assemble a debug APK for deployment:
```powershell
cd C:\Users\Administrator\Documents\WordCounterApp
.\gradlew assembleDebug
```
The output APK file will be located at:
```text
app\build\outputs\apk\debug\app-debug.apk
```
