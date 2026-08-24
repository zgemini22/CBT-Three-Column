package com.threecolumn.cbt.data

import androidx.annotation.StringRes
import com.threecolumn.cbt.R

/**
 * The cognitive distortions popularized by David Burns' "Feeling Good".
 * Label/description text lives in strings.xml (original paraphrases, not
 * quotes from the book) so it's localized.
 *
 * Mind Reading and Fortune Telling were briefly split into separate entries;
 * they're merged back into a single "Jumping to Conclusions" entry (still
 * named MIND_READING for storage continuity). Records saved while they were
 * split may still have a lone "FORTUNE_TELLING" key on disk, so
 * [fromStorageKey] keeps resolving that string to MIND_READING.
 */
enum class CognitiveDistortion(@StringRes val labelRes: Int, @StringRes val descriptionRes: Int) {
    ALL_OR_NOTHING(R.string.distortion_all_or_nothing_label, R.string.distortion_all_or_nothing_desc),
    OVERGENERALIZATION(R.string.distortion_overgeneralization_label, R.string.distortion_overgeneralization_desc),
    MENTAL_FILTER(R.string.distortion_mental_filter_label, R.string.distortion_mental_filter_desc),
    DISCOUNTING_POSITIVE(R.string.distortion_discounting_positive_label, R.string.distortion_discounting_positive_desc),
    MIND_READING(R.string.distortion_mind_reading_label, R.string.distortion_mind_reading_desc),
    MAGNIFICATION_MINIMIZATION(R.string.distortion_magnification_minimization_label, R.string.distortion_magnification_minimization_desc),
    EMOTIONAL_REASONING(R.string.distortion_emotional_reasoning_label, R.string.distortion_emotional_reasoning_desc),
    SHOULD_STATEMENTS(R.string.distortion_should_statements_label, R.string.distortion_should_statements_desc),
    LABELING(R.string.distortion_labeling_label, R.string.distortion_labeling_desc),
    PERSONALIZATION(R.string.distortion_personalization_label, R.string.distortion_personalization_desc);

    companion object {
        private const val LEGACY_FORTUNE_TELLING_KEY = "FORTUNE_TELLING"

        fun fromStorageKey(key: String): CognitiveDistortion? =
            if (key == LEGACY_FORTUNE_TELLING_KEY) MIND_READING else entries.find { it.name == key }
    }
}
