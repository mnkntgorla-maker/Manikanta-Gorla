package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.PortfolioProjectEntity
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton

@Composable
fun ProjectsPortfolioScreen(
    portfolio: List<PortfolioProjectEntity>,
    onAddPortfolioProject: (title: String, deptId: Int, software: String, desc: String, imageUri: String?) -> Unit,
    onDeletePortfolioProject: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Portfolio", "Project Briefs", "Free 3D Apps")

    // Add Portfolio Form State
    var showUploadDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var softwareInput by remember { mutableStateOf("Prisma3D") }
    var descInput by remember { mutableStateOf("") }
    var deptIdInput by remember { mutableIntStateOf(1) }
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) pickedImageUri = uri
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeTab) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = activeTab == idx,
                    onClick = { activeTab = idx },
                    text = { Text(label, maxLines = 1) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (activeTab) {
                0 -> {
                    item {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.img_project_showcase),
                                    contentDescription = "3D Portfolio Showcase",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Your 3D Project Portfolio", style = MaterialTheme.typography.titleLarge)
                                    Text(
                                        "Attach real renders/screenshots from Prisma3D, Tinkercad, Blockbench, or Blender to build your studio reel (+30 XP each).",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    PomanaTactileButton(
                                        text = "Upload Project to Portfolio",
                                        icon = Icons.Filled.AddAPhoto,
                                        onClick = { showUploadDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        testTag = "open_portfolio_upload_btn"
                                    )
                                }
                            }
                        }
                    }

                    if (portfolio.isEmpty()) {
                        item {
                            PomanaCard {
                                Text("No Portfolio Pieces Yet", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Start with '3D Cube & Sphere Pedestal' in Department 1 or tap Upload Project above.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(portfolio, key = { it.id }) { item ->
                            PomanaCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "Dept ${item.departmentId} • ${item.softwareUsed} • ${item.status}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { onDeletePortfolioProject(item.id) }) {
                                        Icon(Icons.Outlined.DeleteOutline, "Delete portfolio project")
                                    }
                                }
                                if (!item.imageUri.isNullOrBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    AsyncImage(
                                        model = Uri.parse(item.imageUri),
                                        contentDescription = item.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(170.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                if (item.description.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                                }
                                if (item.evaluationFeedback.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                    ) {
                                        Text(
                                            text = item.evaluationFeedback,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    item {
                        Text("Progressive Practical Projects", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Real 3D modeling & animation assignments built for free mobile/desktop tools.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(CurriculumCatalog.allProjects, key = { it.id }) { proj ->
                        PomanaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dept ${proj.departmentId}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                DifficultyBadge(proj.difficulty)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(proj.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(proj.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Recommended: ${proj.recommendedSoftware}", style = MaterialTheme.typography.labelSmall)
                            Text(proj.freeVsPaidNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    titleInput = proj.title
                                    deptIdInput = proj.departmentId
                                    softwareInput = proj.recommendedSoftware
                                    descInput = ""
                                    showUploadDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Submit This Project to Portfolio")
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        Text("Free Mobile & Desktop 3D Tools", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Beginner path never requires paid software. Paid features and device limits are clearly noted below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(CurriculumCatalog.allSoftwareGuides, key = { it.id }) { tool ->
                        PomanaCard {
                            Text(tool.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            Text("${tool.platforms} • ${tool.costModel}", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Best for: ${tool.bestFor}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Limitations / Paid Info: ${tool.paidLimitations}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Quick Start:", style = MaterialTheme.typography.titleSmall)
                            tool.quickStartSteps.forEachIndexed { idx, step ->
                                Text("${idx + 1}. $step", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Add to Portfolio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiNoticeBanner(text = "Project evaluation uses sample AI preview feedback unless Firebase AI Logic is enabled.")
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Project Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = softwareInput,
                        onValueChange = { softwareInput = it },
                        label = { Text("3D Software Used") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Notes / What you built") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.AddPhotoAlternate, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (pickedImageUri == null) "Attach Screenshot (Photo Picker)" else "Screenshot Selected ✓")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddPortfolioProject(
                            titleInput,
                            deptIdInput,
                            softwareInput,
                            descInput,
                            pickedImageUri?.toString()
                        )
                        titleInput = ""
                        descInput = ""
                        pickedImageUri = null
                        showUploadDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) { Text("Cancel") }
            }
        )
    }
}
