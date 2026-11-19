package com.Pupcevi3.Xekqes.data.repository

import com.Pupcevi3.Xekqes.data.local.GamePreferences
import com.Pupcevi3.Xekqes.domain.model.GameSettings
import com.Pupcevi3.Xekqes.domain.model.ReactionWindow
import com.Pupcevi3.Xekqes.domain.model.RecoveryWindow
import com.Pupcevi3.Xekqes.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: GamePreferences) : SettingsRepository {

    override fun load(): GameSettings {
        val defaults = GameSettings.DEFAULT
        val reaction = ReactionWindow.entries.getOrElse(
            store.readInt(GamePreferences.KEY_REACTION, defaults.reaction.ordinal)
        ) { defaults.reaction }
        val recovery = RecoveryWindow.entries.getOrElse(
            store.readInt(GamePreferences.KEY_RECOVERY, defaults.recovery.ordinal)
        ) { defaults.recovery }
        return GameSettings(
            reaction = reaction,
            recovery = recovery,
            holdAssist = store.readBoolean(GamePreferences.KEY_HOLD_ASSIST, defaults.holdAssist),
            startingFeathers = store.readInt(GamePreferences.KEY_START_FEATHERS, defaults.startingFeathers),
            reduceMotion = store.readBoolean(GamePreferences.KEY_REDUCE_MOTION, defaults.reduceMotion),
            highContrast = store.readBoolean(GamePreferences.KEY_HIGH_CONTRAST, defaults.highContrast)
        )
    }

    override fun save(settings: GameSettings) {
        store.writeInt(GamePreferences.KEY_REACTION, settings.reaction.ordinal)
        store.writeInt(GamePreferences.KEY_RECOVERY, settings.recovery.ordinal)
        store.writeBoolean(GamePreferences.KEY_HOLD_ASSIST, settings.holdAssist)
        store.writeInt(GamePreferences.KEY_START_FEATHERS, settings.startingFeathers)
        store.writeBoolean(GamePreferences.KEY_REDUCE_MOTION, settings.reduceMotion)
        store.writeBoolean(GamePreferences.KEY_HIGH_CONTRAST, settings.highContrast)
    }
}
