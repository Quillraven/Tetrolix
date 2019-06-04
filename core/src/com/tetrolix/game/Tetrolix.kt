package com.tetrolix.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.LetterboxingViewport
import ktx.inject.Context

/*TODO
    1) create highscore screen after game over
    2) create menu screen
    3) create UI (show highscore, show next block, flash rows that get removed, randomly fill grid when lost)
 */
class Tetrolix : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        context.register {
            bindSingleton<Batch> { SpriteBatch() }
            bindSingleton { AssetManager() }
            bindSingleton<Viewport> { FitViewport(10f, 20f) }
            bindSingleton { Stage(LetterboxingViewport(aspectRatio = 16f / 9f), inject()) }
            bindSingleton<KtxGame<KtxScreen>> { this@Tetrolix }
        }

        addScreen(LoadingScreen(context))
        setScreen<LoadingScreen>()
        super.create()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
