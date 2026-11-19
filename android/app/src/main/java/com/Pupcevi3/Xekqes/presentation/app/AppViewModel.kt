package com.Pupcevi3.Xekqes.presentation.app

import androidx.lifecycle.ViewModel
import com.Pupcevi3.Xekqes.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _runToken = MutableStateFlow(0)
    val runToken: StateFlow<Int> = _runToken.asStateFlow()

    private val stack = ArrayList<Screen>()

    fun open(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) stack.add(current)
        if (target is Screen.Game) _runToken.value = _runToken.value + 1
        _screen.value = target
    }

    fun replace(target: Screen) {
        if (target is Screen.Game) _runToken.value = _runToken.value + 1
        _screen.value = target
    }

    fun back() {
        if (stack.isEmpty()) {
            _screen.value = Screen.Menu
            return
        }
        _screen.value = stack.removeAt(stack.lastIndex)
    }

    fun home() {
        stack.clear()
        _screen.value = Screen.Menu
    }
}
