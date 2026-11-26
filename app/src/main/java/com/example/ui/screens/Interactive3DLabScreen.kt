package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.Lab3DState
import com.example.ui.components.AiNoticeBanner
import com.example.ui.components.Interactive3DViewport
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton
import com.example.ui.theme.PomanaSky
import com.example.ui.theme.PomanaSkyDark
import kotlin.math.roundToInt

@Composable
fun Interactive3DLabScreen(
    state: Lab3DState,
    onUpdateState: ((Lab3DState) -> Lab3DState) -> Unit,
    onRequestAiInsight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Geometry", "Material", "Lights & Cam", "Animation")
    val primitives = listOf("Cube", "Pyramid", "Octahedron", "Cylinder", "Rocket")
    val renderModes = listOf("Shaded", "Wireframe", "Axes")
    val colorOptions = listOf(
        Color(0xFF22C55E),
        Color(0xFF0EA5E9),
        Color(0xFFF59E0B),
        Color(0xFFF43F5E),
        Color(0xFFA855F7)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Interactive 3D Laboratory", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Drag to orbit • Test shaders, 3-point lights, camera FOV & keyframes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = {
                    onUpdateState {
                        it.copy(yawDeg = 32f, pitchDeg = 22f, zoom = 1.0f, fovScale = 1.0f)
                    }
                }
            ) {
                Icon(Icons.Filled.RestartAlt, "Reset camera")
            }
        }

        // Live 3D Viewport
        Interactive3DViewport(
            state = state,
            onRotateDrag = { dx, dy ->
                onUpdateState {
                    it.copy(
                        yawDeg = it.yawDeg + dx,
                        pitchDeg = (it.pitchDeg + dy).coerceIn(-75f, 75f)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .testTag("lab_3d_viewport")
        )

        // Sub-Tabs
        TabRow(selectedTabIndex = activeTab) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = activeTab == idx,
                    onClick = { activeTab = idx },
                    text = { Text(label, maxLines = 1) }
                )
            }
        }

        PomanaCard {
            when (activeTab) {
                0 -> {
                    Text("Select 3D Model", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(primitives) { prim ->
                            FilterChip(
                                selected = state.primitive == prim,
                                onClick = { onUpdateState { it.copy(primitive = prim) } },
                                label = { Text(prim) }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Inspection Mode", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        renderModes.forEach { mode ->
                            FilterChip(
                                selected = state.renderMode == mode,
                                onClick = { onUpdateState { it.copy(renderMode = mode) } },
                                label = { Text(mode) }
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Zoom: ${"%.2f".format(state.zoom)}x", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = state.zoom,
                        onValueChange = { z -> onUpdateState { it.copy(zoom = z) } },
                        valueRange = 0.6f..1.7f
                    )
                }
                1 -> {
                    Text("Base Color", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        colorOptions.forEachIndexed { idx, col ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .clickable { onUpdateState { it.copy(baseColorIndex = idx) } },
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.baseColorIndex == idx) {
                                    Icon(Icons.Filled.Check, "Selected color", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Roughness: ${"%.2f".format(state.roughness)} (0 = Glossy, 1 = Matte)", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = state.roughness,
                        onValueChange = { r -> onUpdateState { it.copy(roughness = r) } },
                        valueRange = 0.05f..1.0f
                    )
                    Text("Metallic: ${"%.2f".format(state.metallic)} (0 = Plastic/Clay, 1 = Metal)", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = state.metallic,
                        onValueChange = { m -> onUpdateState { it.copy(metallic = m) } },
                        valueRange = 0.0f..1.0f
                    )
                }
                2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Key Light (Main)", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = state.keyLightEnabled,
                            onCheckedChange = { v -> onUpdateState { it.copy(keyLightEnabled = v) } }
                        )
                    }
                    Text("Key Azimuth Angle: ${state.keyAngleDeg.roundToInt()}°", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = state.keyAngleDeg,
                        onValueChange = { a -> onUpdateState { it.copy(keyAngleDeg = a) } },
                        valueRange = 0f..360f
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fill Light (Soften Shadows)", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = state.fillLightEnabled,
                            onCheckedChange = { v -> onUpdateState { it.copy(fillLightEnabled = v) } }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rim Light (Edge Separation)", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = state.rimLightEnabled,
                            onCheckedChange = { v -> onUpdateState { it.copy(rimLightEnabled = v) } }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rule of Thirds Camera Grid", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = state.showRuleOfThirds,
                            onCheckedChange = { v -> onUpdateState { it.copy(showRuleOfThirds = v) } }
                        )
                    }
                    Text(
                        "Camera Lens Scale: ${"%.2f".format(state.fovScale)} (${if (state.fovScale < 0.9f) "Wide 24mm" else if (state.fovScale > 1.2f) "Telephoto 85mm" else "Standard 50mm"})",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = state.fovScale,
                        onValueChange = { f -> onUpdateState { it.copy(fovScale = f) } },
                        valueRange = 0.7f..1.5f
                    )
                }
                3 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PomanaTactileButton(
                            text = if (state.isPlayingAnim) "Pause" else "Play Loop",
                            icon = if (state.isPlayingAnim) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            onClick = { onUpdateState { it.copy(isPlayingAnim = !it.isPlayingAnim) } },
                            modifier = Modifier.weight(1f),
                            testTag = "lab_play_anim_btn"
                        )
                        OutlinedButton(
                            onClick = { onUpdateState { it.copy(isPlayingAnim = false, currentFrame = 0f) } }
                        ) {
                            Text("Reset")
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("Scrub Timeline: Frame ${state.currentFrame.roundToInt()} / ${state.totalFrames}", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = state.currentFrame,
                        onValueChange = { f -> onUpdateState { it.copy(isPlayingAnim = false, currentFrame = f) } },
                        valueRange = 0f..state.totalFrames.toFloat()
                    )
                    Text("Frame Rate (FPS)", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(12, 24, 30, 60).forEach { fps ->
                            FilterChip(
                                selected = state.fps == fps,
                                onClick = { onUpdateState { it.copy(fps = fps) } },
                                label = { Text("${fps}fps") }
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Interpolation Curve", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Linear", "Ease In-Out", "Bounce").forEach { ease ->
                            FilterChip(
                                selected = state.easingMode == ease,
                                onClick = { onUpdateState { it.copy(easingMode = ease) } },
                                label = { Text(ease) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Squash & Stretch Volume", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = state.squashStretchEnabled,
                            onCheckedChange = { v -> onUpdateState { it.copy(squashStretchEnabled = v) } }
                        )
                    }
                }
            }
        }

        // AI Lab Insight Card
        PomanaCard(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)) {
            Text("AI 3D Experiment Lab Insight", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            AiNoticeBanner()
            Spacer(Modifier.height(10.dp))
            PomanaTactileButton(
                text = if (state.isAnalyzingLab) "Analyzing Setup..." else "Ask Pomana AI About Current Settings",
                icon = Icons.Filled.AutoAwesome,
                containerColor = PomanaSky,
                lipColor = PomanaSkyDark,
                enabled = !state.isAnalyzingLab,
                onClick = onRequestAiInsight,
                modifier = Modifier.fillMaxWidth()
            )
            if (!state.aiLabInsight.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(state.aiLabInsight, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
