package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.ThemeMode
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseType
import com.example.ui.PomanaViewModel
import com.example.ui.components.GamificationTopBar
import com.example.ui.screens.*
import com.example.ui.theme.PomanaClubTheme

private enum class PomanaDest(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    LEARN(R.string.nav_learn, Icons.Filled.Route, Icons.Outlined.Route),
    PRACTICE(R.string.nav_practice, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    LAB(R.string.nav_lab, Icons.Filled.ViewInAr, Icons.Outlined.ViewInAr),
    AI(R.string.nav_ai, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    STUDIO(R.string.nav_studio, Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
}

private sealed interface SubOverlay {
    data class LessonView(val lessonId: String) : SubOverlay
    data object CatalogView : SubOverlay
    data object ProjectsView : SubOverlay
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: PomanaViewModel = viewModel()
            val themeMode by vm.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            PomanaClubTheme(darkTheme = darkTheme) {
                PomanaRootApp(vm = vm, themeMode = themeMode)
            }
        }
    }
}

@Composable
private fun PomanaRootApp(
    vm: PomanaViewModel,
    themeMode: ThemeMode
) {
    val activeId by vm.activeLearnerId.collectAsStateWithLifecycle()
    val allProfiles by vm.allProfiles.collectAsStateWithLifecycle()
    val profile by vm.activeProfile.collectAsStateWithLifecycle()
    val lessonProgress by vm.lessonProgress.collectAsStateWithLifecycle()
    val attempts by vm.exerciseAttempts.collectAsStateWithLifecycle()
    val portfolio by vm.portfolioProjects.collectAsStateWithLifecycle()
    val achievements by vm.achievements.collectAsStateWithLifecycle()
    val aiMessages by vm.aiMessages.collectAsStateWithLifecycle()
    val adminConfig by vm.adminConfig.collectAsStateWithLifecycle()
    val analytics by vm.analytics.collectAsStateWithLifecycle()
    val practiceSession by vm.practiceState.collectAsStateWithLifecycle()
    val labState by vm.labState.collectAsStateWithLifecycle()
    val authError by vm.authError.collectAsStateWithLifecycle()
    val aiLoading by vm.aiLoading.collectAsStateWithLifecycle()
    val aiErrorBanner by vm.aiErrorBanner.collectAsStateWithLifecycle()
    val uiBanner by vm.uiBannerMessage.collectAsStateWithLifecycle()
    val soundEnabled by vm.soundEnabled.collectAsStateWithLifecycle()
    val shareProgressWithAi by vm.shareProgressWithAi.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiBanner) {
        val msg = uiBanner
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            vm.clearBanner()
        }
    }

    if (activeId == null) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            AuthOnboardingScreen(
                existingProfiles = allProfiles,
                authError = authError,
                onSignUp = { name, handle, color, admin -> vm.signUp(name, handle, color, admin) },
                onSignIn = { handle -> vm.signIn(handle) },
                onQuickSwitch = { id -> vm.switchProfile(id) },
                modifier = Modifier.padding(innerPadding)
            )
        }
        return
    }

    var currentDest by remember(activeId) { mutableStateOf(PomanaDest.LEARN) }
    var overlay by remember(activeId) { mutableStateOf<SubOverlay?>(null) }

    // Active full-screen overlays (Lesson 10-step player, Catalog, Projects/Software)
    when (val ov = overlay) {
        is SubOverlay.LessonView -> {
            val lesson = CurriculumCatalog.getLesson(ov.lessonId)
            if (lesson != null) {
                val prog = lessonProgress.find { it.lessonId == lesson.id }
                LessonPlayerScreen(
                    lesson = lesson,
                    progress = prog,
                    onBack = { overlay = null },
                    onStepChanged = { step -> vm.recordLessonStep(lesson.id, lesson.departmentId, step) },
                    onLaunchDepartmentPractice = { deptId ->
                        overlay = null
                        currentDest = PomanaDest.PRACTICE
                        vm.startPracticeSession(deptId, DifficultyTier.BASIC)
                    },
                    onSubmitMasteryQuiz = { correct, total, onResult ->
                        vm.submitLessonQuiz(lesson.id, lesson.departmentId, correct, total, lesson.xpReward, onResult)
                    },
                    onSubmitToPortfolio = { title, deptId, sw, desc ->
                        vm.addPortfolioProject(title, deptId, sw, desc, null)
                        overlay = SubOverlay.ProjectsView
                    }
                )
                return
            }
        }
        SubOverlay.CatalogView -> {
            CurriculumCatalogScreen(
                lessonProgress = lessonProgress,
                onBack = { overlay = null },
                onOpenLesson = { lesson -> overlay = SubOverlay.LessonView(lesson.id) },
                onPracticeDept = { deptId ->
                    overlay = null
                    currentDest = PomanaDest.PRACTICE
                    vm.startPracticeSession(deptId, DifficultyTier.BASIC)
                }
            )
            return
        }
        SubOverlay.ProjectsView -> {
            BackHandler { overlay = null }
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        title = { Text("Projects, Portfolio & Software") },
                        navigationIcon = {
                            IconButton(onClick = { overlay = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    )
                },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                ProjectsPortfolioScreen(
                    portfolio = portfolio,
                    onAddPortfolioProject = vm::addPortfolioProject,
                    onDeletePortfolioProject = vm::deletePortfolioProject,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            return
        }
        null -> Unit
    }

    // Adaptive Layout (NavigationRail on Tablets/Wide screens >= 600dp, NavigationBar on Phones)
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                GamificationTopBar(
                    profile = profile,
                    rankTitle = analytics.rankTitle,
                    themeMode = themeMode,
                    onToggleTheme = {
                        val next = when (themeMode) {
                            ThemeMode.SYSTEM -> ThemeMode.LIGHT
                            ThemeMode.LIGHT -> ThemeMode.DARK
                            ThemeMode.DARK -> ThemeMode.SYSTEM
                        }
                        vm.setThemeMode(next)
                    },
                    onOpenProfile = { currentDest = PomanaDest.STUDIO },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        PomanaDest.entries.forEach { dest ->
                            val selected = currentDest == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentDest = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes), maxLines = 1) }
                            )
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Spacer(Modifier.height(12.dp))
                        PomanaDest.entries.forEach { dest ->
                            val selected = currentDest == dest
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentDest = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .then(if (isWideScreen) Modifier.windowInsetsPadding(WindowInsets.navigationBars) else Modifier),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier.widthIn(max = 760.dp).fillMaxSize()) {
                        when (currentDest) {
                            PomanaDest.LEARN -> HomeDashboardScreen(
                                analytics = analytics,
                                lessonProgress = lessonProgress,
                                onOpenLesson = { lesson -> overlay = SubOverlay.LessonView(lesson.id) },
                                onStartMicroSprint = {
                                    currentDest = PomanaDest.PRACTICE
                                    vm.startPracticeSession(
                                        departmentId = null,
                                        difficulty = DifficultyTier.BASIC,
                                        preferredType = ExerciseType.MICRO_SPRINT,
                                        count = 4,
                                        title = "1-Minute Micro Sprint"
                                    )
                                },
                                onStartRevision = { topic ->
                                    currentDest = PomanaDest.PRACTICE
                                    vm.startPracticeSession(
                                        departmentId = null,
                                        difficulty = DifficultyTier.EASY,
                                        preferredType = null,
                                        count = 5,
                                        title = if (topic != null) "Revision: $topic" else "Smart Revision Drill"
                                    )
                                },
                                onOpenLab = { currentDest = PomanaDest.LAB },
                                onOpenCatalog = { overlay = SubOverlay.CatalogView }
                            )

                            PomanaDest.PRACTICE -> PracticeCenterScreen(
                                session = practiceSession,
                                adminConfig = adminConfig,
                                isAiGenerating = aiLoading,
                                onStartSession = { dept, diff, type, count ->
                                    vm.startPracticeSession(dept, diff, type, count)
                                },
                                onGenerateAiExercise = { dept, diff, type ->
                                    vm.startAiGeneratedExercise(dept, diff, type)
                                },
                                onSubmitAnswer = vm::submitPracticeAnswer,
                                onCloseSession = vm::closePracticeSession
                            )

                            PomanaDest.LAB -> Interactive3DLabScreen(
                                state = labState,
                                onUpdateState = vm::updateLabState,
                                onRequestAiInsight = vm::requestLabAiInsight
                            )

                            PomanaDest.AI -> PomanaAiScreen(
                                messages = aiMessages,
                                adminConfig = adminConfig,
                                analytics = analytics,
                                isLoading = aiLoading,
                                errorBanner = aiErrorBanner,
                                onSendMessage = vm::sendAiMessage,
                                onClearChat = vm::clearAiChat
                            )

                            PomanaDest.STUDIO -> StudioHubScreen(
                                profile = profile,
                                allProfiles = allProfiles,
                                analytics = analytics,
                                attempts = attempts,
                                achievements = achievements,
                                portfolio = portfolio,
                                themeMode = themeMode,
                                soundEnabled = soundEnabled,
                                shareProgressWithAi = shareProgressWithAi,
                                adminConfig = adminConfig,
                                onOpenProjects = { overlay = SubOverlay.ProjectsView },
                                onOpenCatalog = { overlay = SubOverlay.CatalogView },
                                onStartRevision = { topic ->
                                    currentDest = PomanaDest.PRACTICE
                                    vm.startPracticeSession(
                                        departmentId = null,
                                        difficulty = DifficultyTier.EASY,
                                        count = 5,
                                        title = if (topic != null) "Revision: $topic" else "Smart Revision"
                                    )
                                },
                                onSetThemeMode = vm::setThemeMode,
                                onSetSoundEnabled = vm::setSoundEnabled,
                                onSetShareProgressWithAi = vm::setShareProgressWithAi,
                                onSetDailyGoal = vm::setDailyGoal,
                                onToggleAdminRole = vm::toggleAdminRole,
                                onSaveAdminConfig = vm::saveAdminConfig,
                                onSignOut = vm::signOut
                            )
                        }
                    }
                }
            }
        }
    }
}
