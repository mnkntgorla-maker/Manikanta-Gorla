package com.example.data.ai

import com.example.data.curriculum.CurriculumCatalog
import com.example.data.curriculum.ProceduralExerciseGenerator
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseItem
import com.example.data.model.ExerciseType
import kotlinx.coroutines.delay

enum class PomanaAiMode(val label: String, val subtitle: String) {
    TUTOR("Tutor", "Step-by-step 3D concepts & analogies"),
    EXAMINER("Examiner", "1-by-1 oral/quiz examination & grading"),
    MENTOR("Project Mentor", "Break down projects for Prisma3D / Blender"),
    REVISION("Revision Coach", "Targeted help on your weak topics"),
    VISUAL("Visual Feedback", "Analyze uploaded 3D reference/render image")
}

data class AiContextSnapshot(
    val learnerName: String,
    val rankTitle: String,
    val accuracyPercent: Int,
    val weakTopics: List<String>,
    val strongTopics: List<String>,
    val shareProgressEnabled: Boolean
)

sealed interface AiResult<out T> {
    data class Success<T>(
        val data: T,
        val isPlaceholderPreview: Boolean,
        val notice: String
    ) : AiResult<T>

    data class LimitReached(val message: String) : AiResult<Nothing>
    data class Error(val message: String) : AiResult<Nothing>
}

interface AiRepository {
    val isPlaceholderMode: Boolean
    val setupNotice: String

    suspend fun askAssistant(
        prompt: String,
        mode: PomanaAiMode,
        context: AiContextSnapshot,
        hasAttachedImage: Boolean = false
    ): AiResult<String>

    suspend fun generateCustomExercise(
        departmentId: Int,
        difficulty: DifficultyTier,
        type: ExerciseType
    ): AiResult<ExerciseItem>

    suspend fun evaluateProjectSubmission(
        title: String,
        departmentId: Int,
        softwareUsed: String,
        notes: String,
        hasImage: Boolean
    ): AiResult<String>

    suspend fun explainLabExperiment(
        primitive: String,
        roughness: Float,
        metallic: Float,
        keyEnabled: Boolean,
        fillEnabled: Boolean,
        rimEnabled: Boolean,
        fps: Int,
        easing: String
    ): AiResult<String>
}

/**
 * Placeholder implementation used when Firebase setup was declined.
 * Returns clearly labeled sample preview responses without calling external APIs or embedding keys.
 */
class PlaceholderAiRepository : AiRepository {

    override val isPlaceholderMode: Boolean = true
    override val setupNotice: String =
        "Sample preview mode — Pomana AI requires Firebase AI Logic in AI Studio for live Gemini responses."

    override suspend fun askAssistant(
        prompt: String,
        mode: PomanaAiMode,
        context: AiContextSnapshot,
        hasAttachedImage: Boolean
    ): AiResult<String> {
        delay(350)
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isEmpty() && !hasAttachedImage) {
            return AiResult.Error("Please enter a 3D animation question or attach a reference image.")
        }

        val header = "[Sample Preview — Firebase AI Logic not connected]\n"
        val contextLine = if (context.shareProgressEnabled && context.weakTopics.isNotEmpty()) {
            "Learner Context (${context.rankTitle}, ${context.accuracyPercent}% accuracy): Focus areas include ${context.weakTopics.joinToString(", ")}.\n\n"
        } else {
            ""
        }

        val body = when (mode) {
            PomanaAiMode.VISUAL -> {
                if (!hasAttachedImage) {
                    "No image was attached yet. Tap 'Attach Reference / Render' to select a screenshot via the Android Photo Picker before requesting visual feedback—Pomana AI never guesses unseen artwork."
                } else {
                    "Image received for preview inspection regarding \"$cleanPrompt\":\n" +
                        "1. Silhouette Check: Verify primary shapes read clearly even when zoomed out.\n" +
                        "2. Lighting Separation: Add a subtle Rim light behind the subject so edges don't merge into dark backgrounds.\n" +
                        "3. Mobile Tip: In Prisma3D or Sculpt+, inspect from front and 3/4 side views to confirm proportions.\n\n" +
                        "Note: Live multimodal visual critique of your exact pixels will activate once Firebase AI Logic is enabled."
                }
            }
            PomanaAiMode.EXAMINER -> {
                "Examiner Mode Sample Check on \"$cleanPrompt\":\n" +
                    "• Question 1: Suppose a character's foot slides across the floor during a 24-frame walk cycle. Should you switch leg controllers to IK or FK, and which frame marks Heel Contact?\n" +
                    "• Reply with your answer! (Live adaptive grading requires Firebase AI Logic.)"
            }
            PomanaAiMode.MENTOR -> {
                "Project Mentor Sample Breakdown for \"$cleanPrompt\":\n" +
                    "Step 1 (Free Tool): Open Prisma3D or Tinkercad on mobile, or Blender on desktop.\n" +
                    "Step 2 (Blockout): Start with large primitives at (0,0,0) before adding small details.\n" +
                    "Step 3 (Lighting): Set Key (1.0), Fill (0.3), and Rim (0.8).\n" +
                    "Step 4 (Check): Freeze scale to (1,1,1) before rigging or animating."
            }
            PomanaAiMode.REVISION -> {
                val weak = context.weakTopics.firstOrNull() ?: "3D Coordinates & Timing"
                "Revision Coach Sample Plan ($weak):\n" +
                    "• Core rule: Break complex scenes into small axis-locked steps (RGB = XYZ) and test 1 variable at a time.\n" +
                    "• Try a 6-question Intermediate Practice Sprint in Practice Center to raise your accuracy above 80%."
            }
            PomanaAiMode.TUTOR -> {
                "Sample Tutor Explanation for \"$cleanPrompt\":\n" +
                    "• Simple Analogy: Think of 3D animation like puppetry inside a film stage. The Mesh is the clay puppet, Rigging adds the strings/bones, Keyframes mark start/end poses, and Three-Point Lighting shapes mood.\n" +
                    "• Try Next: Open the 3D Lab tab to rotate axes, adjust Roughness/Metallic, and scrub keyframes live!"
            }
        }

