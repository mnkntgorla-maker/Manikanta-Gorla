package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.LessonProgressEntity
import com.example.data.model.Department
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseType
import com.example.data.model.Lesson
import com.example.data.repository.LearnerAnalytics
import com.example.ui.components.MasteryRing
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton
import com.example.ui.theme.PomanaAmber
import com.example.ui.theme.PomanaAmberDark
import com.example.ui.theme.PomanaSky

@Composable
fun HomeDashboardScreen(
    analytics: LearnerAnalytics,
    lessonProgress: List<LessonProgressEntity>,
    onOpenLesson: (Lesson) -> Unit,
    onStartMicroSprint: () -> Unit,
    onStartRevision: (String?) -> Unit,
    onOpenLab: () -> Unit,
    onOpenCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressMap = remember(lessonProgress) { lessonProgress.associateBy { it.lessonId } }
    var expandedDeptId by remember { mutableIntStateOf(1) }

    // Find next unmastered lesson in curriculum order
    val allLessons = remember { CurriculumCatalog.allDepartments.flatMap { it.lessons } }
    val nextLesson = remember(progressMap) {
        allLessons.firstOrNull { progressMap[it.id]?.isMastered != true } ?: allLessons.first()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner + Next Lesson CTA
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
            ) {
                Column {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_pomana),
                            contentDescription = "Pomana Club Learning Path Hero",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.Black.copy(alpha = 0.62f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "${analytics.completedLessons} / ${analytics.totalLessons} Lessons Mastered",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Up Next • Dept ${nextLesson.departmentId}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = nextLesson.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${nextLesson.durationMinutes} min • +${nextLesson.xpReward} XP • Practical Project Included",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            PomanaTactileButton(
                                text = "Continue Path",
                                icon = Icons.Filled.PlayArrow,
                                onClick = { onOpenLesson(nextLesson) },
                                modifier = Modifier.weight(1.3f),
                                testTag = "continue_path_button"
                            )
                            PomanaTactileButton(
                                text = "1-Min Sprint",
                                icon = Icons.Filled.Timer,
                                containerColor = PomanaAmber,
                                lipColor = PomanaAmberDark,
                                onClick = onStartMicroSprint,
                                modifier = Modifier.weight(1f),
                                testTag = "daily_sprint_button"
                            )
                        }
                    }
                }
            }
        }

        // 2. Real Practice Snapshot & Revision Card
        item {
            PomanaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Your Real Progress", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        val summary = if (analytics.totalAttempts == 0) {
                            "No exercises attempted yet — try a 1-minute sprint or Lesson 1!"
                        } else {
                            "${analytics.totalAttempts} exercises solved • ${analytics.accuracyPercent}% accuracy"
                        }
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (analytics.recommendedRevisionTopic != null) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Recommended Revision: ${analytics.recommendedRevisionTopic}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    MasteryRing(
                        progress = if (analytics.totalLessons == 0) 0f else analytics.completedLessons.toFloat() / analytics.totalLessons,
                        centerText = "${analytics.accuracyPercent}%"
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onStartRevision(analytics.recommendedRevisionTopic) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Outlined.AutoFixHigh, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Revise", maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = onOpenLab,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Outlined.ViewInAr, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("3D Lab", maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = onOpenCatalog,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Outlined.MenuBook, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("All 12", maxLines = 1)
                    }
                }
            }
        }

        // 3. Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Interactive Learning Path", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Pass each lesson's 75% mastery check to unlock the next stage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4. All 12 Progressive Departments as Duolingo-style Unit Nodes
        items(CurriculumCatalog.allDepartments, key = { it.id }) { dept ->
            DepartmentSkillUnitCard(
                department = dept,
                progressMap = progressMap,
                isExpanded = expandedDeptId == dept.id,
                onToggleExpand = {
                    expandedDeptId = if (expandedDeptId == dept.id) -1 else dept.id
                },
                onOpenLesson = onOpenLesson
            )
        }
    }
}

@Composable
private fun DepartmentSkillUnitCard(
    department: Department,
    progressMap: Map<String, LessonProgressEntity>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenLesson: (Lesson) -> Unit
) {
    val accent = Color(department.accentColorHex)
    val masteredCount = department.lessons.count { progressMap[it.id]?.isMastered == true }
    val totalCount = department.lessons.size.coerceAtLeast(1)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, accent.copy(alpha = if (isExpanded) 0.65f else 0.25f)),
        tonalElevation = 1.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${department.id}",
                        style = MaterialTheme.typography.titleLarge,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = department.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = department.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$masteredCount/$totalCount mastered • ${department.primarySoftware}",
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Toggle department",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Topic Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        department.topics.take(3).forEach { topic ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Winding Lesson Nodes inside the Department
                    department.lessons.forEachIndexed { idx, lesson ->
                        val prog = progressMap[lesson.id]
                        val prevLesson = department.lessons.getOrNull(idx - 1)
                        val isUnlocked = idx == 0 || (prevLesson != null && progressMap[prevLesson.id]?.isMastered == true)
                        val isMastered = prog?.isMastered == true

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isMastered) accent.copy(alpha = 0.12f)
                                    else if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                                .clickable(enabled = isUnlocked) { onOpenLesson(lesson) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isMastered -> accent
                                            isUnlocked -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isMastered -> Icons.Filled.WorkspacePremium
                                        isUnlocked -> Icons.Filled.PlayArrow
                                        else -> Icons.Filled.Lock
                                    },
                                    contentDescription = if (isUnlocked) "Open lesson" else "Locked until prior mastery check passes",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val statusText = when {
                                    isMastered -> "Mastered (${prog?.bestAccuracyPercent ?: 100}% best) • +${lesson.xpReward} XP"
                                    isUnlocked -> "${lesson.durationMinutes} min • 10-Step Lesson + Mastery Check"
                                    else -> "Complete previous lesson's 75% mastery check to unlock"
                                }
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isUnlocked) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = accent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
