package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.tetrolix.game.*
import ktx.actors.*
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.scene2d.*

class MenuScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val stage = context.inject<Stage>()
    private val audioMgr = context.inject<AudioManager>()

    private val volumeInfo = Label("${Math.round(audioMgr.volume * 100)}", Scene2DSkin.defaultSkin, Labels.BrightBgd()).apply { setAlignment(Align.center) }
    private val btnVolDec = TextButton("-", Scene2DSkin.defaultSkin)
    private val btnVolInc = TextButton("+", Scene2DSkin.defaultSkin)
    // window that is shown when clicking on "Credits" button
    private val creditsWindow = window("") {
        defaults().expand()
        label("Credits", Labels.Huge()) { cell -> cell.row() }.color = Color.BLACK
        label("""
            |SupspaceAudio for sound effects
            |Patrick de Arteaga for music
            |Daniel Nagura for font
            |ZenoGames for UI
            """.trimMargin()) { cell -> cell.row() }.run {
            setAlignment(Align.left)
            color = Color.BLACK
        }
        textButton("Thank you", Buttons.Dark()).onClick { remove() }
        isModal = true
        pack()
    }
    // window that is shown when clicking on "Information" button
    private val informationWindow = window("") {
        label("Information", Labels.Huge()) { cell -> cell.colspan(3).padTop(20f).row() }.color = Color.BLACK
        // move block info
        imageButton(Buttons.Arrow()) { cell -> cell.size(75f).padLeft(10f) }
        imageButton(Buttons.Arrow()) { cell -> cell.size(75f) }.run {
            isTransform = true
            setOrigin(75 * 0.5f, 75 * 0.5f)
            rotateBy(180f)
        }
        label("moves block right and left", Labels.Dark()) { cell -> cell.expand().fill().padLeft(10f).padRight(10f).row() }.run {
            setAlignment(Align.left)
            setWrap(true)
        }
        // drop block info
        imageButton(Buttons.Arrow()) { cell -> cell.size(75f).padLeft(10f) }.run {
            isTransform = true
            setOrigin(75 * 0.5f, 75 * 0.5f)
            rotateBy(270f)
        }
        label("drops block to lowest possible position", Labels.Dark()) { cell -> cell.expand().fill().padLeft(10f).colspan(2).row() }.run {
            setAlignment(Align.left)
            setWrap(true)
        }
        // rotate block info
        imageButton(Buttons.RotateRight()) { cell -> cell.size(75f).padLeft(10f) }
        imageButton(Buttons.RotateLeft()) { cell -> cell.size(75f) }
        label("rotates block clockwise and counter clockwise", Labels.Dark()) { cell -> cell.expand().fill().padLeft(10f).row() }.run {
            setAlignment(Align.left)
            setWrap(true)
        }
        label("""
            |There is a 0.75 seconds delay before a block gets finally locked if it cannot move anymore.
            |A block is moved down periodically. The interval of the movement decreases the higher the level.
            |A "move" or "rotate" command resets the movement interval and avoids that the block moves down.
            |This can be done 20 times per block.
            """.trimMargin(), Labels.Dark()) { cell -> cell.expand().fill().colspan(3).pad(30f, 10f, 30f, 10f).row() }.run {
            setAlignment(Align.left)
            setWrap(true)
        }
        textButton("OK", Buttons.Dark()) { cell -> cell.colspan(3).width(200f).padBottom(20f).row() }.onClick { remove() }
        isModal = true
        pack()
    }

    private fun updateVolume(newValue: Float) {
        audioMgr.volume = newValue
        audioMgr.play(SoundAssets.ButtonSelect)
        volumeInfo.txt = "${Math.round(audioMgr.volume * 100)}"
    }

    override fun show() {
        audioMgr.play(MusicAssets.Menu)

        stage.clear()
        stage.addActor(table {
            defaults().expand().uniform()

            // close game
            textButton("X") { cell -> cell.expand(false, false).top().right().size(75f).row() }.onClick { Gdx.app.exit() }
            // banner
            imageButton(Buttons.Banner()) { cell -> cell.row() }
            // go to starting level selection
            textButton("Start Game") { cell -> cell.width(200f).row() }.onClick { game.setScreen<GameScreen>() }
            // show controls and gameplay information
            textButton("Information") { cell -> cell.width(200f).row() }.onChange {
                stage.addActor(informationWindow)
                informationWindow.centerPosition()
            }
            // music options
            table { tableCell ->
                imageButton(Buttons.Music()).onChangeEvent { changeEvent, actor ->
                    btnVolDec.isDisabled = actor.isChecked
                    btnVolInc.isDisabled = actor.isChecked
                    btnVolDec.touchable = if (actor.isChecked) Touchable.disabled else Touchable.enabled
                    btnVolInc.touchable = if (actor.isChecked) Touchable.disabled else Touchable.enabled
                    if (actor.isChecked) {
                        actor.userObject = audioMgr.volume
                        updateVolume(0f)
                    } else {
                        updateVolume(actor.userObject as Float)
                    }
                }
                actor(btnVolDec).onChange { updateVolume(audioMgr.volume - 0.05f) }
                actor(volumeInfo) { cell -> cell.width(175f).fill() }
                actor(btnVolInc).onChange { updateVolume(audioMgr.volume + 0.05f) }
                tableCell.row()
            }
            // show credits for assets
            textButton("Credits") { cell -> cell.width(200f).row() }.onChange {
                stage.addActor(creditsWindow)
                creditsWindow.centerPosition()
            }
            // copyright :P
            label("by Quillraven 2019") { cell -> cell.expand(false, false).bottom().right().pad(0f, 0f, 10f, 10f) }

            background = skin.getDrawable(Drawables.GutterDark())
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