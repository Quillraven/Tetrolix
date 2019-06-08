package com.tetrolix.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import ktx.assets.getAsset
import ktx.assets.load

// textures
enum class TextureAssets(val filePath: String) {
    Block("graphics/block.png")
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.filePath)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.filePath)

// music
enum class MusicAssets(val filePath: String) {
    Game("music/game.ogg"),
    GameFinale("music/game_finale.ogg"),
    Highscore("music/highscore.ogg"),
    Menu("music/menu.ogg")
}

fun AssetManager.load(asset: MusicAssets) = load<Music>(asset.filePath)
operator fun AssetManager.get(asset: MusicAssets) = getAsset<Music>(asset.filePath)

// sounds
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

// UI
enum class Drawables {
    btn, btn_music_on, btn_music_off, btn_arrow, btn_rotate, btn_dark, gutter, gutter_dark;

    operator fun invoke() = toString()
}

enum class Buttons {
    music, arrow, rotate;

    operator fun invoke() = toString()
}

enum class Labels {
    huge, bright;

    operator fun invoke() = toString()
}