        return AiResult.Success(
            data = header + contextLine + body,
            isPlaceholderPreview = true,
            notice = setupNotice
        )
    }

    override suspend fun generateCustomExercise(
        departmentId: Int,
        difficulty: DifficultyTier,
        type: ExerciseType
    ): AiResult<ExerciseItem> {
        delay(250)
        val generated = ProceduralExerciseGenerator.generateSession(
            departmentId = departmentId,
            difficulty = difficulty,
            preferredType = type,
            count = 1
        ).first()
        return AiResult.Success(
            data = generated.copy(
                id = "AI-PREVIEW-${generated.id}",
                softwareLinkNote = "[Sample Preview Generator] ${generated.softwareLinkNote}".trim()
            ),
            isPlaceholderPreview = true,
            notice = setupNotice
        )
    }

    override suspend fun evaluateProjectSubmission(
        title: String,
        departmentId: Int,
        softwareUsed: String,
        notes: String,
        hasImage: Boolean
    ): AiResult<String> {
        delay(250)
        val deptName = CurriculumCatalog.getDepartment(departmentId)?.title ?: "3D Studio"
        val imageCheck = if (hasImage) {
            "Screenshot attachment recorded locally. (Live pixel-level critique activates when Firebase AI Logic is enabled.)"
        } else {
            "No screenshot/file was attached—evaluation is based solely on your written project notes (Pomana AI never claims to inspect unprovided visuals)."
        }
        val response = "[Sample Preview — Firebase AI Logic not connected]\n" +
            "Project: $title ($deptName • $softwareUsed)\n" +
            "$imageCheck\n" +
            "Checklist Tips: Verify clean ground contact at Y=0, check 3-point lighting contrast, and ensure transforms are cleanly applied before final export."
        return AiResult.Success(response, isPlaceholderPreview = true, notice = setupNotice)
    }

    override suspend fun explainLabExperiment(
        primitive: String,
        roughness: Float,
        metallic: Float,
        keyEnabled: Boolean,
        fillEnabled: Boolean,
        rimEnabled: Boolean,
        fps: Int,
        easing: String
    ): AiResult<String> {
        delay(200)
        val surfaceFeel = when {
            metallic > 0.6f && roughness < 0.35f -> "polished metallic mirror-like sheen"
            metallic > 0.6f -> "brushed satin metal finish"
            roughness < 0.3f -> "glossy lacquered plastic or ceramic"
            else -> "soft matte clay or fabric finish"
        }
        val lightCount = listOf(keyEnabled, fillEnabled, rimEnabled).count { it }
        val lightingRead = when (lightCount) {
            0 -> "No active scene lights — only flat ambient silhouette."
            1 -> "Single light source — high contrast drama with deep dark shadows."
            2 -> "Two-light setup — balanced form, consider adding Rim for edge separation."
            else -> "Full 3-point studio lighting — crisp Key form, lifted Fill shadows, and Rim separation!"
        }
        val text = "[Sample Preview — Firebase AI Logic not connected]\n" +
            "3D Lab Snapshot ($primitive): Roughness ${"%.2f".format(roughness)} + Metallic ${"%.2f".format(metallic)} produces a $surfaceFeel. " +
            "$lightingRead Motion at ${fps}fps using '$easing' shapes perceived weight and snap."
        return AiResult.Success(text, isPlaceholderPreview = true, notice = setupNotice)
    }
}
