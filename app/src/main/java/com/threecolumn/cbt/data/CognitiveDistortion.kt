package com.threecolumn.cbt.data

/**
 * The ten cognitive distortions popularized by David Burns' "Feeling Good".
 * Descriptions here are original paraphrases, not quotes from the book.
 */
enum class CognitiveDistortion(val label: String, val description: String) {
    ALL_OR_NOTHING(
        "All-or-Nothing Thinking",
        "You see things in black-and-white categories. If a situation falls short of perfect, you see it as a total failure."
    ),
    OVERGENERALIZATION(
        "Overgeneralization",
        "You see a single negative event as part of a never-ending pattern, often using words like \"always\" or \"never\"."
    ),
    MENTAL_FILTER(
        "Mental Filter",
        "You dwell on a single negative detail so much that your view of reality becomes darkened, like a drop of ink coloring a glass of water."
    ),
    DISCOUNTING_POSITIVE(
        "Discounting the Positive",
        "You reject positive experiences by insisting they \"don't count\" for some reason."
    ),
    JUMPING_TO_CONCLUSIONS(
        "Jumping to Conclusions",
        "You interpret things negatively without facts to support it — mind reading what others think, or predicting things will turn out badly."
    ),
    MAGNIFICATION_MINIMIZATION(
        "Magnification or Minimization",
        "You exaggerate the importance of problems and shortcomings, or shrink the importance of your good qualities."
    ),
    EMOTIONAL_REASONING(
        "Emotional Reasoning",
        "You assume your negative emotions reflect the way things really are: \"I feel it, therefore it must be true.\""
    ),
    SHOULD_STATEMENTS(
        "Should Statements",
        "You tell yourself things should be the way you hoped, using \"should,\" \"must,\" or \"ought to,\" which leaves you feeling guilty or resentful."
    ),
    LABELING(
        "Labeling",
        "An extreme form of overgeneralization — instead of describing an error, you attach a fixed negative label to yourself or others."
    ),
    PERSONALIZATION(
        "Personalization",
        "You see yourself as the cause of some negative external event that you weren't primarily responsible for."
    );

    companion object {
        fun fromStorageKey(key: String): CognitiveDistortion? = entries.find { it.name == key }
    }
}
