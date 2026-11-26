package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.LessonProgressEntity
import com.example.data.model.DiagramType
import com.example.data.model.Lesson
import com.example.ui.Lab3DState
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.Interactive3DViewport
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton
import com.example.ui.theme.PomanaAmber
import com.example.ui.theme.PomanaAmberDark

private val StepTitles = listOf(
    "1. Concept",
    "2. 3D Diagram",
    "3. Examples",
    "4. Mobile Workflow",
    "5. Guided Practice",
    "6. Independent Practice",
    "7. Mastery Test",
    "8. Practical Project",
    "9. Rubric Eval",
    "10. Revision Plan"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonPlayerScreen(
    lesson: Lesson,
    progress: LessonProgressEntity?,
    onBack: () -> Unit,
    onStepChanged: (Int) -> Unit,
    onLaunchDepartmentPractice: (Int) -> Unit,
    onSubmitMasteryQuiz: (correct: Int, total: Int, onResult: (passed: Boolean, accuracy: Int) -> Unit) -> Unit,
    onSubmitToPortfolio: (projectTitle: String, departmentId: Int, software: String, desc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    var stepIndex by remember(lesson.id) {
        mutableIntStateOf((progress?.currentStep ?: 0).coerceIn(0, 9))
    }
    val guidedChecked = remember(lesson.id) { mutableStateListOf<Int>() }
    val rubricChecked = remember(lesson.id) { mutableStateListOf<Int>() }

    // Mastery Quiz State (Step 7)
    val selectedQuizAnswers = remember(lesson.id) { mutableStateMapOf<Int, Int>() }
    var quizEvaluated by remember(lesson.id) { mutableStateOf(false) }
    var quizPassed by remember(lesson.id) { mutableStateOf(progress?.isMastered == true) }
    var lastAccuracy by remember(lesson.id) { mutableIntStateOf(progress?.bestAccuracyPercent ?: 0) }

    // Interactive diagram state for Step 2
    var diagramLab by remember(lesson.id) {
        mutableStateOf(
            Lab3DState(
                primitive = lesson.diagramSpec.interactivePrimitive,
                renderMode = when (lesson.diagramSpec.type) {
                    DiagramType.MESH_ANATOMY -> "Wireframe"
                    DiagramType.COORDINATE_AXES -> "Axes"
                    else -> "Shaded"
                },
                isPlayingAnim = lesson.diagramSpec.type == DiagramType.KEYFRAME_CURVE,
                showRuleOfThirds = lesson.diagramSpec.type == DiagramType.CAMERA_FOV
            )
        )
    }

    fun moveToStep(next: Int) {
        val clamped = next.coerceIn(0, 9)
        stepIndex = clamped
        onStepChanged(clamped)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(lesson.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Text(
                            "Step ${stepIndex + 1}/10 • ${StepTitles[stepIndex]}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("lesson_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to Learning Path")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (stepIndex + 1) / 10f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (stepIndex) {
                    0 -> {
                        PomanaCard {
                            Text("1. Understand the Core Concept", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(lesson.conceptSimple, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    1 -> {
                        PomanaCard {
                            Text("2. ${lesson.diagramSpec.title}", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                lesson.diagramSpec.caption,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(10.dp))
                            Interactive3DViewport(
                                state = diagramLab,
                                onRotateDrag = { dx, dy ->
                                    diagramLab = diagramLab.copy(
                                        yawDeg = diagramLab.yawDeg + dx,
                                        pitchDeg = (diagramLab.pitchDeg + dy).coerceIn(-75f, 75f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Shaded", "Wireframe", "Axes").forEach { mode ->
                                    FilterChip(
                                        selected = diagramLab.renderMode == mode,
                                        onClick = { diagramLab = diagramLab.copy(renderMode = mode) },
                                        label = { Text(mode) }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        PomanaCard {
                            Text("3. Real Studio Examples", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            lesson.realExamples.forEachIndexed { idx, ex ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp)) {
                                        Icon(Icons.Filled.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(ex, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        PomanaCard {
                            Text("4. Free Mobile & Desktop Workflow", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            lesson.mobileWorkflowSteps.forEach { wf ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            "Step ${wf.stepNumber} • ${wf.software}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(wf.instruction, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "License note: ${wf.freeNote}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        PomanaCard {
                            Text("5. Guided Practice Checklist", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(lesson.guidedTaskPrompt, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Hint: ${lesson.guidedTaskHint}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(10.dp))
                            lesson.guidedTaskChecklist.forEachIndexed { idx, item ->
                                val checked = idx in guidedChecked
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            if (checked) guidedChecked.remove(idx) else guidedChecked.add(idx)
                                        }
                                    )
                                    Text(item, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                    5 -> {
                        PomanaCard {
                            Text("6. Independent Exercises", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Before taking the Mastery Check, sharpen your speed with procedurally generated practice exercises for Department ${lesson.departmentId}.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(14.dp))
                            PomanaTactileButton(
                                text = "Launch 6-Question Practice Drill",
                                icon = Icons.Filled.FitnessCenter,
                                onClick = { onLaunchDepartmentPractice(lesson.departmentId) },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "lesson_independent_practice_btn"
                            )
                        }
                    }
                    6 -> {
                        PomanaCard {
                            Text("7. Mastery Knowledge Check (75% Required)", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "You cannot unlock the next lesson by clicking through—score at least 75% here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))

                            lesson.masteryQuiz.forEachIndexed { qIdx, q ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text("Q${qIdx + 1}. ${q.prompt}", style = MaterialTheme.typography.titleSmall)
                                    Spacer(Modifier.height(6.dp))
                                    q.options.forEachIndexed { optIdx, opt ->
                                        val isSelected = selectedQuizAnswers[qIdx] == optIdx
                                        val isCorrectOpt = q.correctOptionIndex == optIdx
                                        val borderCol = when {
                                            quizEvaluated && isCorrectOpt -> MaterialTheme.colorScheme.primary
                                            quizEvaluated && isSelected && !isCorrectOpt -> MaterialTheme.colorScheme.error
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.5.dp, borderCol),
                                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                            else MaterialTheme.colorScheme.surface,
                                            onClick = { if (!quizEvaluated) selectedQuizAnswers[qIdx] = optIdx },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = opt,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                    }
                                    if (quizEvaluated) {
                                        Text(
                                            text = "Explanation: ${q.explanation}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            PomanaTactileButton(
                                text = if (quizEvaluated) "Retake Mastery Check" else "Grade Mastery Check",
                                icon = Icons.Filled.Verified,
                                enabled = selectedQuizAnswers.size == lesson.masteryQuiz.size || quizEvaluated,
                                onClick = {
                                    if (quizEvaluated) {
                                        selectedQuizAnswers.clear()
                                        quizEvaluated = false
                                    } else {
                                        val correct = lesson.masteryQuiz.indices.count { i ->
                                            selectedQuizAnswers[i] == lesson.masteryQuiz[i].correctOptionIndex
                                        }
                                        onSubmitMasteryQuiz(correct, lesson.masteryQuiz.size) { passed, acc ->
                                            quizEvaluated = true
                                            quizPassed = passed
                                            lastAccuracy = acc
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "submit_mastery_quiz_btn"
                            )

                            if (quizEvaluated || quizPassed) {
                                Spacer(Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (quizPassed) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Text(
                                        text = if (quizPassed) {
                                            "Mastered! Score: $lastAccuracy% (+${lesson.xpReward} XP). Proceed to Step 8!"
                                        } else {
                                            "Score: $lastAccuracy%. You need 75%+ to pass. Review the explanations above and retry!"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                    7 -> {
                        val proj = lesson.practicalProject
                        PomanaCard {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("8. Practical Project", style = MaterialTheme.typography.titleLarge)
                                DifficultyBadge(proj.difficulty)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(proj.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(6.dp))
                            Text(proj.description, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(8.dp))
                            Text("Software: ${proj.recommendedSoftware}", style = MaterialTheme.typography.labelLarge)
                            Text(proj.freeVsPaidNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(10.dp))
                            Text("Deliverables:", style = MaterialTheme.typography.titleSmall)
                            proj.deliverables.forEach { d ->
                                Text("• $d", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    8 -> {
                        val proj = lesson.practicalProject
                        PomanaCard {
                            Text("9. Self-Evaluation & Portfolio Log", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Verify your project against every studio rubric criterion before logging it:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            proj.rubricChecklist.forEachIndexed { idx, crit ->
                                val checked = idx in rubricChecked
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            if (checked) rubricChecked.remove(idx) else rubricChecked.add(idx)
                                        }
                                    )
                                    Text(crit, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            PomanaTactileButton(
                                text = "Log Project to Portfolio (+30 XP)",
                                icon = Icons.Filled.FolderSpecial,
                                containerColor = PomanaAmber,
                                lipColor = PomanaAmberDark,
                                onClick = {
                                    onSubmitToPortfolio(
                                        proj.title,
                                        proj.departmentId,
                                        proj.recommendedSoftware,
                                        "Completed rubric items (${rubricChecked.size}/${proj.rubricChecklist.size}): ${proj.description}"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    9 -> {
                        PomanaCard {
                            Text("10. Revision & Mastery Summary", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (quizPassed) "Lesson Mastered! Best Score: $lastAccuracy%."
                                else "Mastery check still pending — return to Step 7 to earn your mastery badge.",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (quizPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(10.dp))
                            Text("Key Revision Takeaways:", style = MaterialTheme.typography.titleSmall)
                            lesson.revisionTips.forEach { tip ->
                                Text("• $tip", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 3.dp))
                            }
                            Spacer(Modifier.height(14.dp))
                            PomanaTactileButton(
                                text = "Finish & Return to Path",
                                icon = Icons.Filled.CheckCircle,
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Bottom Step Navigation (Gated at Step 7 unless quizPassed!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { moveToStep(stepIndex - 1) },
                    enabled = stepIndex > 0,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 50.dp)
                ) {
                    Text("Previous")
                }
                val canAdvanceFromCurrent = if (stepIndex == 6) quizPassed else stepIndex < 9
                PomanaTactileButton(
                    text = if (stepIndex == 6 && !quizPassed) "Pass 75% Quiz First" else "Next Step",
                    enabled = canAdvanceFromCurrent,
                    onClick = { moveToStep(stepIndex + 1) },
                    modifier = Modifier.weight(1.4f),
                    testTag = "lesson_next_step_btn"
                )
            }
        }
    }
}
