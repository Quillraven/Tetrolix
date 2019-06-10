package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.label
import ktx.scene2d.table

class HighscoreScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val stage = context.inject<Stage>()

    override fun show() {
        stage.clear()

        stage.addActor(table {
            defaults().expand()

            label("Highscore: 0") { cell -> cell.row() }
            label("Highest Score: 0") { cell -> cell.row() }

            setFillParent(true)
        })
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isTouched) {
            game.setScreen<MenuScreen>()
        }

        stage.act(delta)
        stage.viewport.apply(true)
        stage.draw()
    }
}