package com.example.data.model

enum class DifficultyTier(val label: String, val xpMultiplier: Float) {
    BASIC("Basic", 1.0f),
    EASY("Easy", 1.2f),
    INTERMEDIATE("Intermediate", 1.5f),
    DIFFICULT("Difficult", 1.8f),
    ADVANCED("Advanced", 2.2f),
    EXPERT("Expert", 2.6f)
}

enum class ExerciseType(val label: String) {
    MICRO_SPRINT("1-Min Micro"),
    MULTIPLE_CHOICE("Multiple Choice"),
    FIND_ERROR("Find the Error"),
    MATCH_PAIRS("Match Concepts"),
    ANIMATION_TIMING("Animation Timing"),
    COORDINATE_3D("3D Coordinates"),
    FILL_BLANK("Fill in the Blank"),
    PRACTICAL_BRIEF("Practical Challenge")
}

data class MatchPair(
    val left: String,
    val right: String
)

data class TimingChallengeSpec(
    val fps: Int,
    val totalFrames: Int,
    val targetFrame: Int,
    val toleranceFrames: Int,
    val targetEasing: String
)

data class CoordinateChallengeSpec(
    val targetAxis: String, // "X", "Y", "Z"
    val targetDeltaLabel: String,
    val startVec: Triple<Int, Int, Int>,
    val endVec: Triple<Int, Int, Int>
)

data class ExerciseItem(
    val id: String,
    val departmentId: Int,
    val topic: String,
    val difficulty: DifficultyTier,
    val type: ExerciseType,
    val learningObjective: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val matchPairs: List<MatchPair> = emptyList(),
    val timingSpec: TimingChallengeSpec? = null,
    val coordSpec: CoordinateChallengeSpec? = null,
    val fillBlankExpected: String = "",
    val hint: String,
    val explanation: String,
    val xpReward: Int = 15,
    val softwareLinkNote: String = ""
)
