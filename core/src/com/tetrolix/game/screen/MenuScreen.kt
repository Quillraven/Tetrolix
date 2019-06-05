package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.tetrolix.game.MusicAssets
import com.tetrolix.game.get
import ktx.actors.onClick
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.imageButton
import ktx.scene2d.table
import ktx.scene2d.textButton

class MenuScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val stage = context.inject<Stage>()
    private var music = context.inject<AssetManager>()[MusicAssets.Menu].apply { isLooping = true }

    override fun show() {
        music.play()
        stage.clear()

        stage.addActor(table {
            textButton("X") { cell -> cell.top().right().expand().row() }.onClick { Gdx.app.exit() }
            textButton("Start Game") { cell -> cell.expand().row() }.onClick { game.setScreen<GameScreen>() }
            textButton("Credits") { cell -> cell.expand().row() }
            imageButton("music") { cell -> cell.expand().padBottom(100f).row() }
            setFillParent(true)
        })
    }

    override fun hide() {
        music.stop()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.viewport.apply(true)
        stage.draw()
    }
}