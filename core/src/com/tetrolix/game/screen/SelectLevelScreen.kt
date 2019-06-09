package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.tetrolix.game.Buttons
import com.tetrolix.game.Drawables
import com.tetrolix.game.Labels
import ktx.actors.onClick
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.textButton

class SelectLevelScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val stage = context.inject<Stage>()

    override fun show() {
        stage.clear()
        stage.addActor(table {
            label("Level Selection", Labels.Huge()) { cell -> cell.colspan(2).padTop(30f).row() }
            label("""
                |The higher the level the more points you get for clearing rows but the blocks are falling faster.
                |Level 15 is for super experts and the blocks become transparent.
                |Whenever you clear 10 rows of a level the level will increase.
                """.trimMargin(), Labels.Dark()) { cell -> cell.colspan(2).left().expand().fill().padLeft(20f).padRight(20f).row() }.run {
                setWrap(true)
                setAlignment(Align.left)
            }
            for (i in 1..8) {
                textButton("Level $i", Buttons.Dark()) { cell ->
                    cell.width(200f).pad(0f, 20f, 20f, 20f)
                    if (i >= 7) cell.padBottom(50f)
                    if (i % 2 == 0) cell.row()
                }.onClick {
                    game.getScreen<GameScreen>().currentLevel = i
                    game.setScreen<GameScreen>()
                }
            }

            background = skin.getDrawable(Drawables.Gutter())
            setFillParent(true)
        })
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) show()
        stage.act(delta)
        stage.viewport.apply(true)
        stage.draw()
    }
}