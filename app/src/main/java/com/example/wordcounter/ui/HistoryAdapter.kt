package com.example.wordcounter.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordcounter.R
import com.example.wordcounter.databinding.ItemHistoryBinding
import com.example.wordcounter.model.HistoryEntry

/**
 * RecyclerView adapter for displaying calculation history entries.
 */
class HistoryAdapter(
    private val entries: MutableList<HistoryEntry> = mutableListOf()
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val entry = entries[position]
        val context = holder.itemView.context
        val metrics = entry.metrics

        // Display original text using string resource
        holder.binding.tvHistoryOriginalText.text = context.getString(
            R.string.history_item_text_label,
            entry.originalText
        )

        // Display compact summary (e.g. "Good morning" → 1, 2, 0, 0)
        holder.binding.tvHistoryCompactSummary.text = context.getString(
            R.string.history_item_compact_format,
            entry.originalText,
            metrics.sentenceCount,
            metrics.wordCount,
            metrics.punctuationCount,
            metrics.numberCount
        )

        // Display detailed metrics breakdown using string resource
        holder.binding.tvHistoryMetricsBreakdown.text = context.getString(
            R.string.history_item_metrics_format,
            metrics.sentenceCount,
            metrics.wordCount,
            metrics.punctuationCount,
            metrics.numberCount
        )
    }

    override fun getItemCount(): Int = entries.size

    /**
     * Adds a new entry at the top of the history list and notifies the adapter.
     */
    fun addEntry(entry: HistoryEntry) {
        entries.add(0, entry)
        notifyItemInserted(0)
    }

    /**
     * Clears all history entries.
     */
    fun clearEntries() {
        val count = entries.size
        entries.clear()
        notifyItemRangeRemoved(0, count)
    }

    /**
     * Returns true if there are no history entries.
     */
    fun isEmpty(): Boolean = entries.isEmpty()
}
