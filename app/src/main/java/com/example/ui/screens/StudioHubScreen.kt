package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.*
import com.example.data.repository.LearnerAnalytics
import com.example.ui.components.MasteryRing
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton
import com.example.ui.theme.PomanaAmber
import com.example.ui.theme.PomanaCoral
import com.example.ui.theme.PomanaEmerald
import com.example.ui.theme.PomanaSky

@Composable
fun StudioHubScreen(
    profile: LearnerProfileEntity?,
    allProfiles: List<LearnerProfileEntity>,
    analytics: LearnerAnalytics,
    attempts: List<ExerciseAttemptEntity>,
    achievements: List<AchievementEntity>,
    portfolio: List<PortfolioProjectEntity>,
    themeMode: ThemeMode,
    soundEnabled: Boolean,
    shareProgressWithAi: Boolean,
    adminConfig: AdminConfigEntity,
    onOpenProjects: () -> Unit,
    onOpenCatalog: () -> Unit,
    onStartRevision: (String?) -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetSoundEnabled: (Boolean) -> Unit,
    onSetShareProgressWithAi: (Boolean) -> Unit,
    onSetDailyGoal: (Int) -> Unit,
    onToggleAdminRole: () -> Unit,
    onSaveAdminConfig: (AdminConfigEntity) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = buildList {
        add("Dashboard")
        add("Badges")
        add("Rankings")
        add("Settings")
        if (profile?.isAdmin == true) add("Admin")
    }

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab.coerceIn(0, tabs.lastIndex),
            edgePadding = 12.dp
        ) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(label) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (tabs.getOrNull(selectedTab)) {
                "Dashboard" -> {
                    item {
                        PomanaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(profile?.displayName ?: "Learner", style = MaterialTheme.typography.headlineSmall)
                                    Text(
                                        "${analytics.rankTitle} • ${analytics.xpToNextRank} XP to ${analytics.nextRankTitle}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Time learned: ${profile?.totalMinutesLearned ?: 0} min • Portfolio: ${portfolio.size} pieces",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                MasteryRing(
                                    progress = if (analytics.totalLessons == 0) 0f
                                    else analytics.completedLessons.toFloat() / analytics.totalLessons,
                                    size = 64.dp,
                                    centerText = "${analytics.completedLessons}/${analytics.totalLessons}"
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onOpenProjects, modifier = Modifier.weight(1f)) {
                                    Text("Projects & Apps")
                                }
                                OutlinedButton(onClick = onOpenCatalog, modifier = Modifier.weight(1f)) {
                                    Text("All 12 Depts")
                                }
                            }
                        }
                    }

                    // Department Accuracy Chart (Compose Canvas)
                    item {
                        PomanaCard {
                            Text("Real Practice Accuracy by Department", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            if (attempts.isEmpty()) {
                                Text(
                                    "Complete exercises in Practice Centre or Lesson Mastery Quizzes to chart your real department accuracy.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                val deptAccuracies = (1..12).mapNotNull { d ->
                                    val list = attempts.filter { it.departmentId == d }
                                    if (list.isEmpty()) null
                                    else d to ((list.count { it.isCorrect } * 100f) / list.size).toInt()
                                }
                                val barColor = MaterialTheme.colorScheme.primary
                                val trackColor = MaterialTheme.colorScheme.surfaceVariant
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                ) {
                                    val count = deptAccuracies.size.coerceAtLeast(1)
                                    val barW = (size.width / (count * 1.6f)).coerceAtMost(38f)
                                    val spacing = (size.width - barW * count) / (count + 1)
                                    deptAccuracies.forEachIndexed { idx, (_, pct) ->
                                        val x = spacing + idx * (barW + spacing)
                                        drawRoundRect(
                                            color = trackColor,
                                            topLeft = Offset(x, 0f),
                                            size = Size(barW, size.height),
                                            cornerRadius = CornerRadius(10f, 10f)
                                        )
                                        val h = size.height * (pct / 100f).coerceIn(0.05f, 1f)
                                        drawRoundRect(
                                            color = if (pct >= 75) barColor else PomanaAmber,
                                            topLeft = Offset(x, size.height - h),
                                            size = Size(barW, h),
                                            cornerRadius = CornerRadius(10f, 10f)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    deptAccuracies.joinToString(" • ") { "D${it.first}: ${it.second}%" },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Strong & Weak Topics + Revision
                    item {
                        PomanaCard {
                            Text("Topic Strengths & Revision", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Strong Topics: " + (analytics.strongTopics.takeIf { it.isNotEmpty() }
                                    ?.joinToString { "${it.topic} (${it.accuracyPercent}%)" }
                                    ?: "Keep practicing to unlock strengths"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Needs Practice: " + (analytics.weakTopics.takeIf { it.isNotEmpty() }
                                    ?.joinToString { "${it.topic} (${it.accuracyPercent}%)" }
                                    ?: "No weak topics flagged yet"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            if (analytics.recommendedRevisionTopic != null) {
                                Spacer(Modifier.height(10.dp))
                                PomanaTactileButton(
                                    text = "Revise ${analytics.recommendedRevisionTopic}",
                                    icon = Icons.Filled.AutoFixHigh,
                                    onClick = { onStartRevision(analytics.recommendedRevisionTopic) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Recent Practice History
                    item {
                        Text("Recent Practice History", style = MaterialTheme.typography.titleMedium)
                    }
                    if (attempts.isEmpty()) {
                        item {
                            PomanaCard {
                                Text(
                                    "Your practice history will record every solved exercise with accuracy and XP.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(attempts.take(10), key = { it.id }) { att ->
                            PomanaCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${att.topic} (${att.difficulty})", style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            "Dept ${att.departmentId} • ${att.exerciseType} • ${att.timeSpentSec}s",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = if (att.isCorrect) "+${att.xpEarned} XP ✓" else "+${att.xpEarned} XP",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (att.isCorrect) PomanaEmerald else PomanaCoral
                                    )
                                }
                            }
                        }
                    }
                }

                "Badges" -> {
                    item {
                        Text("Achievement Badges", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Earned solely through genuine exercises, mastery checks, streaks, and portfolio uploads.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(achievements, key = { it.code }) { ach ->
                        PomanaCard(
                            containerColor = if (ach.isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.surface
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (ach.isUnlocked) PomanaAmber else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (ach.isUnlocked) Icons.Filled.EmojiEvents else Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = if (ach.isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ach.title, style = MaterialTheme.typography.titleMedium)
                                    Text(ach.description, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    "+${ach.xpReward} XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (ach.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                "Rankings" -> {
                    item {
                        PomanaCard {
                            Text("Local Studio Leaderboard", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Shows real learner accounts created on this device sorted by earned XP (never simulated bots or fake scores). Enable Firebase later to sync global weekly competitions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(allProfiles.withIndex().toList(), key = { it.value.id }) { (idx, p) ->
                        val isMe = p.id == profile?.id
                        PomanaCard(
                            containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "#${idx + 1}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (idx == 0) PomanaAmber else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            if (isMe) "${p.displayName} (You)" else p.displayName,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text("@${p.handle} • ${p.streakDays}d streak", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Text("${p.totalXp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                "Settings" -> {
                    item {
                        PomanaCard {
                            Text("Appearance & Learning Goals", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("Theme Mode", style = MaterialTheme.typography.labelMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ThemeMode.entries.forEach { m ->
                                    FilterChip(
                                        selected = themeMode == m,
                                        onClick = { onSetThemeMode(m) },
                                        label = { Text(m.label) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("Daily XP Goal", style = MaterialTheme.typography.labelMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(30, 50, 100).forEach { goal ->
                                    FilterChip(
                                        selected = (profile?.dailyGoalXp ?: 50) == goal,
                                        onClick = { onSetDailyGoal(goal) },
                                        label = { Text("$goal XP") }
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Practice Sound & Haptic Cues", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = soundEnabled, onCheckedChange = onSetSoundEnabled)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Share Progress Context with Pomana AI", style = MaterialTheme.typography.bodyMedium)
                                    Text("Privacy control for personalized revision tips", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(checked = shareProgressWithAi, onCheckedChange = onSetShareProgressWithAi)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Educator / Admin Mode", style = MaterialTheme.typography.bodyMedium)
                                    Text("Unlock Admin Console tab for AI limits & monitoring", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = profile?.isAdmin == true,
                                    onCheckedChange = { onToggleAdminRole() },
                                    modifier = Modifier.testTag("toggle_admin_switch")
                                )
                            }
                        }
                    }

                    // Help Centre FAQ
                    item {
                        PomanaCard {
                            Text("Help Centre & FAQ", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("• Why are lessons locked?", style = MaterialTheme.typography.titleSmall)
                            Text("Pass each lesson's 75% mastery test so skills build solidly without skipping foundations.", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                            Text("• Do I need paid software?", style = MaterialTheme.typography.titleSmall)
                            Text("No! Prisma3D, Tinkercad, Blockbench, and Blender cover the entire path free.", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                            Text("• How do I enable live Gemini AI & cloud sync?", style = MaterialTheme.typography.titleSmall)
                            Text("Enable Firebase in AI Studio to connect Firebase AI Logic and Firestore.", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp)
                                .testTag("sign_out_button")
                        ) {
                            Icon(Icons.Outlined.Logout, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Switch Account / Log Out")
                        }
                    }
                }

                "Admin" -> {
                    item {
                        AdminConsoleCard(
                            config = adminConfig,
                            totalLearners = allProfiles.size,
                            onSave = onSaveAdminConfig
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminConsoleCard(
    config: AdminConfigEntity,
    totalLearners: Int,
    onSave: (AdminConfigEntity) -> Unit
) {
    var qLimit by remember(config) { mutableIntStateOf(config.dailyAiQuestionLimit) }
    var exLimit by remember(config) { mutableIntStateOf(config.dailyExerciseGenLimit) }
    var imgLimit by remember(config) { mutableIntStateOf(config.dailyImageAnalysisLimit) }
    var tutorOn by remember(config) { mutableStateOf(config.aiTutorEnabled) }
    var examinerOn by remember(config) { mutableStateOf(config.aiExaminerEnabled) }
    var exGenOn by remember(config) { mutableStateOf(config.aiExerciseGenEnabled) }
    var visualOn by remember(config) { mutableStateOf(config.aiVisualFeedbackEnabled) }
    var modelLabel by remember(config) { mutableStateOf(config.selectedProviderModel) }

    val models = listOf(
        "Firebase AI Logic (Gemini 3.8 Flash — Inactive)",
        "Firebase AI Logic (Gemini 3.5 Flash-Lite — Inactive)",
        "Custom Provider Adapter (Future)"
    )

    PomanaCard {
        Text("Administrator Console", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            "Registered device learners: $totalLearners • Usage today: ${config.aiQuestionsUsedToday} AI Qs, ${config.aiExercisesGenToday} Gen Tasks, ${config.imageAnalysisUsedToday} Images",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        Text("Daily AI Question Limit: $qLimit", style = MaterialTheme.typography.labelLarge)
        Slider(value = qLimit.toFloat(), onValueChange = { qLimit = it.toInt() }, valueRange = 5f..100f)

        Text("Daily Exercise Generation Limit: $exLimit", style = MaterialTheme.typography.labelLarge)
        Slider(value = exLimit.toFloat(), onValueChange = { exLimit = it.toInt() }, valueRange = 5f..100f)

        Text("Daily Image Analysis Limit: $imgLimit", style = MaterialTheme.typography.labelLarge)
        Slider(value = imgLimit.toFloat(), onValueChange = { imgLimit = it.toInt() }, valueRange = 1f..30f)

        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enable AI Tutor", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = tutorOn, onCheckedChange = { tutorOn = it })
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enable AI Examiner", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = examinerOn, onCheckedChange = { examinerOn = it })
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enable AI Exercise Generator", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = exGenOn, onCheckedChange = { exGenOn = it })
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Visual Image Feedback", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = visualOn, onCheckedChange = { visualOn = it })
        }

        Spacer(Modifier.height(8.dp))
        Text("AI Provider / Model Target", style = MaterialTheme.typography.labelLarge)
        models.forEach { m ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = modelLabel == m, onClick = { modelLabel = m })
                Text(m, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(12.dp))
        PomanaTactileButton(
            text = "Save Admin Policy",
            icon = Icons.Filled.Save,
            onClick = {
                onSave(
                    config.copy(
                        dailyAiQuestionLimit = qLimit,
                        dailyExerciseGenLimit = exLimit,
                        dailyImageAnalysisLimit = imgLimit,
                        aiTutorEnabled = tutorOn,
                        aiExaminerEnabled = examinerOn,
                        aiExerciseGenEnabled = exGenOn,
                        aiVisualFeedbackEnabled = visualOn,
                        selectedProviderModel = modelLabel
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "save_admin_config_btn"
        )
    }
}
