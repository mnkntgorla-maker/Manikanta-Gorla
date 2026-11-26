package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiRepository
import com.example.data.ai.AiResult
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.ai.PomanaAiMode
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.curriculum.ProceduralExerciseGenerator
import com.example.data.local.*
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseItem
import com.example.data.model.ExerciseType
import com.example.data.repository.LearnerAnalytics
import com.example.data.repository.PomanaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PracticeSessionState(
    val isActive: Boolean = false,
    val title: String = "Practice Session",
    val items: List<ExerciseItem> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val xpEarned: Int = 0,
    val isFinished: Boolean = false,
    val itemStartTimeMs: Long = System.currentTimeMillis()
)

data class Lab3DState(
    val primitive: String = "Cube", // Cube, Pyramid, Octahedron, Cylinder, Rocket
    val renderMode: String = "Shaded", // Shaded, Wireframe, Axes
    val yawDeg: Float = 32f,
    val pitchDeg: Float = 22f,
    val zoom: Float = 1.0f,
    val fovScale: Float = 1.0f, // 0.7 wide -> 1.5 tele
    val roughness: Float = 0.45f,
    val metallic: Float = 0.15f,
    val baseColorIndex: Int = 0,
    val keyLightEnabled: Boolean = true,
    val keyAngleDeg: Float = 45f,
    val fillLightEnabled: Boolean = true,
    val rimLightEnabled: Boolean = true,
    val showRuleOfThirds: Boolean = false,
    val isPlayingAnim: Boolean = false,
    val fps: Int = 24,
    val totalFrames: Int = 48,
    val currentFrame: Float = 0f,
    val easingMode: String = "Bounce", // Linear, Ease In-Out, Bounce
    val squashStretchEnabled: Boolean = true,
    val aiLabInsight: String? = null,
    val isAnalyzingLab: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class PomanaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PomanaDatabase.getInstance(application)
    private val prefs = PreferencesManager(application)
    private val aiRepo: AiRepository = PlaceholderAiRepository()
    val repository = PomanaRepository(db.pomanaDao(), prefs, aiRepo)

    val activeLearnerId: StateFlow<Long?> = repository.activeLearnerId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val soundEnabled: StateFlow<Boolean> = repository.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val shareProgressWithAi: StateFlow<Boolean> = repository.shareProgressWithAi
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val allProfiles: StateFlow<List<LearnerProfileEntity>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfigEntity> = repository.adminConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminConfigEntity())

    val activeProfile: StateFlow<LearnerProfileEntity?> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.observeProfile(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lessonProgress: StateFlow<List<LessonProgressEntity>> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeLessonProgress(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exerciseAttempts: StateFlow<List<ExerciseAttemptEntity>> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeAttempts(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolioProjects: StateFlow<List<PortfolioProjectEntity>> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observePortfolio(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeAchievements(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMessages: StateFlow<List<AiConversationEntity>> = activeLearnerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeAiMessages(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<LearnerAnalytics> = combine(
        activeProfile,
        lessonProgress,
        exerciseAttempts
    ) { profile, progress, attempts ->
        repository.computeAnalytics(profile, progress, attempts)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.computeAnalytics(null, emptyList(), emptyList())
    )

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _practiceState = MutableStateFlow(PracticeSessionState())
    val practiceState: StateFlow<PracticeSessionState> = _practiceState.asStateFlow()

    private val _labState = MutableStateFlow(Lab3DState())
    val labState: StateFlow<Lab3DState> = _labState.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiErrorBanner = MutableStateFlow<String?>(null)
    val aiErrorBanner: StateFlow<String?> = _aiErrorBanner.asStateFlow()

    private val _uiBannerMessage = MutableStateFlow<String?>(null)
    val uiBannerMessage: StateFlow<String?> = _uiBannerMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureAdminConfigSeeded()
        }
        // Animation loop for Interactive 3D Lab
        viewModelScope.launch {
            while (true) {
                val st = _labState.value
                if (st.isPlayingAnim) {
                    val stepMs = (1000L / st.fps.coerceAtLeast(12)).coerceAtLeast(16L)
                    delay(stepMs)
                    val next = (_labState.value.currentFrame + 1f) % _labState.value.totalFrames
                    _labState.update { it.copy(currentFrame = next) }
                } else {
                    delay(120L)
                }
            }
        }
    }

    fun clearBanner() {
        _uiBannerMessage.value = null
        _aiErrorBanner.value = null
        _authError.value = null
    }

    // Auth actions
    fun signUp(name: String, handle: String, colorIdx: Int, asAdmin: Boolean) {
        viewModelScope.launch {
            _authError.value = null
            repository.createAndSignInLearner(name, handle, colorIdx, asAdmin)
                .onFailure { _authError.value = it.message }
        }
    }

    fun signIn(handle: String) {
        viewModelScope.launch {
            _authError.value = null
            repository.signInByHandle(handle)
                .onFailure { _authError.value = it.message }
        }
    }

    fun switchProfile(id: Long) {
        viewModelScope.launch { repository.switchLearner(id) }
    }

    fun signOut() {
        viewModelScope.launch { repository.signOut() }
    }

    // Practice Engine Actions
    fun startPracticeSession(
        departmentId: Int?,
        difficulty: DifficultyTier,
        preferredType: ExerciseType? = null,
        count: Int = 6,
        title: String? = null
    ) {
        val items = ProceduralExerciseGenerator.generateSession(
            departmentId = departmentId,
            difficulty = difficulty,
            preferredType = preferredType,
            count = count
        )
        val deptTitle = departmentId?.let { CurriculumCatalog.getDepartment(it)?.title } ?: "All Departments"
        _practiceState.value = PracticeSessionState(
            isActive = true,
            title = title ?: "$deptTitle • ${difficulty.label}",
            items = items,
            currentIndex = 0,
            correctCount = 0,
            xpEarned = 0,
            isFinished = false,
            itemStartTimeMs = System.currentTimeMillis()
        )
    }

    fun startAiGeneratedExercise(departmentId: Int, difficulty: DifficultyTier, type: ExerciseType) {
        viewModelScope.launch {
            _aiLoading.value = true
            when (val res = repository.generateAiPracticeItem(departmentId, difficulty, type)) {
                is AiResult.Success -> {
                    _practiceState.value = PracticeSessionState(
                        isActive = true,
                        title = "Pomana AI Practice • ${difficulty.label}",
                        items = listOf(res.data),
                        currentIndex = 0,
                        correctCount = 0,
                        xpEarned = 0,
                        isFinished = false,
                        itemStartTimeMs = System.currentTimeMillis()
                    )
                }
                is AiResult.LimitReached -> _uiBannerMessage.value = res.message
                is AiResult.Error -> _uiBannerMessage.value = res.message
            }
            _aiLoading.value = false
        }
    }

    fun submitPracticeAnswer(isCorrect: Boolean) {
        val st = _practiceState.value
        val item = st.items.getOrNull(st.currentIndex) ?: return
        val learnerId = activeLearnerId.value ?: return
        val elapsedSec = ((System.currentTimeMillis() - st.itemStartTimeMs) / 1000L).toInt().coerceIn(1, 300)
        val earned = if (isCorrect) item.xpReward else 3

        viewModelScope.launch {
            repository.recordExerciseAttempt(learnerId, item, isCorrect, elapsedSec)
        }

        val nextIdx = st.currentIndex + 1
        _practiceState.value = st.copy(
            correctCount = st.correctCount + if (isCorrect) 1 else 0,
            xpEarned = st.xpEarned + earned,
            currentIndex = nextIdx,
            isFinished = nextIdx >= st.items.size,
            itemStartTimeMs = System.currentTimeMillis()
        )
    }

    fun closePracticeSession() {
        _practiceState.value = PracticeSessionState()
    }

    // Lesson Actions
    fun recordLessonStep(lessonId: String, departmentId: Int, step: Int) {
        val learnerId = activeLearnerId.value ?: return
        viewModelScope.launch {
            repository.updateLessonStep(learnerId, lessonId, departmentId, step)
        }
    }

    fun submitLessonQuiz(
        lessonId: String,
        departmentId: Int,
        correctAnswers: Int,
        totalQuestions: Int,
        xpReward: Int,
        onResult: (Boolean, Int) -> Unit
    ) {
        val learnerId = activeLearnerId.value ?: return
        val accuracy = if (totalQuestions == 0) 0 else ((correctAnswers * 100f) / totalQuestions).toInt()
        viewModelScope.launch {
            val passed = repository.submitLessonMasteryResult(learnerId, lessonId, departmentId, accuracy, xpReward)
            onResult(passed, accuracy)
        }
    }

    // 3D Lab Actions
    fun updateLabState(transform: (Lab3DState) -> Lab3DState) {
        _labState.update(transform)
    }

    fun requestLabAiInsight() {
        val st = _labState.value
        viewModelScope.launch {
            _labState.update { it.copy(isAnalyzingLab = true) }
            when (val res = aiRepo.explainLabExperiment(
                primitive = st.primitive,
                roughness = st.roughness,
                metallic = st.metallic,
                keyEnabled = st.keyLightEnabled,
                fillEnabled = st.fillLightEnabled,
                rimEnabled = st.rimLightEnabled,
                fps = st.fps,
                easing = st.easingMode
            )) {
                is AiResult.Success -> _labState.update { it.copy(aiLabInsight = res.data, isAnalyzingLab = false) }
                is AiResult.LimitReached -> _labState.update { it.copy(aiLabInsight = res.message, isAnalyzingLab = false) }
                is AiResult.Error -> _labState.update { it.copy(aiLabInsight = res.message, isAnalyzingLab = false) }
            }
        }
    }

    // Portfolio Actions
    fun addPortfolioProject(
        title: String,
        departmentId: Int,
        softwareUsed: String,
        description: String,
        imageUri: String?
    ) {
        val learnerId = activeLearnerId.value ?: return
        if (title.isBlank()) {
            _uiBannerMessage.value = "Please enter a project title."
            return
        }
        viewModelScope.launch {
            repository.savePortfolioProject(learnerId, title, departmentId, softwareUsed, description, imageUri)
            _uiBannerMessage.value = "Project added to Portfolio (+30 XP)!"
        }
    }

    fun deletePortfolioProject(projectId: Long) {
        viewModelScope.launch { repository.deletePortfolioProject(projectId) }
    }

    // Pomana AI Actions
    fun sendAiMessage(prompt: String, mode: PomanaAiMode, hasAttachedImage: Boolean) {
        val learnerId = activeLearnerId.value ?: return
        viewModelScope.launch {
            _aiLoading.value = true
            _aiErrorBanner.value = null
            when (val res = repository.sendAiChatMessage(
                learnerId = learnerId,
                prompt = prompt,
                mode = mode,
                analytics = analytics.value,
                profile = activeProfile.value,
                hasAttachedImage = hasAttachedImage
            )) {
                is AiResult.Success -> Unit
                is AiResult.LimitReached -> _aiErrorBanner.value = res.message
                is AiResult.Error -> _aiErrorBanner.value = res.message
            }
            _aiLoading.value = false
        }
    }

    fun clearAiChat() {
        val learnerId = activeLearnerId.value ?: return
        viewModelScope.launch { repository.clearAiHistory(learnerId) }
    }

    // Settings & Admin
    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { repository.updateThemeMode(mode) }
    fun setSoundEnabled(enabled: Boolean) = viewModelScope.launch { repository.updateSoundEnabled(enabled) }
    fun setShareProgressWithAi(enabled: Boolean) = viewModelScope.launch { repository.updateShareProgressWithAi(enabled) }
    fun setDailyGoal(xp: Int) {
        val id = activeLearnerId.value ?: return
        viewModelScope.launch { repository.updateDailyGoal(id, xp) }
    }
    fun toggleAdminRole() {
        val id = activeLearnerId.value ?: return
        viewModelScope.launch { repository.toggleAdminRole(id) }
    }
    fun saveAdminConfig(config: AdminConfigEntity) {
        viewModelScope.launch {
            repository.updateAdminConfig(config)
            _uiBannerMessage.value = "Administrator configuration saved."
        }
    }
}
