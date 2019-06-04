package com.tetrolix.game.screen

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tetrolix.game.MusicAssets
import com.tetrolix.game.SoundAssets
import com.tetrolix.game.TextureAssets
import com.tetrolix.game.load
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context

class LoadingScreen(private val context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val assets = context.inject<AssetManager>()
    private val stage = context.inject<Stage>()

    override fun show() {
        TextureAssets.values().forEach { assets.load(it) }
        MusicAssets.values().forEach { assets.load(it) }
        SoundAssets.values().forEach { assets.load(it) }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        if (assets.update()) {
            game.removeScreen<LoadingScreen>()
            this.dispose()

            game.addScreen(GameScreen(context))
            game.addScreen(HighscoreScreen(context))
            game.addScreen(MenuScreen(context))

            game.setScreen<MenuScreen>()
        }

        stage.act(delta)
        stage.viewport.apply(true)
        stage.draw()
    }
}