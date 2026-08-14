package com.threecolumn.cbt.data

import androidx.annotation.StringRes
import com.threecolumn.cbt.R

/**
 * The ten cognitive distortions popularized by David Burns' "Feeling Good".
 * Label/description text lives in strings.xml (original paraphrases, not
 * quotes from the book) so it's localized.
 */
enum class CognitiveDistortion(@StringRes val labelRes: Int, @StringRes val descriptionRes: Int) {
    ALL_OR_NOTHING(R.string.distortion_all_or_nothing_label, R.string.distortion_all_or_nothing_desc),
    OVERGENERALIZATION(R.string.distortion_overgeneralization_label, R.string.distortion_overgeneralization_desc),
    MENTAL_FILTER(R.string.distortion_mental_filter_label, R.string.distortion_mental_filter_desc),
    DISCOUNTING_POSITIVE(R.string.distortion_discounting_positive_label, R.string.distortion_discounting_positive_desc),
    JUMPING_TO_CONCLUSIONS(R.string.distortion_jumping_to_conclusions_label, R.string.distortion_jumping_to_conclusions_desc),
    MAGNIFICATION_MINIMIZATION(R.string.distortion_magnification_minimization_label, R.string.distortion_magnification_minimization_desc),
    EMOTIONAL_REASONING(R.string.distortion_emotional_reasoning_label, R.string.distortion_emotional_reasoning_desc),
    SHOULD_STATEMENTS(R.string.distortion_should_statements_label, R.string.distortion_should_statements_desc),
    LABELING(R.string.distortion_labeling_label, R.string.distortion_labeling_desc),
    PERSONALIZATION(R.string.distortion_personalization_label, R.string.distortion_personalization_desc);

    companion object {
        fun fromStorageKey(key: String): CognitiveDistortion? = entries.find { it.name == key }
    }
}
