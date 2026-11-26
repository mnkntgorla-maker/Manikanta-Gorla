package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Image
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
import com.example.data.ai.PomanaAiMode
import com.example.data.local.AdminConfigEntity
import com.example.data.local.AiConversationEntity
import com.example.data.repository.LearnerAnalytics
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.PomanaCard

@Composable
fun PomanaAiScreen(
    messages: List<AiConversationEntity>,
    adminConfig: AdminConfigEntity,
    analytics: LearnerAnalytics,
    isLoading: Boolean,
    errorBanner: String?,
    onSendMessage: (prompt: String, mode: PomanaAiMode, hasImage: Boolean) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(PomanaAiMode.TUTOR) }
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            selectedMode = PomanaAiMode.VISUAL
        }
    }

    val quickPrompts = remember(analytics.recommendedRevisionTopic, selectedMode) {
        when (selectedMode) {
            PomanaAiMode.TUTOR -> listOf(
                "Explain IK vs FK with a simple analogy",
                "Why do quad edge loops matter at knees?",
                "How do Roughness and Metallic work?"
            )
            PomanaAiMode.EXAMINER -> listOf(
                "Examine me on 3-point lighting!",
                "Test my understanding of 24fps timing"
            )
            PomanaAiMode.MENTOR -> listOf(
                "Break down modelling a low-poly robot in Prisma3D",
                "Plan my first 10-second short animated film"
            )
            PomanaAiMode.REVISION -> listOf(
                "Help me revise ${analytics.recommendedRevisionTopic ?: "3D Coordinates"}",
                "Give me a 3-step revision plan for my accuracy (${analytics.accuracyPercent}%)"
            )
            PomanaAiMode.VISUAL -> listOf(
                "Review silhouette & lighting of my attached render",
                "Check proportions on my reference sheet"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Header with Avatar & Daily Quota
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.img_ai_tutor_avatar),
                    contentDescription = "Pomana AI Mentor Avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Pomana AI", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${adminConfig.aiQuestionsUsedToday}/${adminConfig.dailyAiQuestionLimit} questions today",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (messages.isNotEmpty()) {
                IconButton(onClick = onClearChat) {
                    Icon(Icons.Outlined.DeleteOutline, "Clear conversation")
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        AiNoticeBanner()
        Spacer(Modifier.height(8.dp))

        // Mode Switcher
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PomanaAiMode.entries) { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = { Text(mode.label) }
                )
            }
        }
        Text(
            text = selectedMode.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (!errorBanner.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = errorBanner,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Conversation List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    PomanaCard {
                        Text("Ask Pomana AI Anything in 3D!", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Switch between Tutor, Examiner, Project Mentor, Revision Coach, and Visual Feedback modes. Tap a starter prompt below or attach a screenshot.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        quickPrompts.forEach { qp ->
                            SuggestionChip(
                                onClick = { inputText = qp },
                                label = { Text(qp) },
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isUser = msg.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "You • ${msg.mode}" else "Pomana AI • ${msg.mode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                else MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Attached Photo Preview (Zero-permission Photo Picker)
        if (selectedImageUri != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Attached reference image",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Reference image attached", style = MaterialTheme.typography.labelMedium)
                }
                IconButton(onClick = { selectedImageUri = null }) {
                    Icon(Icons.Filled.Close, "Remove image")
                }
            }
            Spacer(Modifier.height(6.dp))
        }

        // Input Composer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.testTag("ai_attach_image_btn")
            ) {
                Icon(Icons.Outlined.Image, "Attach 3D screenshot")
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Pomana AI...") },
                maxLines = 3,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input")
            )

            FilledIconButton(
                onClick = {
                    val prompt = inputText.trim()
                    if (prompt.isNotEmpty() || selectedImageUri != null) {
                        onSendMessage(prompt, selectedMode, selectedImageUri != null)
                        inputText = ""
                        selectedImageUri = null
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("ai_send_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send message")
                }
            }
        }
    }
}
