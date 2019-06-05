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
import com.tetrolix.game.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.LetterboxingViewport
import ktx.freetype.generateFont
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.style.*


// TODO create UI (show highscore, show next block, flash rows that get removed, randomly fill grid when lost, select starting level, save highscore in preference and show it in menu)
class Tetrolix : KtxGame<KtxScreen>() {
    private val context = Context()

    override fun create() {
        context.register {
            bindSingleton<Batch> { SpriteBatch() }
            bindSingleton { AssetManager().apply { registerFreeTypeFontLoaders() } }
            bindSingleton(createSkin())
            bindSingleton<Viewport> { FitViewport(10f, 20f) }
            bindSingleton { Stage(LetterboxingViewport(aspectRatio = 9f / 16f), inject()) }
            bindSingleton<KtxGame<KtxScreen>> { this@Tetrolix }
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
            label("huge") {
                font = hugeFont
            }
            // button styles
            textButton {
                font = defaultFont
                fontColor = Color.BLACK
                downFontColor = Color.WHITE
                up = it["btn"]
                down = it["btn"]
            }
            // image button styles
            imageButton("music") {
                imageUp = it["btn_music_on"]
                imageDown = it["btn_music_on"]
                imageChecked = it["btn_music_off"]
            }
            imageButton("arrow") {
                imageUp = it["btn_arrow"]
                imageDown = it["btn_arrow"]
            }
            imageButton("rotate") {
                imageUp = it["btn_rotate"]
                imageDown = it["btn_rotate"]
            }
        }
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
