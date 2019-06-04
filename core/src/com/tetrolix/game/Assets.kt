package com.tetrolix.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load

enum class TextureAssets(val filePath: String) {
    Block("graphics/block.png")
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.filePath)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.filePath)

enum class MusicAssets(val filePath: String) {
    Game("music/game.ogg"),
    GameFinale("music/game_finale.ogg"),
    Highscore("music/highscore.ogg"),
    Menu("music/menu.ogg")
}

fun AssetManager.load(asset: MusicAssets) = load<Music>(asset.filePath)
operator fun AssetManager.get(asset: MusicAssets) = getAsset<Music>(asset.filePath)

enum class SoundAssets(val filePath: String) {
    RotateLeft("sounds/block_rotate_left.wav"),
    RotateRight("sounds/block_rotate_right.wav"),
    LineComplete("sounds/line_complete.wav"),
    BlockLock("sounds/block_lock.wav"),
    BlockMove("sounds/block_move.wav"),
    ButtonSelect("sounds/btn_select.wav"),
    GameOver("sounds/game_over.wav"),
    Highscore("sounds/highscore.wav"),
    NextLevel("sounds/next_level.wav")
}

fun AssetManager.load(asset: SoundAssets) = load<Sound>(asset.filePath)
operator fun AssetManager.get(asset: SoundAssets) = getAsset<Sound>(asset.filePath)