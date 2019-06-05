package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.MusicAssets
import com.tetrolix.game.SoundAssets
import com.tetrolix.game.TextureAssets
import com.tetrolix.game.get
import com.tetrolix.game.model.Block
import com.tetrolix.game.model.ColorTheme
import com.tetrolix.game.model.Grid
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.inject.Context

class GameScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val batch = context.inject<Batch>()
    private val assets = context.inject<AssetManager>()
    private val viewport = context.inject<Viewport>()
    private val stage = context.inject<Stage>()

    private val blockTexture = assets[TextureAssets.Block]

    private val grid = Grid(10, 20)
    private var currentBlock = Block()
    private var currentColorTheme = ColorTheme.Theme0
    private var currentMusic = assets[MusicAssets.Game]

    private var numResets = 0
    private val maxResets = 20
    private var clearedRows = 0
    private val rowsForNextLevel = 10
    private var currentLevel = 1
    private var accumulator = 0f
    private var tickThreshold = 1f // 1 = once per second; 0.5 = twice per second; 0.1 = ten times per second
    private var highscore = 0
    private var lockTimer = 0f
    private val maxLockTimer = 0.75f

    override fun show() {
        stage.clear()

        currentMusic.run {
            isLooping = true
            play()
        }

        currentBlock.spawn(grid)
    }

    override fun hide() {
        currentMusic.stop()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        stage.viewport.update(width, height)
    }

    private fun resetAccumulator() {
        ++numResets
        if (numResets < maxResets) {
            accumulator = 0f
        }
    }

    override fun render(delta: Float) {
        // render game
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                currentBlock.moveLeft(grid)
                assets[SoundAssets.BlockMove].play()
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                currentBlock.moveRight(grid)
                assets[SoundAssets.BlockMove].play()
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {
                currentBlock.moveToBottom(grid)
                if (lockTimer == 0f) {
                    lockTimer = maxLockTimer
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.R) -> {
                currentBlock.rotate(grid, true)
                assets[SoundAssets.RotateRight].play()
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.L) -> {
                currentBlock.rotate(grid, false)
                assets[SoundAssets.RotateLeft].play()
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) -> currentColorTheme = currentColorTheme.next()
        }

        if (lockTimer == 0f) {
            // no block locking at the moment -> check if block needs to be moved down
            accumulator += delta
            while (accumulator >= tickThreshold) {
                // force block to move down every X seconds
                accumulator -= tickThreshold
                if (!currentBlock.moveDown(grid)) {
                    // block cannot move anymore -> start block locking
                    lockTimer = maxLockTimer
                    break
                }
            }
        } else {
            // block cannot move anymore -> give the player a little time before block gets locked and a new one spawns
            lockTimer -= delta
            if (lockTimer <= 0) {
                lockTimer = 0f
                // check if the block could be moved down again (e.g. block was moved right/left and can fall again)
                if (!currentBlock.moveDown(grid)) {
                    // if it really cannot move anymore -> lock it
                    lockBlock()
                }
            }
        }

        // render game
        viewport.apply(true)
        batch.projectionMatrix = viewport.camera.combined
        batch.use {
            // draw grid
            for (row in 0 until grid.rows()) {
                for (column in 0 until grid.columns()) {
                    batch.color = currentColorTheme.colorMap[grid[row, column].type]
                    it.draw(blockTexture, column.toFloat(), row.toFloat(), 1f, 1f)
                }
            }
        }

        // draw UI
        stage.act(delta)
        batch.color = Color.WHITE
        stage.viewport.apply(true)
        stage.draw()
    }

    private fun getPointsForClear(numClearedRows: Int): Int {
        return when (numClearedRows) {
            0 -> 0
            1 -> 40 * currentLevel
            2 -> 100 * currentLevel
            3 -> 300 * currentLevel
            else -> 1000 * currentLevel
        }
    }

    private fun lockBlock() {
        // check for cleared rows
        val numClearedRows = grid.removeFullRows(currentBlock)
        if (numClearedRows > 0) {
            // rows cleared -> update highscore and level
            clearedRows += numClearedRows
            highscore += getPointsForClear(numClearedRows)
            assets[SoundAssets.LineComplete].play()

            if (clearedRows >= rowsForNextLevel) {
                clearedRows = 0
                ++currentLevel
                currentColorTheme = if (currentLevel >= 15) ColorTheme.Transparent else currentColorTheme.next()
                tickThreshold *= 0.75f
                assets[SoundAssets.NextLevel].play()

                if (currentLevel == 7) {
                    // change to more epic music for finale ;)
                    currentMusic.stop()
                    currentMusic = assets[MusicAssets.GameFinale]
                    currentMusic.run {
                        isLooping = true
                        play()
                    }
                }
            }
        } else {
            // no rows cleared
            assets[SoundAssets.BlockLock].play()
        }

        numResets = 0
        if (!currentBlock.spawn(grid)) {
            // grid is full and block cannot be spawned -> game over
            assets[SoundAssets.GameOver].play()
            game.setScreen<HighscoreScreen>()
        }
    }
}