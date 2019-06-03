package com.tetrolix.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.tetrolix.game.Tetrolix

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            width = 504
            height = 896
        }
        LwjglApplication(Tetrolix(), config)
    }
}
