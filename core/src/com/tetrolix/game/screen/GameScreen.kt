package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.*
import com.tetrolix.game.model.Block
import com.tetrolix.game.model.ColorTheme
import com.tetrolix.game.model.Grid
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.inject.Context

private const val MAX_RESETS = 20
private const val ROWS_FOR_NEXT_LEVEL = 10
private const val MAX_LOCK_TIMER = 0.75f

class GameScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val batch = context.inject<Batch>()
    private val assets = context.inject<AssetManager>()
    private val audioMgr = context.inject<AudioManager>()
    private val viewport = context.inject<Viewport>()
    private val stage = context.inject<Stage>()

    private val blockTexture = assets[TextureAssets.Block]

    private val grid = Grid(10, 20)
    private var currentBlock = Block()
    private var currentColorTheme = ColorTheme.Theme0

    private var numResets = 0
    private var clearedRows = 0

    var currentLevel = 1
    private var accumulator = 0f
    private var tickThreshold = 1f // 1 = once per second; 0.5 = twice per second; 0.1 = ten times per second
    private var highscore = 0
    private var lockTimer = 0f


    override fun show() {
        stage.clear()

        // set music
        if (currentLevel >= 7) {
            // more epic Music for finale ;)
            audioMgr.play(MusicAssets.GameFinale)
        } else {
            audioMgr.play(MusicAssets.Game)
        }

        // reset game state to initial values
        // clear grid
        grid.clear()
        // set level: currentLevel is set from "SelectLevelScreen"
        setLevel(currentLevel)
        // reset game internal counters
        numResets = 0
        clearedRows = 0
        accumulator = 0f
        highscore = 0
        lockTimer = 0f

        // spawn first block
        currentBlock.spawn(grid)
    }

    private fun setLevel(newLevel: Int) {
        currentLevel = newLevel
        // set color theme
        currentColorTheme = if (currentLevel >= 15) ColorTheme.Transparent else ColorTheme.Theme0
        if (currentLevel < 15) {
            for (i in 1 until currentLevel) {
                currentColorTheme = currentColorTheme.next()
            }
        }
        // set how fast blocks are falling
        tickThreshold = 1f
        for (i in 1 until currentLevel) {
            tickThreshold *= 0.75f
        }
        // reset rows to clear
        clearedRows = 0
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        stage.viewport.update(width, height)
    }

    private fun resetAccumulator() {
        ++numResets
        if (numResets < MAX_RESETS) {
            accumulator = 0f
        }
    }

    override fun render(delta: Float) {
        // render game
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                currentBlock.moveLeft(grid)
                audioMgr.play(SoundAssets.BlockMove)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                currentBlock.moveRight(grid)
                audioMgr.play(SoundAssets.BlockMove)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> {
                currentBlock.moveToBottom(grid)
                if (lockTimer == 0f) {
                    lockTimer = MAX_LOCK_TIMER
                }
            }
            Gdx.input.isKeyJustPressed(Input.Keys.R) -> {
                currentBlock.rotate(grid, true)
                audioMgr.play(SoundAssets.RotateRight)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.L) -> {
                currentBlock.rotate(grid, false)
                audioMgr.play(SoundAssets.RotateLeft)
                resetAccumulator()
            }
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) -> game.setScreen<MenuScreen>()
        }

        if (lockTimer == 0f) {
            // no block locking at the moment -> check if block needs to be moved down
            accumulator += delta
            while (accumulator >= tickThreshold) {
                // force block to move down every X seconds
                accumulator -= tickThreshold
                if (!currentBlock.moveDown(grid)) {
                    // block cannot move anymore -> start block locking
                    lockTimer = MAX_LOCK_TIMER
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
                    it.draw(blockTexture, column.toFloat() * 0.75f, row.toFloat() * 0.75f, 0.75f, 0.75f)
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
            audioMgr.play(SoundAssets.LineComplete)

            if (clearedRows >= ROWS_FOR_NEXT_LEVEL) {
                setLevel(currentLevel + 1)
                audioMgr.play(SoundAssets.NextLevel)
            }
        } else {
            // no rows cleared
            audioMgr.play(SoundAssets.BlockLock)
        }

        numResets = 0
        if (!currentBlock.spawn(grid)) {
            // grid is full and block cannot be spawned -> game over
            audioMgr.play(SoundAssets.GameOver)
            game.setScreen<HighscoreScreen>()
        }
    }
}