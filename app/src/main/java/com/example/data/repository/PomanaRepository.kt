package com.example.data.repository

import com.example.data.ai.AiContextSnapshot
import com.example.data.ai.AiRepository
import com.example.data.ai.AiResult
import com.example.data.ai.PomanaAiMode
import com.example.data.curriculum.CurriculumCatalog
import com.example.data.local.*
import com.example.data.model.DifficultyTier
import com.example.data.model.ExerciseItem
import com.example.data.model.ExerciseType
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TopicStat(
    val topic: String,
    val attempts: Int,
    val correct: Int
) {
    val accuracyPercent: Int
        get() = if (attempts == 0) 0 else ((correct * 100f) / attempts).toInt()
}

data class LearnerAnalytics(
    val completedLessons: Int,
    val totalLessons: Int,
    val totalAttempts: Int,
    val accuracyPercent: Int,
    val strongTopics: List<TopicStat>,
    val weakTopics: List<TopicStat>,
    val recommendedRevisionTopic: String?,
    val rankTitle: String,
    val nextRankTitle: String,
    val xpToNextRank: Int
)

class PomanaRepository(
    private val dao: PomanaDao,
    private val prefs: PreferencesManager,
    val aiRepository: AiRepository
) {
    val activeLearnerId: Flow<Long?> = prefs.activeLearnerId
    val themeMode: Flow<ThemeMode> = prefs.themeMode
    val soundEnabled: Flow<Boolean> = prefs.soundFeedbackEnabled
    val shareProgressWithAi: Flow<Boolean> = prefs.shareProgressWithAi
    val allProfiles: Flow<List<LearnerProfileEntity>> = dao.observeAllProfiles()
    val adminConfig: Flow<AdminConfigEntity> = dao.observeAdminConfig().map { it ?: AdminConfigEntity() }

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    suspend fun ensureAdminConfigSeeded() {
        val current = dao.getAdminConfigOnce()
        if (current == null) {
            dao.upsertAdminConfig(AdminConfigEntity(usageDateKey = todayKey()))
        } else if (current.usageDateKey != todayKey()) {
            dao.upsertAdminConfig(
                current.copy(
                    aiQuestionsUsedToday = 0,
                    aiExercisesGenToday = 0,
                    imageAnalysisUsedToday = 0,
                    usageDateKey = todayKey()
                )
            )
        }
    }

    suspend fun createAndSignInLearner(
        displayName: String,
        handle: String,
        avatarColorIdx: Int,
        isAdmin: Boolean = false
    ): Result<LearnerProfileEntity> {
        val cleanName = displayName.trim()
        val cleanHandle = handle.trim().lowercase(Locale.US).replace(" ", "_")
        if (cleanName.length < 2) return Result.failure(IllegalArgumentException("Name must be at least 2 characters."))
        if (cleanHandle.length < 2) return Result.failure(IllegalArgumentException("Handle must be at least 2 characters."))

        val existing = dao.findProfileByHandle(cleanHandle)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Handle @$cleanHandle already exists. Choose Sign In instead!"))
        }

        ensureAdminConfigSeeded()
        val profile = LearnerProfileEntity(
            displayName = cleanName,
            handle = cleanHandle,
            avatarColorIndex = avatarColorIdx,
            isAdmin = isAdmin,
            lastActiveDateKey = ""
        )
        val newId = dao.insertProfile(profile)
        seedDefaultAchievements(newId)
        prefs.setActiveLearnerId(newId)
        return Result.success(profile.copy(id = newId))
    }

    suspend fun signInByHandle(handle: String): Result<LearnerProfileEntity> {
        val clean = handle.trim().lowercase(Locale.US).removePrefix("@")
        val found = dao.findProfileByHandle(clean)
            ?: return Result.failure(IllegalArgumentException("No learner found with handle @$clean."))
        ensureAdminConfigSeeded()
        seedDefaultAchievements(found.id)
        prefs.setActiveLearnerId(found.id)
        return Result.success(found)
    }

    suspend fun switchLearner(learnerId: Long) {
        ensureAdminConfigSeeded()
        seedDefaultAchievements(learnerId)
        prefs.setActiveLearnerId(learnerId)
    }

    suspend fun signOut() {
        prefs.setActiveLearnerId(null)
    }

    private suspend fun seedDefaultAchievements(learnerId: Long) {
        val defs = listOf(
            AchievementEntity(learnerId, "FIRST_STEP", "First Vertex", "Complete your very first 3D exercise", 20),
            AchievementEntity(learnerId, "MASTER_1", "Lesson Master", "Pass a lesson mastery check with 75%+", 40),
            AchievementEntity(learnerId, "STREAK_3", "3-Day Momentum", "Maintain a 3-day learning streak", 60),
            AchievementEntity(learnerId, "XP_100", "Century Crafter", "Earn 100 total XP through real practice", 35),
            AchievementEntity(learnerId, "XP_500", "Poly Prodigy", "Earn 500 total XP across departments", 100),
            AchievementEntity(learnerId, "PORTFOLIO_1", "First Studio Piece", "Upload your first project to Portfolio", 50),
            AchievementEntity(learnerId, "DEPT_3", "Multi-Disciplinary", "Practice across 3+ different departments", 75),
            AchievementEntity(learnerId, "EXPERT_WIN", "Expert Challenger", "Answer an Advanced or Expert question correctly", 80)
        )
        dao.insertAchievements(defs)
    }

    fun observeProfile(learnerId: Long): Flow<LearnerProfileEntity?> = dao.observeProfile(learnerId)
    fun observeLessonProgress(learnerId: Long): Flow<List<LessonProgressEntity>> = dao.observeLessonProgress(learnerId)
    fun observeAttempts(learnerId: Long): Flow<List<ExerciseAttemptEntity>> = dao.observeExerciseAttempts(learnerId)
    fun observePortfolio(learnerId: Long): Flow<List<PortfolioProjectEntity>> = dao.observePortfolioProjects(learnerId)
    fun observeAchievements(learnerId: Long): Flow<List<AchievementEntity>> = dao.observeAchievements(learnerId)
    fun observeAiMessages(learnerId: Long): Flow<List<AiConversationEntity>> = dao.observeAiMessages(learnerId)

    fun computeAnalytics(
        profile: LearnerProfileEntity?,
        progressList: List<LessonProgressEntity>,
        attempts: List<ExerciseAttemptEntity>
    ): LearnerAnalytics {
        val totalLessons = CurriculumCatalog.allDepartments.sumOf { it.lessons.size }
        val completedLessons = progressList.count { it.isMastered }
        val totalAttempts = attempts.size
        val accuracy = if (totalAttempts == 0) 0 else ((attempts.count { it.isCorrect } * 100f) / totalAttempts).toInt()

        val topicStats = attempts.groupBy { it.topic }.map { (topic, list) ->
            TopicStat(topic = topic, attempts = list.size, correct = list.count { it.isCorrect })
        }
        val strong = topicStats.filter { it.attempts >= 1 && it.accuracyPercent >= 75 }
            .sortedByDescending { it.accuracyPercent }
        val weak = topicStats.filter { it.attempts >= 1 && it.accuracyPercent < 75 }
            .sortedBy { it.accuracyPercent }

        val xp = profile?.totalXp ?: 0
        val (rank, nextRank, targetXp) = when {
            xp < 100 -> Triple("Voxel Novice", "Mesh Apprentice", 100)
            xp < 300 -> Triple("Mesh Apprentice", "Shader Crafter", 300)
            xp < 700 -> Triple("Shader Crafter", "Rigging Architect", 700)
            xp < 1500 -> Triple("Rigging Architect", "Keyframe Director", 1500)
            xp < 3000 -> Triple("Keyframe Director", "Studio Master", 3000)
            else -> Triple("Studio Master", "Grand Animator", xp)
        }

        return LearnerAnalytics(
            completedLessons = completedLessons,
            totalLessons = totalLessons,
            totalAttempts = totalAttempts,
            accuracyPercent = accuracy,
            strongTopics = strong,
            weakTopics = weak,
            recommendedRevisionTopic = weak.firstOrNull()?.topic,
            rankTitle = rank,
            nextRankTitle = nextRank,
            xpToNextRank = (targetXp - xp).coerceAtLeast(0)
        )
    }

    suspend fun recordExerciseAttempt(
        learnerId: Long,
        exercise: ExerciseItem,
        isCorrect: Boolean,
        timeSpentSec: Int
    ) {
        val xp = if (isCorrect) exercise.xpReward else 3
        dao.insertAttempt(
            ExerciseAttemptEntity(
                learnerId = learnerId,
                exerciseId = exercise.id,
                departmentId = exercise.departmentId,
                topic = exercise.topic,
                difficulty = exercise.difficulty.label,
                exerciseType = exercise.type.label,
                isCorrect = isCorrect,
                xpEarned = xp,
                timeSpentSec = timeSpentSec.coerceAtLeast(1)
            )
        )
        awardXpAndStreak(learnerId, xp, minutesSpent = 1)
        evaluateAchievements(learnerId)
    }

    suspend fun updateLessonStep(learnerId: Long, lessonId: String, departmentId: Int, step: Int) {
        val existing = dao.getLessonProgressOnce(learnerId, lessonId)
        val updated = (existing ?: LessonProgressEntity(learnerId, lessonId, departmentId)).copy(
            currentStep = maxOf(existing?.currentStep ?: 0, step),
            lastPracticedAt = System.currentTimeMillis()
        )
        dao.upsertLessonProgress(updated)
    }

    suspend fun submitLessonMasteryResult(
        learnerId: Long,
        lessonId: String,
        departmentId: Int,
        accuracyPercent: Int,
        lessonXpReward: Int
    ): Boolean {
        val passed = accuracyPercent >= 75
        val existing = dao.getLessonProgressOnce(learnerId, lessonId)
        val firstMastery = passed && (existing?.isMastered != true)
        val updated = (existing ?: LessonProgressEntity(learnerId, lessonId, departmentId)).copy(
            currentStep = if (passed) 9 else existing?.currentStep ?: 6,
            isMastered = (existing?.isMastered == true) || passed,
            bestAccuracyPercent = maxOf(existing?.bestAccuracyPercent ?: 0, accuracyPercent),
            attemptsCount = (existing?.attemptsCount ?: 0) + 1,
            lastPracticedAt = System.currentTimeMillis()
        )
        dao.upsertLessonProgress(updated)

        if (firstMastery) {
            awardXpAndStreak(learnerId, lessonXpReward, minutesSpent = 5)
        } else if (passed) {
            awardXpAndStreak(learnerId, 10, minutesSpent = 2)
        }
        evaluateAchievements(learnerId)
        return passed
    }

    private suspend fun awardXpAndStreak(learnerId: Long, xp: Int, minutesSpent: Int) {
        val profile = dao.getProfileOnce(learnerId) ?: return
        val today = todayKey()
        val isNewDay = profile.lastActiveDateKey != today
        val newStreak = when {
            profile.lastActiveDateKey.isEmpty() -> 1
            isNewDay -> profile.streakDays + 1
            else -> profile.streakDays.coerceAtLeast(1)
        }
        val newTodayXp = if (isNewDay) xp else profile.todayXp + xp
        dao.updateProfile(
            profile.copy(
                totalXp = profile.totalXp + xp,
                todayXp = newTodayXp,
                streakDays = newStreak,
                totalMinutesLearned = profile.totalMinutesLearned + minutesSpent,
                lastActiveDateKey = today
            )
        )
    }

    suspend fun savePortfolioProject(
        learnerId: Long,
        title: String,
        departmentId: Int,
        softwareUsed: String,
        description: String,
        imageUri: String?
    ): AiResult<PortfolioProjectEntity> {
        val reviewResult = aiRepository.evaluateProjectSubmission(
            title = title,
            departmentId = departmentId,
            softwareUsed = softwareUsed,
            notes = description,
            hasImage = !imageUri.isNullOrBlank()
        )
        val feedbackText = when (reviewResult) {
            is AiResult.Success -> reviewResult.data
            is AiResult.LimitReached -> reviewResult.message
            is AiResult.Error -> reviewResult.message
        }
        val entity = PortfolioProjectEntity(
            learnerId = learnerId,
            title = title.trim(),
            departmentId = departmentId,
            softwareUsed = softwareUsed.trim(),
            description = description.trim(),
            imageUri = imageUri,
            status = "Submitted",
            evaluationFeedback = feedbackText
        )
        val id = dao.insertPortfolioProject(entity)
        awardXpAndStreak(learnerId, 30, minutesSpent = 5)
        evaluateAchievements(learnerId)
        return AiResult.Success(
            data = entity.copy(id = id),
            isPlaceholderPreview = aiRepository.isPlaceholderMode,
            notice = aiRepository.setupNotice
        )
    }

    suspend fun deletePortfolioProject(projectId: Long) {
        dao.deletePortfolioProject(projectId)
    }

    suspend fun sendAiChatMessage(
        learnerId: Long,
        prompt: String,
        mode: PomanaAiMode,
        analytics: LearnerAnalytics,
        profile: LearnerProfileEntity?,
        hasAttachedImage: Boolean
    ): AiResult<String> {
        ensureAdminConfigSeeded()
        val config = dao.getAdminConfigOnce() ?: AdminConfigEntity(usageDateKey = todayKey())

        if (mode == PomanaAiMode.TUTOR && !config.aiTutorEnabled) {
            return AiResult.LimitReached("AI Tutor is currently disabled by the Administrator.")
        }
        if (mode == PomanaAiMode.EXAMINER && !config.aiExaminerEnabled) {
            return AiResult.LimitReached("AI Examiner is currently disabled by the Administrator.")
        }
        if (mode == PomanaAiMode.VISUAL && !config.aiVisualFeedbackEnabled) {
            return AiResult.LimitReached("AI Visual Feedback is currently disabled by the Administrator.")
        }
        if (hasAttachedImage && config.imageAnalysisUsedToday >= config.dailyImageAnalysisLimit) {
            return AiResult.LimitReached("Daily image analysis limit (${config.dailyImageAnalysisLimit}) reached. Adjust limits in Admin Console.")
        }
        if (config.aiQuestionsUsedToday >= config.dailyAiQuestionLimit) {
            return AiResult.LimitReached("Daily AI question limit (${config.dailyAiQuestionLimit}) reached. Adjust limits in Admin Console.")
        }

        val shareProgress = prefs.shareProgressWithAi.first()
        dao.insertAiMessage(
            AiConversationEntity(
                learnerId = learnerId,
                role = "user",
                mode = mode.label,
                message = if (hasAttachedImage) "[Image Attached] $prompt" else prompt,
                isPlaceholderResponse = false
            )
        )

        val ctx = AiContextSnapshot(
            learnerName = profile?.displayName ?: "Learner",
            rankTitle = analytics.rankTitle,
            accuracyPercent = analytics.accuracyPercent,
            weakTopics = analytics.weakTopics.map { it.topic },
            strongTopics = analytics.strongTopics.map { it.topic },
            shareProgressEnabled = shareProgress
        )

        val result = aiRepository.askAssistant(prompt, mode, ctx, hasAttachedImage)
        if (result is AiResult.Success) {
            dao.insertAiMessage(
                AiConversationEntity(
                    learnerId = learnerId,
                    role = "assistant",
                    mode = mode.label,
                    message = result.data,
                    isPlaceholderResponse = result.isPlaceholderPreview
                )
            )
            dao.upsertAdminConfig(
                config.copy(
                    aiQuestionsUsedToday = config.aiQuestionsUsedToday + 1,
                    imageAnalysisUsedToday = if (hasAttachedImage) config.imageAnalysisUsedToday + 1 else config.imageAnalysisUsedToday,
                    usageDateKey = todayKey()
                )
            )
        }
        return result
    }

    suspend fun generateAiPracticeItem(
        departmentId: Int,
        difficulty: DifficultyTier,
        type: ExerciseType
    ): AiResult<ExerciseItem> {
        ensureAdminConfigSeeded()
        val config = dao.getAdminConfigOnce() ?: AdminConfigEntity(usageDateKey = todayKey())
        if (!config.aiExerciseGenEnabled) {
            return AiResult.LimitReached("AI Exercise Generator is disabled by the Administrator.")
        }
        if (config.aiExercisesGenToday >= config.dailyExerciseGenLimit) {
            return AiResult.LimitReached("Daily AI exercise generation limit (${config.dailyExerciseGenLimit}) reached.")
        }
        val res = aiRepository.generateCustomExercise(departmentId, difficulty, type)
        if (res is AiResult.Success) {
            dao.upsertAdminConfig(
                config.copy(
                    aiExercisesGenToday = config.aiExercisesGenToday + 1,
                    usageDateKey = todayKey()
                )
            )
        }
        return res
    }

    suspend fun clearAiHistory(learnerId: Long) = dao.clearAiHistory(learnerId)

    suspend fun updateThemeMode(mode: ThemeMode) = prefs.setThemeMode(mode)
    suspend fun updateSoundEnabled(enabled: Boolean) = prefs.setSoundFeedback(enabled)
    suspend fun updateShareProgressWithAi(enabled: Boolean) = prefs.setShareProgressWithAi(enabled)

    suspend fun updateDailyGoal(learnerId: Long, goalXp: Int) {
        val profile = dao.getProfileOnce(learnerId) ?: return
        dao.updateProfile(profile.copy(dailyGoalXp = goalXp))
    }

    suspend fun toggleAdminRole(learnerId: Long) {
        val profile = dao.getProfileOnce(learnerId) ?: return
        dao.updateProfile(profile.copy(isAdmin = !profile.isAdmin))
    }

    suspend fun updateAdminConfig(config: AdminConfigEntity) {
        dao.upsertAdminConfig(config.copy(id = 1))
    }

    private suspend fun evaluateAchievements(learnerId: Long) {
        val profile = dao.getProfileOnce(learnerId) ?: return
        val progress = dao.observeLessonProgress(learnerId).first()
        val attempts = dao.observeExerciseAttempts(learnerId).first()
        val portfolio = dao.observePortfolioProjects(learnerId).first()
        val achievements = dao.getAchievementsOnce(learnerId)

        val deptsPracticed = attempts.map { it.departmentId }.distinct().size
        val hasExpertCorrect = attempts.any {
            it.isCorrect && (it.difficulty == DifficultyTier.ADVANCED.label || it.difficulty == DifficultyTier.EXPERT.label)
        }

        val conditions = mapOf(
            "FIRST_STEP" to attempts.isNotEmpty(),
            "MASTER_1" to progress.any { it.isMastered },
            "STREAK_3" to (profile.streakDays >= 3),
            "XP_100" to (profile.totalXp >= 100),
            "XP_500" to (profile.totalXp >= 500),
            "PORTFOLIO_1" to portfolio.isNotEmpty(),
            "DEPT_3" to (deptsPracticed >= 3),
            "EXPERT_WIN" to hasExpertCorrect
        )

        var bonusXp = 0
        achievements.forEach { ach ->
            if (!ach.isUnlocked && conditions[ach.code] == true) {
                dao.updateAchievement(ach.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
                bonusXp += ach.xpReward
            }
        }
        if (bonusXp > 0) {
            val refreshed = dao.getProfileOnce(learnerId) ?: return
            dao.updateProfile(refreshed.copy(totalXp = refreshed.totalXp + bonusXp))
        }
    }
}
