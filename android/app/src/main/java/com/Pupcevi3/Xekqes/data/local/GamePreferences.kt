package com.Pupcevi3.Xekqes.data.local

import android.content.Context

class GamePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readLong(key: String, fallback: Long): Long = prefs.getLong(key, fallback)

    fun writeLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun readText(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeText(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun contains(key: String): Boolean = prefs.contains(key)

    companion object {
        private const val STORE = "featherline_rush_store"
        const val KEY_BEST_SCORE = "best_score"
        const val KEY_BEST_LANES = "best_lanes"
        const val KEY_LEADERBOARD = "leaderboard_rows"
        const val KEY_REACTION = "reaction_window"
        const val KEY_RECOVERY = "recovery_window"
        const val KEY_HOLD_ASSIST = "hold_assist"
        const val KEY_START_FEATHERS = "starting_feathers"
        const val KEY_REDUCE_MOTION = "reduce_motion"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_DRILL_DAY = "drill_day"
        const val KEY_DRILL_ROWS = "drill_progress_rows"
        const val KEY_FEATHER_CACHE = "feather_cache"
    }
}
