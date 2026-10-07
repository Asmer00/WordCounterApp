package com.example.wordcounter.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordcounter.R
import com.example.wordcounter.analyzer.TextMetricsCalculator
import com.example.wordcounter.data.SampleTextProvider
import com.example.wordcounter.databinding.ActivityMainBinding
import com.example.wordcounter.model.HistoryEntry

/**
 * Main Activity for the Word Counter App.
 *
 * Coordinates user input, triggers text analysis via [TextMetricsCalculator],
 * displays current metrics, and maintains an in-memory runtime history list.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Reusable analyzer and data provider classes (separated from UI logic)
    private val calculator = TextMetricsCalculator()
    private val sampleTextProvider = SampleTextProvider()
    private val historyAdapter = HistoryAdapter()

    private var nextHistoryId: Long = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter
        updateHistoryVisibility()
    }

    private fun setupListeners() {
        // Calculate Button
        binding.btnCalculate.setOnClickListener {
            performCalculation()
        }

        // Generate Test Sentence Button
        binding.btnGenerateTestSentence.setOnClickListener {
            insertTestSentence()
        }

        // Clear Input Button
        binding.btnClear.setOnClickListener {
            clearInput()
        }
    }

    /**
     * Validates input, calculates text metrics, and updates UI and history.
     */
    private fun performCalculation() {
        val rawInput = binding.etInput.text?.toString() ?: ""

        // Validate non-empty input using localized string resource
        if (rawInput.trim().isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.error_empty_input),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Calculate metrics using the separate analyzer module
        val metrics = calculator.calculate(rawInput)

        // Update current results cards
        binding.tvSentencesCount.text = metrics.sentenceCount.toString()
        binding.tvWordsCount.text = metrics.wordCount.toString()
        binding.tvPunctuationCount.text = metrics.punctuationCount.toString()
        binding.tvNumbersCount.text = metrics.numberCount.toString()

        // Create history record and prepend to history list
        val entry = HistoryEntry(
            id = nextHistoryId++,
            originalText = rawInput,
            metrics = metrics
        )
        historyAdapter.addEntry(entry)
        updateHistoryVisibility()
    }

    /**
     * Randomly picks a test sentence and inserts it into the EditText.
     */
    private fun insertTestSentence() {
        val sample = sampleTextProvider.getRandomSampleText()
        binding.etInput.setText(sample)
        binding.etInput.setSelection(sample.length)

        Toast.makeText(
            this,
            getString(R.string.test_sentence_loaded),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Clears user input in the EditText.
     */
    private fun clearInput() {
        binding.etInput.text?.clear()
    }

    /**
     * Updates visibility between the empty placeholder and RecyclerView.
     */
    private fun updateHistoryVisibility() {
        if (historyAdapter.isEmpty()) {
            binding.tvEmptyHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvEmptyHistory.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }
}
