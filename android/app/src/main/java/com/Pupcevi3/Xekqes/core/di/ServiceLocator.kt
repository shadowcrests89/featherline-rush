package com.Pupcevi3.Xekqes.core.di

import android.content.Context
import com.Pupcevi3.Xekqes.data.local.GamePreferences
import com.Pupcevi3.Xekqes.data.repository.DrillRepositoryImpl
import com.Pupcevi3.Xekqes.data.repository.ScoreRepositoryImpl
import com.Pupcevi3.Xekqes.data.repository.SettingsRepositoryImpl
import com.Pupcevi3.Xekqes.domain.repository.DrillRepository
import com.Pupcevi3.Xekqes.domain.repository.ScoreRepository
import com.Pupcevi3.Xekqes.domain.repository.SettingsRepository
import com.Pupcevi3.Xekqes.domain.usecase.GenerateRouteUseCase
import com.Pupcevi3.Xekqes.domain.usecase.SubmitRunUseCase

object ServiceLocator {

    private var viewModelFactory: AppViewModelFactory? = null

    fun start(context: Context) {
        if (viewModelFactory != null) return
        val store = GamePreferences(context.applicationContext)
        val scores: ScoreRepository = ScoreRepositoryImpl(store)
        val settings: SettingsRepository = SettingsRepositoryImpl(store)
        val drills: DrillRepository = DrillRepositoryImpl(store)
        viewModelFactory = AppViewModelFactory(
            scoreRepository = scores,
            settingsRepository = settings,
            drillRepository = drills,
            generateRoute = GenerateRouteUseCase(),
            submitRun = SubmitRunUseCase(scores, drills)
        )
    }

    val factory: AppViewModelFactory
        get() = requireNotNull(viewModelFactory)
}
