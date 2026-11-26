package com.example

import com.example.data.ai.AiContextSnapshot
import com.example.data.ai.AiResult
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.ai.PomanaAiMode
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.curriculum.ProceduralExerciseGenerator
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class PomanaEngineTest {

    @Test
    fun curriculumCoversAll12DepartmentsWithMasteryQuizzes() {
        val depts = CurriculumCatalog.allDepartments
        assertEquals(12, depts.size)
        assertEquals((1..12).toList(), depts.map { it.id })
        depts.forEach { dept ->
            assertTrue("Dept ${dept.id} needs at least 1 lesson", dept.lessons.isNotEmpty())
            dept.lessons.forEach { lesson ->
                assertTrue(lesson.masteryQuiz.size >= 4)
                assertTrue(lesson.mobileWorkflowSteps.isNotEmpty())
                assertTrue(lesson.realExamples.isNotEmpty())
            }
        }
    }

    @Test
    fun proceduralExerciseGeneratorProducesValidUniqueExercises() {
        DifficultyTier.entries.forEach { diff ->
            ExerciseType.entries.forEach { type ->
                val session = ProceduralExerciseGenerator.generateSession(
                    departmentId = null,
                    difficulty = diff,
                    preferredType = type,
                    count = 5,
                    seed = 42L + diff.ordinal * 31 + type.ordinal
                )
                assertTrue("Expected exercises for $diff / $type", session.isNotEmpty())
                val ids = session.map { it.id }.toSet()
                assertEquals(session.size, ids.size)
                session.forEach { ex ->
                    assertTrue(ex.prompt.isNotBlank())
                    assertTrue(ex.explanation.isNotBlank())
                    when (ex.type) {
                        ExerciseType.ANIMATION_TIMING -> assertNotNull(ex.timingSpec)
                        ExerciseType.MATCH_PAIRS -> assertTrue(ex.matchPairs.size >= 3)
                        ExerciseType.FILL_BLANK -> assertTrue(ex.fillBlankExpected.isNotBlank())
                        else -> {
                            assertTrue(ex.options.isNotEmpty())
                            assertTrue(ex.correctOptionIndex in ex.options.indices)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun placeholderAiNeverPretendsToInspectMissingImage() = runBlocking {
        val ai = PlaceholderAiRepository()
        val ctx = AiContextSnapshot("Maya", "Voxel Novice", 0, emptyList(), emptyList(), true)
        val res = ai.askAssistant("Check my mesh", PomanaAiMode.VISUAL, ctx, hasAttachedImage = false)
        assertTrue(res is AiResult.Success)
        val text = (res as AiResult.Success).data
        assertTrue(text.contains("Sample Preview"))
        assertTrue(text.contains("No image was attached"))
    }
}
