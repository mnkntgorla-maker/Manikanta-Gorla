package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.LearnerProfileEntity
import com.example.ui.components.PomanaCard
import com.example.ui.components.PomanaTactileButton
import com.example.ui.theme.*

@Composable
fun AuthOnboardingScreen(
    existingProfiles: List<LearnerProfileEntity>,
    authError: String?,
    onSignUp: (name: String, handle: String, colorIdx: Int, asAdmin: Boolean) -> Unit,
    onSignIn: (handle: String) -> Unit,
    onQuickSwitch: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoginTab by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var handleInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableIntStateOf(0) }
    var createAsAdmin by remember { mutableStateOf(false) }

    val avatarColors = listOf(PomanaEmerald, PomanaSky, PomanaAmber, PomanaCoral, PomanaViolet)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_pomana),
                        contentDescription = "Pomana Club 3D Animation Studio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.tagline),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Master 3D animation from absolute zero through 12 departments, interactive 3D experiments, mobile workflows & massive hands-on practice.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                        )
                    }
                }
            }

            // Existing Local Learner Accounts (Real stored accounts only)
            if (existingProfiles.isNotEmpty()) {
                PomanaCard {
                    Text(
                        text = "Continue on This Device",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    existingProfiles.forEach { prof ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onQuickSwitch(prof.id) }
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(avatarColors[prof.avatarColorIndex % avatarColors.size]),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        prof.displayName.firstOrNull()?.uppercase() ?: "P",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(prof.displayName, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "@${prof.handle} • ${prof.totalXp} XP • ${prof.streakDays}d streak",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = "Open profile",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Sign Up / Login Card
            PomanaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isLoginTab,
                        onClick = { isLoginTab = false },
                        label = { Text("Sign Up") },
                        leadingIcon = { Icon(Icons.Outlined.PersonAdd, null, Modifier.size(18.dp)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_signup")
                    )
                    FilterChip(
                        selected = isLoginTab,
                        onClick = { isLoginTab = true },
                        label = { Text("Login") },
                        leadingIcon = { Icon(Icons.Outlined.Login, null, Modifier.size(18.dp)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_login")
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (!isLoginTab) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Your Name") },
                        placeholder = { Text("e.g. Maya Patel") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_name_input")
                    )
                    Spacer(Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = handleInput,
                    onValueChange = { handleInput = it },
                    label = { Text("Username Handle") },
                    placeholder = { Text("e.g. maya_3d") },
                    prefix = { Text("@") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_handle_input")
                )

                if (!isLoginTab) {
                    Spacer(Modifier.height(12.dp))
                    Text("Choose Badge Color", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        avatarColors.forEachIndexed { idx, col ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .clickable { selectedColor = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColor == idx) {
                                    Icon(Icons.Filled.Check, "Selected", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Enable Educator / Admin Tools", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Lets you configure AI usage quotas & inspect curriculum controls.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = createAsAdmin,
                            onCheckedChange = { createAsAdmin = it }
                        )
                    }
                }

                if (!authError.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = authError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(16.dp))
                PomanaTactileButton(
                    text = if (isLoginTab) "Enter Studio" else "Start Learning Free",
                    icon = if (isLoginTab) Icons.Filled.Login else Icons.Filled.RocketLaunch,
                    onClick = {
                        if (isLoginTab) onSignIn(handleInput)
                        else onSignUp(nameInput, handleInput, selectedColor, createAsAdmin)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "auth_submit_button"
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
