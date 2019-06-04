package com.tetrolix.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.LetterboxingViewport
import ktx.assets.getValue
import ktx.assets.loadOnDemand
import ktx.freetype.generateFont
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin


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
            bindSingleton { AssetManager().apply { registerFreeTypeFontLoaders() } }
            bindSingleton(createSkin(context.inject()))
            bindSingleton<Viewport> { FitViewport(10f, 20f) }
            bindSingleton { Stage(LetterboxingViewport(aspectRatio = 16f / 9f), inject()) }
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

    private fun createSkin(assets: AssetManager): Skin {
        // create fonts that are referenced in skin's json file
        // and store them in a map to pass them as resource dependencies for the skin
        val skinResources = ObjectMap<String, Any>()
        val fontGenerator by assets.loadOnDemand<FreeTypeFontGenerator>("ui/8-bit.ttf")
        skinResources.put("font_default", fontGenerator.generateFont { size = 32 })
        skinResources.put("font_huge", fontGenerator.generateFont { size = 64 })
        // unload the generator as it is no longer needed
        assets.unload("ui/8-bit.ttf")
        return assets.loadOnDemand<Skin>("ui/ui.json", SkinLoader.SkinParameter(skinResources)).asset
    }

    override fun dispose() {
        // remove bitmap fonts from skin because they are loaded via assetmanager and he will take
        // care of the disposal. Otherwise they get disposed twice and we get an "Pixmap already disposed" exception
        val skin = context.inject<Skin>()
        for (fontKey in skin.getAll(BitmapFont::class.java).keys()) {
            skin.remove(fontKey, BitmapFont::class.java)
        }
        context.dispose()
        super.dispose()
    }
}
