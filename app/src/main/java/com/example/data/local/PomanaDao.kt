package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PomanaDao {

    // Profiles
    @Query("SELECT * FROM learner_profiles ORDER BY totalXp DESC, createdAt ASC")
    fun observeAllProfiles(): Flow<List<LearnerProfileEntity>>

    @Query("SELECT * FROM learner_profiles WHERE id = :learnerId")
    fun observeProfile(learnerId: Long): Flow<LearnerProfileEntity?>

    @Query("SELECT * FROM learner_profiles WHERE id = :learnerId")
    suspend fun getProfileOnce(learnerId: Long): LearnerProfileEntity?

    @Query("SELECT * FROM learner_profiles WHERE LOWER(handle) = LOWER(:handle) LIMIT 1")
    suspend fun findProfileByHandle(handle: String): LearnerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: LearnerProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: LearnerProfileEntity)

    // Lesson Progress
    @Query("SELECT * FROM lesson_progress WHERE learnerId = :learnerId")
    fun observeLessonProgress(learnerId: Long): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE learnerId = :learnerId AND lessonId = :lessonId LIMIT 1")
    suspend fun getLessonProgressOnce(learnerId: Long, lessonId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLessonProgress(progress: LessonProgressEntity)

    // Exercise Attempts
    @Query("SELECT * FROM exercise_attempts WHERE learnerId = :learnerId ORDER BY timestamp DESC LIMIT 250")
    fun observeExerciseAttempts(learnerId: Long): Flow<List<ExerciseAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExerciseAttemptEntity)

    // Portfolio Projects
    @Query("SELECT * FROM portfolio_projects WHERE learnerId = :learnerId ORDER BY createdAt DESC")
    fun observePortfolioProjects(learnerId: Long): Flow<List<PortfolioProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioProject(project: PortfolioProjectEntity): Long

    @Update
    suspend fun updatePortfolioProject(project: PortfolioProjectEntity)

    @Query("DELETE FROM portfolio_projects WHERE id = :projectId")
    suspend fun deletePortfolioProject(projectId: Long)

    // Achievements
    @Query("SELECT * FROM achievements WHERE learnerId = :learnerId ORDER BY isUnlocked DESC, xpReward ASC")
    fun observeAchievements(learnerId: Long): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(items: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(item: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE learnerId = :learnerId")
    suspend fun getAchievementsOnce(learnerId: Long): List<AchievementEntity>

    // AI Conversation History
    @Query("SELECT * FROM ai_conversations WHERE learnerId = :learnerId ORDER BY timestamp ASC LIMIT 120")
    fun observeAiMessages(learnerId: Long): Flow<List<AiConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(message: AiConversationEntity)

    @Query("DELETE FROM ai_conversations WHERE learnerId = :learnerId")
    suspend fun clearAiHistory(learnerId: Long)

    // Admin Config
    @Query("SELECT * FROM admin_config WHERE id = 1")
    fun observeAdminConfig(): Flow<AdminConfigEntity?>

    @Query("SELECT * FROM admin_config WHERE id = 1")
    suspend fun getAdminConfigOnce(): AdminConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAdminConfig(config: AdminConfigEntity)
}
