package com.tetrolix.game.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.tetrolix.game.Tetrolix


fun main() {
    val config = Lwjgl3ApplicationConfiguration().apply {
        setWindowSizeLimits(504, 896, -1, -1)
        setTitle("Tetrolix")
    }
    Lwjgl3Application(Tetrolix(), config)
}
