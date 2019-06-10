package com.tetrolix.game.model

import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * Representation of a Tetrolix block. The **x** and **y** coordinate specify the **top-left** corner of the block.
 * The **type** represents the type of block and the **currentPatternIdx** indicates which rotation of the block is currently active.
 */
class Block {
    private var x = 0
    private var y = 0
    private var type = BlockType.Empty
    private var currentPatternIdx = 0
    private var next = BlockType.random()

    fun type() = type

    fun next() = next

    /**
     * Returns the bounding box amount of rows for the block. E.g. block I has a bounding box of size 4x4
     */
    fun rows() = type.patterns[currentPatternIdx].size

    /**
     * Returns the bounding box amount of columns for the block. E.g. block I has a bounding box of size 4x4
     */
    fun columns() = type.patterns[currentPatternIdx][0].size

    /**
     * Returns the real height of the block. E.g. block J has a bounding box of size 3x3 but the real size can be 3x2 and therefore the height would be 2
     */
    private fun height(): Int {
        var result = 0
        for (row in 0 until rows()) {
            for (column in 0 until columns()) {
                if (this[row, column] != 0) {
                    ++result
                    break
                }
            }
        }
        return result
    }

    operator fun get(row: Int, column: Int) = type.patterns[currentPatternIdx][row][column]

    fun spawn(grid: Grid): Boolean {
        currentPatternIdx = 0
        type = next
        next = BlockType.random()
        x = (grid.columns() * 0.5f - columns() * 0.5f).toInt()
        y = grid.rows() + height() - 2
        // special case for I block because it has a height of 1 but due to the bounding box of 4x4 it would spawn already within the grid
        if (y <= grid.rows()) y = grid.rows()

        y--
        return if (isOutsideOrBlocked(grid)) {
            // block cannot move anymore --> game is lost
            false
        } else {
            y++
            grid.addBlock(this)
            true
        }
    }

    // subtract row because bottom row of grid has index 0 and top row has index grid.rows()
    // --> bottom left = 0,0
    fun getRowIdx(row: Int) = y - row

    // add column because left column of grid has index 0 and right column has index grid.columns()
    // --> bottom left = 0,0
    fun getColumnIdx(column: Int) = x + column

    /**
     * Checks if a block is outside the game grid ([Grid]) or blocked by any other blocks. It only checks for left, bottom and right
     * border since it is valid that a block falls from the top into the grid.
     * @param grid game grid
     */
    private fun isOutsideOrBlocked(grid: Grid): Boolean {
        for (row in 0 until rows()) {
            for (column in 0 until columns()) {
                // ignore grid cell if it is not used for the block
                if (this[row, column] == 0) continue

                val rowIdx = getRowIdx(row)
                val colIdx = getColumnIdx(column)
                if (colIdx < 0 || rowIdx < 0 || colIdx >= grid.columns() || (rowIdx < grid.rows() && grid[rowIdx, colIdx].type != BlockType.Empty)) {
                    return true
                }
            }
        }

        return false
    }

    private fun move(newX: Int, newY: Int, grid: Grid): Boolean {
        grid.removeBlock(this)
        val oldX = x
        val oldY = y
        x = newX
        y = newY
        return if (isOutsideOrBlocked(grid)) {
            // cannot move
            x = oldX
            y = oldY
            grid.addBlock(this)
            false
        } else {
            // move possible -> update grid
            x = newX
            y = newY
            grid.addBlock(this)
            true
        }
    }

    fun moveLeft(grid: Grid) = move(x - 1, y, grid)

    fun moveRight(grid: Grid) = move(x + 1, y, grid)

    fun moveDown(grid: Grid) = move(x, y - 1, grid)

    /**
     * Moves block to the bottom most location where it is not blocked or outside the grid
     * @param grid game grid ([Grid])
     */
    fun moveToBottom(grid: Grid) {
        grid.removeBlock(this)
        val oldY = y
        while (!isOutsideOrBlocked(grid)) {
            --y
        }
        y = if (y == oldY) oldY else y + 1
        grid.addBlock(this)
    }

    fun rotate(grid: Grid, clockwise: Boolean) {
        grid.removeBlock(this)

        val origPatternIdx = currentPatternIdx
        if (clockwise) {
            currentPatternIdx = (currentPatternIdx + 1) % type.patterns.size
        } else {
            currentPatternIdx = (currentPatternIdx - 1)
            if (currentPatternIdx < 0) currentPatternIdx = type.patterns.size - 1
        }
        if (isOutsideOrBlocked(grid)) {
            currentPatternIdx = origPatternIdx
        }

        grid.addBlock(this)
    }
}

/**
 * Predefined list of blocks that are used in **Tetrolix**.
 *
 * **patterns** represents the different patterns of a block. Normally there are 4 patterns for the four different rotations.
 * The structure of the block is defined by an InArray where **0** means the grid cell is not used and **1** means it is used.
 * Width and Height of the grid must be the same.
 */
enum class BlockType(internal val patterns: Array<Array<IntArray>>) {
    // Empty block for empty grid cells
    Empty(arrayOf(arrayOf(intArrayOf(1)))),
    // I
    I(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 1, 0), intArrayOf(0, 0, 1, 0)), // 90
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0), intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 0, 0)), // 180
            arrayOf(intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0), intArrayOf(0, 1, 0, 0))) // 270
    ),
    // J
    J(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1), intArrayOf(0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 1, 1), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0)), // 90
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(1, 1, 1), intArrayOf(0, 0, 1)), // 180
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(1, 1, 0))) // 270
    ),
    // L
    L(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1), intArrayOf(0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 1)), // 90
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(1, 1, 1), intArrayOf(1, 0, 0)), // 180
            arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 0), intArrayOf(0, 1, 0))) // 270
    ),
    // O
    O(arrayOf(arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)))),
    // S
    S(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0), intArrayOf(0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 0, 1)), // 90
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)), // 180
            arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 0), intArrayOf(0, 1, 0))) // 270
    ),
    // T
    T(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1), intArrayOf(0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 1, 0)), // 90
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(1, 1, 1), intArrayOf(0, 1, 0)), // 180
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 0), intArrayOf(0, 1, 0))) // 270
    ),
    // Z
    Z(arrayOf( // 4 rotation types
            arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1), intArrayOf(0, 0, 0)), // 0
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(0, 1, 1), intArrayOf(0, 1, 0)), // 90
            arrayOf(intArrayOf(0, 0, 0), intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)), // 180
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 0), intArrayOf(1, 0, 0))) // 270
    );

    init {
        for (pattern in patterns) {
            for (row in 0 until pattern.size) {
                if (pattern.size != pattern[row].size) throw GdxRuntimeException("Invalid BlockType $this in row $row. Block width and height must be of same size: ${pattern.size} x ${pattern[row].size}")
            }
        }
    }

    fun next(): BlockType = if (ordinal + 1 < values().size) values()[ordinal + 1] else I

    companion object {
        fun random(): BlockType {
            val result = values().random()
            return if (result == Empty) I else result
        }
    }
}