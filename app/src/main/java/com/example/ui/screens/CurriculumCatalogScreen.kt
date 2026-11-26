package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.LessonProgressEntity
import com.example.data.model.Lesson
import com.example.ui.components.MasteryRing
import com.example.ui.components.PomanaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumCatalogScreen(
    lessonProgress: List<LessonProgressEntity>,
    onBack: () -> Unit,
    onOpenLesson: (Lesson) -> Unit,
    onPracticeDept: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    var searchQuery by remember { mutableStateOf("") }
    val progressMap = remember(lessonProgress) { lessonProgress.associateBy { it.lessonId } }

    val filteredDepartments = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) CurriculumCatalog.allDepartments
        else CurriculumCatalog.allDepartments.filter { dept ->
            dept.title.lowercase().contains(q) ||
                dept.subtitle.lowercase().contains(q) ||
                dept.topics.any { it.lowercase().contains(q) } ||
                dept.primarySoftware.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("12-Department Course Catalogue") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    placeholder = { Text("Search topics (rigging, UV, Prisma3D, VFX...)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(filteredDepartments, key = { it.id }) { dept ->
                val mastered = dept.lessons.count { progressMap[it.id]?.isMastered == true }
                val total = dept.lessons.size.coerceAtLeast(1)
                val accent = Color(dept.accentColorHex)

                PomanaCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MasteryRing(
                            progress = mastered.toFloat() / total,
                            size = 50.dp,
                            color = accent,
                            centerText = "D${dept.id}"
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dept.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                dept.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Software: ${dept.primarySoftware}",
                                style = MaterialTheme.typography.labelSmall,
                                color = accent
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Topics: ${dept.topics.joinToString(" • ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dept.lessons.firstOrNull()?.let { firstLesson ->
                            Button(
                                onClick = { onOpenLesson(firstLesson) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.PlayArrow, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Lesson")
                            }
                        }
                        OutlinedButton(
                            onClick = { onPracticeDept(dept.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.FitnessCenter, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Practice")
                        }
                    }
                }
            }
        }
    }
}
