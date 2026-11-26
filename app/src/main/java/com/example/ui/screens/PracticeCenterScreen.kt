package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.AdminConfigEntity
import com.example.data.model.*
import com.example.ui.PracticeSessionState
import com.example.ui.components.*
import com.example.ui.theme.PomanaAmber
import com.example.ui.theme.PomanaAmberDark
import com.example.ui.theme.PomanaSky
import com.example.ui.theme.PomanaSkyDark
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun PracticeCenterScreen(
    session: PracticeSessionState,
    adminConfig: AdminConfigEntity,
    isAiGenerating: Boolean,
    onStartSession: (deptId: Int?, difficulty: DifficultyTier, type: ExerciseType?, count: Int) -> Unit,
    onGenerateAiExercise: (deptId: Int, difficulty: DifficultyTier, type: ExerciseType) -> Unit,
    onSubmitAnswer: (Boolean) -> Unit,
    onCloseSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (session.isActive) {
        PracticeRunnerView(
            session = session,
            onSubmitAnswer = onSubmitAnswer,
            onClose = onCloseSession,
            modifier = modifier
        )
        return
    }

    var selectedDeptId by remember { mutableStateOf<Int?>(null) } // null = All 12
    var selectedDiff by remember { mutableStateOf(DifficultyTier.BASIC) }
    var selectedType by remember { mutableStateOf<ExerciseType?>(null) } // null = Smart Mix

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Massive Practice Centre", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Procedural + structured exercise engine spanning all 12 departments and 6 skill tiers. Zero meaningless repetition.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Department Picker
        PomanaCard {
            Text("1. Choose Department", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedDeptId == null,
                        onClick = { selectedDeptId = null },
                        label = { Text("All 12 Mixed") }
                    )
                }
                items(CurriculumCatalog.allDepartments) { dept ->
                    FilterChip(
                        selected = selectedDeptId == dept.id,
                        onClick = { selectedDeptId = dept.id },
                        label = { Text("${dept.id}. ${dept.title}") }
                    )
                }
            }
        }

        // Difficulty Tier Picker
        PomanaCard {
            Text("2. Select Difficulty Level", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DifficultyTier.entries) { tier ->
                    FilterChip(
                        selected = selectedDiff == tier,
                        onClick = { selectedDiff = tier },
                        label = { Text("${tier.label} (${tier.xpMultiplier}x XP)") }
                    )
                }
            }
        }

        // Exercise Mode Picker
        PomanaCard {
            Text("3. Exercise Mode", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("Smart Mix") }
                    )
                }
                items(ExerciseType.entries) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.label) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PomanaTactileButton(
                    text = "Start 6-Task Set",
                    icon = Icons.Filled.PlayArrow,
                    onClick = { onStartSession(selectedDeptId, selectedDiff, selectedType, 6) },
                    modifier = Modifier.weight(1.3f),
                    testTag = "start_practice_btn"
                )
                PomanaTactileButton(
                    text = "1-Min Micro",
                    icon = Icons.Filled.Timer,
                    containerColor = PomanaAmber,
                    lipColor = PomanaAmberDark,
                    onClick = { onStartSession(selectedDeptId, selectedDiff, ExerciseType.MICRO_SPRINT, 4) },
                    modifier = Modifier.weight(1f),
                    testTag = "start_micro_btn"
                )
            }
        }

        // AI Exercise Generator Card
        PomanaCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pomana AI Exercise Generator", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${adminConfig.aiExercisesGenToday}/${adminConfig.dailyExerciseGenLimit} today",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(Modifier.height(6.dp))
            AiNoticeBanner()
            Spacer(Modifier.height(10.dp))
            PomanaTactileButton(
                text = if (isAiGenerating) "Generating..." else "Generate Custom Exercise",
                icon = Icons.Filled.AutoAwesome,
                containerColor = PomanaSky,
                lipColor = PomanaSkyDark,
                enabled = !isAiGenerating,
                onClick = {
                    onGenerateAiExercise(
                        selectedDeptId ?: 1,
                        selectedDiff,
                        selectedType ?: ExerciseType.MULTIPLE_CHOICE
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "ai_generate_exercise_btn"
            )
        }
    }
}

