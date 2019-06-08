package com.tetrolix.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils

class AudioManager(private val assets: AssetManager) {
    private var currentMusic = assets[MusicAssets.Menu]

    var volume: Float = 1f
        set(value) {
            field = MathUtils.clamp(value, 0f, 1f)
            currentMusic.volume = field
        }


    fun play(soundType: SoundAssets) = assets[soundType].run {
        val soundID = play()
        setVolume(soundID, volume)
    }

    fun play(musicType: MusicAssets) = assets[musicType].run {
        currentMusic.stop()
        currentMusic = this
        isLooping = true
        volume = this@AudioManager.volume
        play()
    }
}