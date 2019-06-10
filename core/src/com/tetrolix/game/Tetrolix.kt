package com.tetrolix.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.Drawables.*
import com.tetrolix.game.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.freetype.generateFont
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.style.*

const val UI_BTN_SIZE = 75f

// TODO create UI (flash rows that get removed, randomly fill grid when lost, save highscore and volume in preference and show it in menu)
class Tetrolix : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        context.register {
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(AssetManager().apply { registerFreeTypeFontLoaders() })
            bindSingleton(createSkin())
            bindSingleton<Viewport>(FitViewport(10f, 20f))
            bindSingleton(Stage(FitViewport(576f, 1024f), inject()))
            bindSingleton<KtxGame<KtxScreen>>(this@Tetrolix)
        }

        // set skin for UI and set stage as input processor for button events, etc.
        Scene2DSkin.defaultSkin = context.inject()
        Gdx.input.inputProcessor = context.inject<Stage>()

        // start with the loading screen to load assets of the game
        addScreen(LoadingScreen(context))
        setScreen<LoadingScreen>()
        super.create()
    }

    private fun createSkin(): Skin {
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("ui/8-bit.ttf"))
        val defaultFont = fontGenerator.generateFont { size = 32 }
        val hugeFont = fontGenerator.generateFont { size = 64 }
        fontGenerator.dispose()

        return skin(TextureAtlas(Gdx.files.internal("ui/UI.atlas"))) {
            // label styles
            label {
                font = defaultFont
            }
            label(Labels.Dark()) {
                font = defaultFont
                fontColor = Color.BLACK
            }
            label(Labels.Huge()) {
                font = hugeFont
            }
            label(Labels.BrightBgd()) {
                font = defaultFont
                fontColor = Color.BLACK
                background = it[Btn]
            }
            // button styles
            textButton {
                font = defaultFont
                fontColor = Color.BLACK
                downFontColor = Color.WHITE
                up = it[Btn]
                down = it[Btn]
                disabled = it[BtnDark]
            }
            textButton(Buttons.Dark()) {
                font = defaultFont
                fontColor = Color.BLACK
                downFontColor = Color.WHITE
                up = it[BtnDark]
                down = it[BtnDark]
            }
            // image button styles
            imageButton(Buttons.Banner()) {
                imageUp = it[Banner]
                imageDown = it[Banner]
            }
            imageButton(Buttons.Music()) {
                imageUp = it[BtnMusicOn]
                imageDown = it[BtnMusicOn]
                imageChecked = it[BtnMusicOff]
            }
            imageButton(Buttons.Arrow()) {
                imageUp = it[BtnArrow]
                imageDown = it[BtnArrowPressed]
            }
            imageButton(Buttons.RotateLeft()) {
                imageUp = it[BtnRotateLeft]
                imageDown = it[BtnRotateLeftPressed]
            }
            imageButton(Buttons.RotateRight()) {
                imageUp = it[BtnRotateRight]
                imageDown = it[BtnRotateRightPressed]
            }
            // window styles
            window {
                titleFont = hugeFont
                background = it[Gutter]
            }
        }
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
