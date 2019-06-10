package com.tetrolix.game

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable

private const val VOLUME_KEY = "volume"

class AudioManager(private val assets: AssetManager, private val prefs: Preferences) : Disposable {
    private var currentMusic = assets[MusicAssets.Menu]
    var volume: Float = prefs.getFloat(VOLUME_KEY, 1f)
        set(value) {
            field = MathUtils.clamp(value, 0f, 1f)
            currentMusic.volume = field
        }

    fun play(soundType: SoundAssets) = assets[soundType].play(volume)

    fun play(musicType: MusicAssets) = assets[musicType].run {
        currentMusic.stop()
        currentMusic = this
        isLooping = true
        volume = this@AudioManager.volume
        play()
    }

    override fun dispose() {
        prefs.putFloat(VOLUME_KEY, volume)
        prefs.flush()
    }
}