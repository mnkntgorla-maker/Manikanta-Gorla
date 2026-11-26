package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learner_profiles")
data class LearnerProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val displayName: String,
    val handle: String,
    val avatarColorIndex: Int = 0,
    val totalXp: Int = 0,
    val streakDays: Int = 0,
    val dailyGoalXp: Int = 50,
    val todayXp: Int = 0,
    val totalMinutesLearned: Int = 0,
    val lastActiveDateKey: String = "",
    val isAdmin: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress", primaryKeys = ["learnerId", "lessonId"])
data class LessonProgressEntity(
    val learnerId: Long,
    val lessonId: String,
    val departmentId: Int,
    val currentStep: Int = 0,
    val isMastered: Boolean = false,
    val bestAccuracyPercent: Int = 0,
    val attemptsCount: Int = 0,
    val lastPracticedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "exercise_attempts")
data class ExerciseAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val learnerId: Long,
    val exerciseId: String,
    val departmentId: Int,
    val topic: String,
    val difficulty: String,
    val exerciseType: String,
    val isCorrect: Boolean,
    val xpEarned: Int,
    val timeSpentSec: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_projects")
data class PortfolioProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val learnerId: Long,
    val title: String,
    val departmentId: Int,
    val softwareUsed: String,
    val description: String,
    val imageUri: String? = null,
    val status: String = "Submitted",
    val evaluationFeedback: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements", primaryKeys = ["learnerId", "code"])
data class AchievementEntity(
    val learnerId: Long,
    val code: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "ai_conversations")
data class AiConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val learnerId: Long,
    val role: String, // "user" or "assistant"
    val mode: String, // "Tutor", "Examiner", "Mentor", "Revision", "Visual"
    val message: String,
    val isPlaceholderResponse: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey val id: Int = 1,
    val dailyAiQuestionLimit: Int = 25,
    val dailyExerciseGenLimit: Int = 15,
    val dailyImageAnalysisLimit: Int = 5,
    val aiTutorEnabled: Boolean = true,
    val aiExaminerEnabled: Boolean = true,
    val aiExerciseGenEnabled: Boolean = true,
    val aiVisualFeedbackEnabled: Boolean = true,
    val selectedProviderModel: String = "Firebase AI Logic (Gemini 3.8 Flash — Inactive)",
    val aiQuestionsUsedToday: Int = 0,
    val aiExercisesGenToday: Int = 0,
    val imageAnalysisUsedToday: Int = 0,
    val usageDateKey: String = ""
)
