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
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.inject.Context

class GameScreen(context: Context) : KtxScreen {
    private val batch = context.inject<Batch>()
    private val assets = context.inject<AssetManager>()
    private val viewport = context.inject<Viewport>()
    private val stage = context.inject<Stage>()

    private val blockTexture = assets[TextureAssets.Block]

    private val grid = Grid(10, 20)
    private var currentBlock = Block()
    private var currentColorTheme = ColorTheme.Level0
    private var numResets = 0
    private var accumulator = 0f
    private var tickThreshold = 1f // 1 = once per second; 0.5 = twice per second; 0.1 = ten times per second
    private var highscore = 0

    override fun show() {
        assets[MusicAssets.Game].run {
            isLooping = true
            play()
        }
        currentBlock.spawn(grid)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        stage.viewport.update(width, height)
    }

    private fun resetAccumulator() {
        ++numResets
        if (numResets < 10) {
            accumulator = 0f
        }
    }

    override fun render(delta: Float) {
        // render game
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                currentBlock.moveLeft(grid)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                currentBlock.moveRight(grid)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {
                currentBlock.moveToBottom(grid)
                resetAccumulator()
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
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) -> currentColorTheme = ColorTheme.Level0
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) -> currentColorTheme = ColorTheme.Level1
        }

        accumulator += delta
        while (accumulator >= tickThreshold) {
            // update game logic every X seconds
            accumulator -= tickThreshold
            tick()
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

    private fun tick() {
        numResets = 0
        if (!currentBlock.moveDown(grid)) {
            val numClearedRows = grid.removeFullRows(currentBlock)
            if (numClearedRows > 0) {
                highscore += numClearedRows
                if (highscore > 4) {
                    currentColorTheme = ColorTheme.Level1
                    tickThreshold *= 0.5f
                }

                assets[SoundAssets.LineComplete].play()
            } else {
                assets[SoundAssets.BlockLock].play()
            }

            if (!currentBlock.spawn(grid)) {
                println("Game is lost!")
            }
        }
    }
}