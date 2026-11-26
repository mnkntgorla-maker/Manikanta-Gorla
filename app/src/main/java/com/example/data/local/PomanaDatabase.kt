package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        LearnerProfileEntity::class,
        LessonProgressEntity::class,
        ExerciseAttemptEntity::class,
        PortfolioProjectEntity::class,
        AchievementEntity::class,
        AiConversationEntity::class,
        AdminConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PomanaDatabase : RoomDatabase() {
    abstract fun pomanaDao(): PomanaDao

    companion object {
        @Volatile
        private var INSTANCE: PomanaDatabase? = null

        fun getInstance(context: Context): PomanaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PomanaDatabase::class.java,
                    "pomana_club_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
