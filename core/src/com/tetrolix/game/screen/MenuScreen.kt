package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.tetrolix.game.*
import ktx.actors.onClick
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.imageButton
import ktx.scene2d.label
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
            defaults().expand().uniform()

            // close game
            textButton("X") { cell -> cell.expand(false, false).top().right().row() }.onClick { Gdx.app.exit() }
            // go to starting level selection
            textButton("Start Game") { cell -> cell.width(200f).row() }.onClick { game.setScreen<GameScreen>() }
            // show controls and gameplay information
            textButton("Information") { cell -> cell.width(200f).row() }
            // music options
            table { tableCell ->
                imageButton(Buttons.music())
                textButton("-")
                label("100", Labels.bright()) { cell -> cell.width(175f).fill() }.setAlignment(Align.center)
                textButton("+")
                tableCell.row()
            }
            // show credits for assets
            textButton("Credits") { cell -> cell.width(200f).row() }
            // copyright :P
            label("by Quillraven 2019") { cell -> cell.expand(false, false).bottom().right().pad(0f, 0f, 10f, 10f) }

            background = skin.getDrawable(Drawables.gutter_dark())
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