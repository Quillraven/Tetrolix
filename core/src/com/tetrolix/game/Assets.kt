package com.tetrolix.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAssets(val filePath: String) {
    Block("graphics/block.png");
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.filePath)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.filePath)

enum class MusicAssets(val filePath: String) {
    Game("music/game_bgd.mp3");
}

fun AssetManager.load(asset: MusicAssets) = load<Music>(asset.filePath)
operator fun AssetManager.get(asset: MusicAssets) = getAsset<Music>(asset.filePath)

enum class SoundAssets(val filePath: String) {
    RotateLeft("sounds/rotate_left.wav"),
    RotateRight("sounds/rotate_right.wav"),
    LineComplete("sounds/line_complete.wav"),
    BlockLock("sounds/block_lock.wav");
}

fun AssetManager.load(asset: SoundAssets) = load<Sound>(asset.filePath)
operator fun AssetManager.get(asset: SoundAssets) = getAsset<Sound>(asset.filePath)