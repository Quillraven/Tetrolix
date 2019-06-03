package com.tetrolix.game.model

class Cell(internal var type: BlockType = BlockType.Empty)

class Grid(width: Int, height: Int) {
    private val grid = Array(height) { Array(width) { Cell() } }

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
                // recheck the row again after the entire grid was collapsed by one row
                --rowToCheck
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
        }
        return numRowsCleared
    }
}