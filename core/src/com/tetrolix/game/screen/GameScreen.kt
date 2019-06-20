package com.tetrolix.game.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.Viewport
import com.tetrolix.game.*
import com.tetrolix.game.model.Block
import com.tetrolix.game.model.ColorTheme
import com.tetrolix.game.model.Grid
import ktx.actors.onClick
import ktx.actors.txt
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table

private const val MAX_RESETS = 20
private const val ROWS_FOR_NEXT_LEVEL = 10
private const val MAX_LOCK_TIMER = 0.75f
private const val GAME_SCALE = 0.70f
private const val PREVIEW_BLOCK_SIZE = 24f
private const val GAME_OVER_TIME = 2f

class GameScreen(context: Context) : KtxScreen {
    private val game = context.inject<KtxGame<KtxScreen>>()
    private val batch = context.inject<Batch>()
    private val audioMgr = context.inject<AudioManager>()
    private val viewport = context.inject<Viewport>()
    private val stage = context.inject<Stage>()

    private val blockTexture = Scene2DSkin.defaultSkin.atlas.findRegion(Drawables.Block())

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
    private var gameOver = false

    private val highscoreLabel = Label("Highscore: $highscore", Scene2DSkin.defaultSkin, Labels.BrightBgd())
    private val levelLabel = Label("Current Level: $currentLevel", Scene2DSkin.defaultSkin, Labels.BrightBgd())
    private val btnRotateRight = ImageButton(Scene2DSkin.defaultSkin, Buttons.RotateRight())
    private val btnRotateLeft = ImageButton(Scene2DSkin.defaultSkin, Buttons.RotateLeft())
    private val btnMoveLeft = ImageButton(Scene2DSkin.defaultSkin, Buttons.Arrow()).apply {
        isTransform = true
        setOrigin(UI_BTN_SIZE * 0.5f, UI_BTN_SIZE * 0.5f)
        rotateBy(180f)
    }
    private val btnMoveRight = ImageButton(Scene2DSkin.defaultSkin, Buttons.Arrow())
    private val btnMoveDown = ImageButton(Scene2DSkin.defaultSkin, Buttons.Arrow()).apply {
        isTransform = true
        setOrigin(UI_BTN_SIZE * 0.5f, UI_BTN_SIZE * 0.5f)
        rotateBy(270f)
    }
    private val touchDownEvent = InputEvent().apply { type = InputEvent.Type.touchDown }
    private val touchUpEvent = InputEvent().apply { type = InputEvent.Type.touchUp }

    override fun show() {
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
        updateHighscore(0)
        lockTimer = 0f
        gameOver = false

        // spawn first block
        currentBlock.spawn(grid)

        // create UI
        stage.clear()
        stage.addActor(table {
            // highscore
            actor(highscoreLabel) { cell -> cell.expandX().width(300f).top().left().padTop(15f).padLeft(10f) }
            // level indicator
            actor(levelLabel) { cell -> cell.expandX().width(255f).top().right().padRight(10f).padTop(15f).row() }
            // next block
            label("Next block: ", Labels.BrightBgd()) { cell -> cell.expand().width(300f).top().left().padLeft(10f).padTop(5f).colspan(2).row() }

            // rotate block buttons
            actor(btnRotateLeft) { cell -> cell.size(UI_BTN_SIZE).left().bottom().expand().padLeft(10f).padBottom(10f) }.onClick { rotate(false) }
            actor(btnRotateRight) { cell -> cell.size(UI_BTN_SIZE).right().bottom().expand().colspan(2).padRight(10f).padBottom(10f).row() }.onClick { rotate(true) }

            // move block buttons
            actor(btnMoveLeft) { cell -> cell.size(UI_BTN_SIZE).left().bottom().padLeft(10f).padBottom(80f) }.onClick { moveLeft() }
            actor(btnMoveRight) { cell -> cell.size(UI_BTN_SIZE).right().bottom().padRight(10f).padBottom(80f).row() }.onClick { moveRight() }
            actor(btnMoveDown) { cell -> cell.size(UI_BTN_SIZE).expandX().right().bottom().padRight(10f).padBottom(10f).colspan(2) }.onClick { moveBottom() }

            background = skin.getDrawable(Drawables.GutterDark())
            setFillParent(true)
        })
    }

