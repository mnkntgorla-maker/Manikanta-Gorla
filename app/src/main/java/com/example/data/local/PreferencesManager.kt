package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pomana_settings")

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

class PreferencesManager(private val context: Context) {

    private object Keys {
        val ACTIVE_LEARNER_ID = longPreferencesKey("active_learner_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SOUND_FEEDBACK = booleanPreferencesKey("sound_feedback")
        val PRIVACY_SHARE_AI_PROGRESS = booleanPreferencesKey("privacy_share_ai_progress")
    }

    val activeLearnerId: Flow<Long?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.ACTIVE_LEARNER_ID]
        if (id == null || id <= 0L) null else id
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        runCatching { ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    val soundFeedbackEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SOUND_FEEDBACK] ?: true
    }

    val shareProgressWithAi: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.PRIVACY_SHARE_AI_PROGRESS] ?: true
    }

    suspend fun setActiveLearnerId(id: Long?) {
        context.dataStore.edit { prefs ->
            if (id == null) prefs.remove(Keys.ACTIVE_LEARNER_ID)
            else prefs[Keys.ACTIVE_LEARNER_ID] = id
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setSoundFeedback(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.SOUND_FEEDBACK] = enabled }
    }

    suspend fun setShareProgressWithAi(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.PRIVACY_SHARE_AI_PROGRESS] = enabled }
    }
}
