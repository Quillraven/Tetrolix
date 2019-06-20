package com.tetrolix.game.model

import com.badlogic.gdx.utils.IntArray

private const val FLASH_ROW_TIME = 1.00f
private const val COLOR_CHANGE_INTERVAL = 0.1f

class Cell(internal var type: BlockType = BlockType.Empty)

class Grid(width: Int, height: Int) {
    private val grid = Array(height) { Array(width) { Cell() } }

    // variables to flash rows before they get removed
    private val rowsToRemove = IntArray()
    private var colorChangeTimer = 0f
    private var flashRowTimer = 0f
    private var fillTime = 0f
    private var blocksToFill = 0f
    private var filledBlocks = 0f

    fun rows() = grid.size

    fun columns() = grid[0].size

    operator fun get(row: Int, column: Int) = grid[row][column]

    fun removeBlock(block: Block) {
        for (row in 0 until block.rows()) {
            for (column in 0 until block.columns()) {
                val rowIdx = block.getRowIdx(row)
                val colIdx = block.getColumnIdx(column)
                // ignore block cells that are outside the grid or that are not used for the block's structure
                if (rowIdx < 0 || rowIdx >= rows() || colIdx < 0 || colIdx >= columns() || block[row, column] == 0) continue

                // clear grid cell
                this[rowIdx, colIdx].type = BlockType.Empty
            }
        }
    }

    fun addBlock(block: Block) {
        for (row in 0 until block.rows()) {
            for (column in 0 until block.columns()) {
                val rowIdx = block.getRowIdx(row)
                val colIdx = block.getColumnIdx(column)
                // ignore block cells that are outside the grid or that are not used for the block's structure
                if (rowIdx < 0 || rowIdx >= rows() || colIdx < 0 || colIdx >= columns() || block[row, column] == 0) continue

                // change grid cell to block grid cell data
                this[rowIdx, colIdx].type = block.type()
            }
        }
    }

    fun update(currentBlock: Block, delta: Float): Boolean {
        if (fillTime > 0) {
            fillTime -= delta
            filledBlocks += blocksToFill * delta
            var toFill = filledBlocks
            for (row in 0 until rows()) {
                for (col in 0 until columns()) {
                    toFill--
                    if (toFill <= 0) break
                    this[row, col].type = BlockType.random()
                }
                if (toFill <= 0) break
            }
            return true
        } else if (flashRowTimer > 0) {
            // flash rows that get removed
            flashRowTimer -= delta
            colorChangeTimer -= delta
            if (colorChangeTimer <= 0) {
                colorChangeTimer = COLOR_CHANGE_INTERVAL
                for (i in 0 until rowsToRemove.size) {
                    for (column in 0 until columns()) {
                        // change block type for flash effect
                        this[rowsToRemove[i], column].type = this[rowsToRemove[i], column].type.next()
                    }
                }
            }
            return true
        } else if (rowsToRemove.size > 0) {
            // remove rows
            for (i in rowsToRemove.size - 1 downTo 0) {
                // since the entire grid is shifted down all following row indices have to be reduced by the amount of rows that get removed beforehand
                val rowIdx = rowsToRemove[i] - (rowsToRemove.size - 1 - i)
                for (column in 0 until columns()) {
                    // clear grid cell
                    this[rowIdx, column].type = BlockType.Empty
                }
                // shift all rows down
                for (rowToShift in rowIdx until rows()) {
                    for (colToShift in 0 until columns()) {
                        this[rowToShift, colToShift].type = if (rowToShift + 1 >= rows()) BlockType.Empty else this[rowToShift + 1, colToShift].type
                    }
                }
            }
            rowsToRemove.clear()
            // spawn next block
            currentBlock.spawn(this)
        }
        return false
    }

    fun removeFullRows(block: Block): Int {
        var numRowsCleared = 0
        var rowToCheck = 0
        while (rowToCheck < block.rows()) {
            val rowIdx = block.getRowIdx(rowToCheck)
            rowToCheck++

            // ignore rows that are not part of the grid
            if (rowIdx < 0 || rowIdx >= rows()) continue

            var clearRow = true
            for (column in 0 until columns()) {
                // check for empty grid cells. If there are any then do not clear the row
                if (this[rowIdx, column].type == BlockType.Empty) {
                    clearRow = false
                    break
                }
            }

            if (clearRow) {
                ++numRowsCleared
                rowsToRemove.add(rowIdx)
                colorChangeTimer = COLOR_CHANGE_INTERVAL
                flashRowTimer = FLASH_ROW_TIME
            }
        }
        return numRowsCleared
    }

    fun clear() {
        rowsToRemove.clear()
        colorChangeTimer = 0f
        flashRowTimer = 0f
        fillTime = 0f
        for (row in 0 until rows()) {
            for (col in 0 until columns()) {
                this[row, col].type = BlockType.Empty
            }
        }
    }

    fun fillRandom(timeToFill: Float) {
        fillTime = timeToFill
        blocksToFill = rows() * columns() / fillTime
        filledBlocks = 0f
    }
}