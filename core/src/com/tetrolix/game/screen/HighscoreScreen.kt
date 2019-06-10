package com.tetrolix.game.screen

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.utils.Align
import com.tetrolix.game.Buttons
import com.tetrolix.game.Drawables
import com.tetrolix.game.Labels
import com.tetrolix.game.PREF_KEY_HIGHSCORE
import ktx.actors.onClick
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table

class HighscoreScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val stage = context.inject<Stage>()
    private val prefs = context.inject<Preferences>()
    var highscore = 0

    override fun show() {
        val highestScore = prefs.getInteger(PREF_KEY_HIGHSCORE, 0)
        val newHighscore = highscore > highestScore
        if (newHighscore) {
            prefs.putInteger(PREF_KEY_HIGHSCORE, highscore)
            prefs.flush()
        }

        stage.clear()
        stage.addActor(table {
            imageButton(Buttons.Banner()) { cell -> cell.expand().row() }
            label("Highscore:       $highscore", Labels.BrightBgd()) { cell -> cell.expandX().width(475f).padBottom(20f).row() }
            label("Highest Score: $highestScore", Labels.BrightBgd()) { cell -> cell.expandX().width(475f).row() }
            if (newHighscore) {
                label("New highscore!", Labels.Huge()) { cell -> cell.expand().row() } += forever(color(Color.RED) + delay(0.1f) + color(Color.GREEN) + delay(0.1f) + color(Color.BLUE) + delay(0.1f) + color(Color.GREEN) + delay(0.1f))
            } else {
                label("Better luck next time!", Labels.Huge()) { cell -> cell.expand().fill().pad(0f, 10f, 0f, 10f).row() }.run {
                    setWrap(true)
                    setAlignment(Align.center)
                }
            }
            label("Touch anywhere to return to menu", Labels.Huge()) { cell -> cell.expand().fill().padBottom(70f).row() }.run {
                setWrap(true)
                setAlignment(Align.center)
                color = Color.BLACK
                this += forever(alpha(0f) + fadeIn(1f) + delay(0.25f) + fadeOut(1f))
            }

            onClick { game.setScreen<MenuScreen>() }

            background = skin.getDrawable(Drawables.Gutter())
            setFillParent(true)
        })
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