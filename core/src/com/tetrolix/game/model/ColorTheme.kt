package com.tetrolix.game.model

import com.badlogic.gdx.graphics.Color

enum class ColorTheme(internal val colorMap: Map<BlockType, Color>) {
    Level0(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.CYAN),
            Pair(BlockType.J, Color.BLUE),
            Pair(BlockType.L, Color.ORANGE),
            Pair(BlockType.O, Color.YELLOW),
            Pair(BlockType.S, Color.GREEN),
            Pair(BlockType.T, Color.PURPLE),
            Pair(BlockType.Z, Color.RED)
    )),
    Level1(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.WHITE),
            Pair(BlockType.J, Color.CHARTREUSE),
            Pair(BlockType.L, Color.BROWN),
            Pair(BlockType.O, Color.CORAL),
            Pair(BlockType.S, Color.FIREBRICK),
            Pair(BlockType.T, Color.GOLDENROD),
            Pair(BlockType.Z, Color.MAGENTA)
    ))
}