@Composable
private fun PracticeRunnerView(
    session: PracticeSessionState,
    onSubmitAnswer: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (session.isFinished) {
        val total = session.items.size.coerceAtLeast(1)
        val pct = ((session.correctCount * 100f) / total).toInt()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MasteryRing(
                progress = session.correctCount.toFloat() / total,
                size = 110.dp,
                strokeWidth = 10.dp,
                centerText = "$pct%"
            )
            Spacer(Modifier.height(18.dp))
            Text("Practice Complete!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "${session.correctCount} of $total correct • +${session.xpEarned} XP earned",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))
            PomanaTactileButton(
                text = "Back to Practice Centre",
                icon = Icons.Filled.CheckCircle,
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            )
        }
        return
    }

    val item = session.items.getOrNull(session.currentIndex) ?: return
    var selectedOpt by remember(item.id) { mutableStateOf<Int?>(null) }
    var textAnswer by remember(item.id) { mutableStateOf("") }
    var sliderFrame by remember(item.id) { mutableFloatStateOf(0f) }
    var showHint by remember(item.id) { mutableStateOf(false) }
    var checkedResult by remember(item.id) { mutableStateOf<Boolean?>(null) }

    // Match Pairs local state
    val leftItems = remember(item.id) { item.matchPairs.map { it.left } }
    val rightItems = remember(item.id) { item.matchPairs.map { it.right }.shuffled() }
    var selectedLeft by remember(item.id) { mutableStateOf<String?>(null) }
    val matchedMap = remember(item.id) { mutableStateMapOf<String, String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(session.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Task ${session.currentIndex + 1} of ${session.items.size} • ID: ${item.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Exit practice")
            }
        }

        LinearProgressIndicator(
            progress = { (session.currentIndex + 1).toFloat() / session.items.size.coerceAtLeast(1) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        PomanaCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyBadge(item.difficulty)
                Text(
                    item.type.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Objective: ${item.learningObjective}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Text(item.prompt, style = MaterialTheme.typography.titleMedium)
            if (item.softwareLinkNote.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    item.softwareLinkNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        // Type-Specific Interactive Area
        when (item.type) {
            ExerciseType.ANIMATION_TIMING -> {
                val spec = item.timingSpec
                if (spec != null) {
                    PomanaCard {
                        Text(
                            "Selected Frame: ${sliderFrame.roundToInt()} / ${spec.totalFrames} (${spec.fps} fps)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = sliderFrame,
                            onValueChange = { if (checkedResult == null) sliderFrame = it },
                            valueRange = 0f..spec.totalFrames.toFloat(),
                            steps = (spec.totalFrames - 1).coerceAtLeast(1)
                        )
                        Text(
                            "Target Easing Style: ${spec.targetEasing}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            ExerciseType.FILL_BLANK -> {
                OutlinedTextField(
                    value = textAnswer,
                    onValueChange = { if (checkedResult == null) textAnswer = it },
                    label = { Text("Type exact answer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            ExerciseType.MATCH_PAIRS -> {
                PomanaCard {
                    Text(
                        "Tap a Left concept, then tap its matching Right definition (${matchedMap.size}/${leftItems.size} paired):",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    leftItems.forEach { left ->
                        val pairedRight = matchedMap[left]
                        val isSel = selectedLeft == left
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.5.dp,
                                when {
                                    isSel -> MaterialTheme.colorScheme.primary
                                    pairedRight != null -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surface,
                            onClick = { if (checkedResult == null) selectedLeft = left },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(left, style = MaterialTheme.typography.labelLarge)
                                if (pairedRight != null) {
                                    Text("→ $pairedRight", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Definitions:", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    rightItems.forEach { right ->
                        val used = matchedMap.values.contains(right)
                        OutlinedButton(
                            onClick = {
                                val l = selectedLeft
                                if (checkedResult == null && l != null) {
                                    matchedMap[l] = right
                                    selectedLeft = null
                                }
                            },
                            enabled = checkedResult == null && selectedLeft != null && !used,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(right, maxLines = 2)
                        }
                    }
                }
            }
            else -> {
                item.options.forEachIndexed { idx, opt ->
                    val isSelected = selectedOpt == idx
                    val borderCol = when {
                        checkedResult != null && idx == item.correctOptionIndex -> MaterialTheme.colorScheme.primary
                        checkedResult != null && isSelected && idx != item.correctOptionIndex -> MaterialTheme.colorScheme.error
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.8.dp, borderCol),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        else MaterialTheme.colorScheme.surface,
                        onClick = { if (checkedResult == null) selectedOpt = idx },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        // Hint button
        if (checkedResult == null) {
            TextButton(onClick = { showHint = !showHint }) {
                Icon(Icons.Outlined.Lightbulb, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (showHint) "Hide Hint" else "Show Hint")
            }
            if (showHint) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                ) {
                    Text(
                        text = "Hint: ${item.hint}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Result feedback
        if (checkedResult != null) {
            val ok = checkedResult == true
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (ok) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (ok) "Awesome! +${item.xpReward} XP" else "Good try! +3 XP for effort",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(item.explanation, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        val readyToCheck = when (item.type) {
            ExerciseType.ANIMATION_TIMING -> true
            ExerciseType.FILL_BLANK -> textAnswer.isNotBlank()
            ExerciseType.MATCH_PAIRS -> matchedMap.size == item.matchPairs.size
            else -> selectedOpt != null
        }

        if (checkedResult == null) {
            PomanaTactileButton(
                text = "Check Answer",
                enabled = readyToCheck,
                onClick = {
                    val correct = when (item.type) {
                        ExerciseType.ANIMATION_TIMING -> {
                            val spec = item.timingSpec
                            spec != null && abs(sliderFrame.roundToInt() - spec.targetFrame) <= spec.toleranceFrames
                        }
                        ExerciseType.FILL_BLANK ->
                            textAnswer.trim().equals(item.fillBlankExpected.trim(), ignoreCase = true)
                        ExerciseType.MATCH_PAIRS ->
                            item.matchPairs.all { matchedMap[it.left] == it.right }
                        else -> selectedOpt == item.correctOptionIndex
                    }
                    checkedResult = correct
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "check_practice_answer_btn"
            )
        } else {
            PomanaTactileButton(
                text = if (session.currentIndex + 1 >= session.items.size) "Finish Practice" else "Continue",
                onClick = { onSubmitAnswer(checkedResult == true) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "continue_practice_btn"
            )
        }
    }
}
