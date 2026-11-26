package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.LearnerProfileEntity
import com.example.data.local.ThemeMode
import com.example.data.model.DifficultyTier
import com.example.ui.theme.*

@Composable
fun PomanaTactileButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    lipColor: Color = PomanaEmeraldLip,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val bg by animateColorAsState(
        targetValue = if (enabled) containerColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "btn_bg"
    )
    val lip = if (enabled) lipColor else MaterialTheme.colorScheme.outline
    val fg = if (enabled) contentColor else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(lip)
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PomanaCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = RoundedCornerShape(22.dp),
        color = containerColor,
        border = BorderStroke(1.5.dp, borderColor),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun GamificationTopBar(
    profile: LearnerProfileEntity?,
    rankTitle: String,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dailyGoal = (profile?.dailyGoalXp ?: 50).coerceAtLeast(10)
    val todayXp = profile?.todayXp ?: 0
    val goalProgress by animateFloatAsState(
        targetValue = (todayXp.toFloat() / dailyGoal).coerceIn(0f, 1f),
        animationSpec = spring(),
        label = "goal_progress"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenProfile)
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profile?.displayName?.firstOrNull() ?: 'P').uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile?.displayName ?: "Pomana Learner",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = rankTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        icon = Icons.Filled.LocalFireDepartment,
                        value = "${profile?.streakDays ?: 0}",
                        tint = PomanaCoral,
                        bg = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                        contentDesc = "Daily streak"
                    )
                    StatPill(
                        icon = Icons.Filled.Bolt,
                        value = "${profile?.totalXp ?: 0}",
                        tint = PomanaAmberDark,
                        bg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        contentDesc = "Total XP"
                    )
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("toggle_theme_button")
                    ) {
                        Icon(
                            imageVector = when (themeMode) {
                                ThemeMode.DARK -> Icons.Outlined.LightMode
                                ThemeMode.LIGHT -> Icons.Outlined.DarkMode
                                ThemeMode.SYSTEM -> Icons.Outlined.BrightnessAuto
                            },
                            contentDescription = "Switch theme mode",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Goal: $todayXp / $dailyGoal XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(10.dp))
                LinearProgressIndicator(
                    progress = { goalProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatPill(
    icon: ImageVector,
    value: String,
    tint: Color,
    bg: Color,
    contentDesc: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MasteryRing(
    progress: Float,
    size: Dp = 54.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 6.dp,
    centerText: String? = null
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(),
        label = "ring"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        if (centerText != null) {
            Text(
                text = centerText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: DifficultyTier) {
    val (bg, fg) = when (difficulty) {
        DifficultyTier.BASIC -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        DifficultyTier.EASY -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        DifficultyTier.INTERMEDIATE -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        DifficultyTier.DIFFICULT -> PomanaVioletContainer to Color(0xFF4C1D95)
        DifficultyTier.ADVANCED -> PomanaCoralContainer to Color(0xFF9F1239)
        DifficultyTier.EXPERT -> Color(0xFF1F2937) to Color(0xFFFDE68A)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = difficulty.label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AiNoticeBanner(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.ai_firebase_notice)
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "AI setup notice",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