    private fun setLevel(newLevel: Int) {
        currentLevel = newLevel
        levelLabel.txt = "Current Level: $currentLevel"

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

    private fun moveLeft() {
        currentBlock.moveLeft(grid)
        audioMgr.play(SoundAssets.BlockMove)
        resetAccumulator()
    }

    private fun moveRight() {
        currentBlock.moveRight(grid)
        audioMgr.play(SoundAssets.BlockMove)
        resetAccumulator()
    }

    private fun moveBottom() {
        currentBlock.moveToBottom(grid)
        if (lockTimer == 0f) {
            lockTimer = MAX_LOCK_TIMER
        }
    }

    private fun rotate(clockwise: Boolean) {
        currentBlock.rotate(grid, clockwise)
        audioMgr.play(if (clockwise) SoundAssets.RotateRight else SoundAssets.RotateLeft)
        resetAccumulator()
    }

    private fun simulateButtonPress(button: Button) {
        button.run {
            fire(touchDownEvent)
            fire(touchUpEvent)
        }
    }

    override fun render(delta: Float) {
        if (grid.update(currentBlock, delta)) {
            // grid is still being updated (e.g. flashing full rows) -> do nothing
            draw(delta)
            return
        } else if (gameOver) {
            game.getScreen<HighscoreScreen>().highscore = highscore
            game.setScreen<HighscoreScreen>()
            return
        }

        // desktop input
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> simulateButtonPress(btnMoveLeft)
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> simulateButtonPress(btnMoveRight)
            Gdx.input.isKeyJustPressed(Input.Keys.DOWN) -> simulateButtonPress(btnMoveDown)
            Gdx.input.isKeyJustPressed(Input.Keys.R) -> simulateButtonPress(btnRotateRight)
            Gdx.input.isKeyJustPressed(Input.Keys.L) -> simulateButtonPress(btnRotateLeft)
        }
        // game logic
        updateLogic(delta)
        // render game
        draw(delta)
    }

    private fun updateLogic(delta: Float) {
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
    }

    private fun draw(delta: Float) {
        // draw UI
        stage.act(delta)
        batch.color = Color.WHITE
        stage.viewport.apply(true)
        stage.draw()

        // render game
        viewport.apply(true)
        batch.projectionMatrix = viewport.camera.combined
        batch.use {
            // draw grid
            for (row in 0 until grid.rows()) {
                for (column in 0 until grid.columns()) {
                    batch.color = currentColorTheme.colorMap[grid[row, column].type]
                    it.draw(blockTexture, 1.5f + column * GAME_SCALE, 1.5f + row * GAME_SCALE, GAME_SCALE, GAME_SCALE)
                }
            }
        }

        // render block preview
        stage.viewport.apply(true)
        batch.projectionMatrix = stage.viewport.camera.combined
        batch.use {
            if (currentColorTheme == ColorTheme.Transparent) {
                batch.color = Color.WHITE
            } else {
                batch.color = currentColorTheme.colorMap[currentBlock.next()]
            }
            val pattern = currentBlock.next().patterns[0]
            val offsetY = if (pattern.size == 4) 904f else 892f
            for (row in 0 until pattern.size) {
                for (col in 0 until pattern[0].size) {
                    if (pattern[row][col] == 0) continue
                    it.draw(blockTexture, 180f + PREVIEW_BLOCK_SIZE * col, offsetY - PREVIEW_BLOCK_SIZE * row, PREVIEW_BLOCK_SIZE, PREVIEW_BLOCK_SIZE)
                }
            }
        }

        // reset batch color
        batch.color = Color.WHITE
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

    private fun updateHighscore(newHighscore: Int) {
        highscore = newHighscore
        highscoreLabel.txt = "Highscore: $highscore"
    }

    private fun lockBlock() {
        numResets = 0
        // check for cleared rows
        val numClearedRows = grid.removeFullRows(currentBlock)
        if (numClearedRows > 0) {
            // rows cleared -> update highscore and level
            clearedRows += numClearedRows
            updateHighscore(highscore + getPointsForClear(numClearedRows))
            audioMgr.play(SoundAssets.LineComplete)

            if (clearedRows >= ROWS_FOR_NEXT_LEVEL) {
                setLevel(currentLevel + 1)
                audioMgr.play(SoundAssets.NextLevel)
            }
            // block will be spawned when row flashing in "Grid" class is done
        } else {
            // no rows cleared
            audioMgr.play(SoundAssets.BlockLock)
            // spawn next block
            if (!currentBlock.spawn(grid)) {
                // grid is full and block cannot be spawned -> game over
                audioMgr.play(SoundAssets.GameOver)
                gameOver = true
                // screen switch is done when "Grid" class is done randomly filling in blocks
                grid.fillRandom(GAME_OVER_TIME)
            }
        }
    }
}