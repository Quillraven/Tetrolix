package com.tetrolix.game.model

import com.badlogic.gdx.graphics.Color

enum class ColorTheme(internal val colorMap: Map<BlockType, Color>) {
    Theme0(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.CYAN),
            Pair(BlockType.J, Color.BLUE),
            Pair(BlockType.L, Color.ORANGE),
            Pair(BlockType.O, Color.YELLOW),
            Pair(BlockType.S, Color.GREEN),
            Pair(BlockType.T, Color.PURPLE),
            Pair(BlockType.Z, Color.RED)
    )),
    Theme1(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.WHITE),
            Pair(BlockType.J, Color.CHARTREUSE),
            Pair(BlockType.L, Color.BROWN),
            Pair(BlockType.O, Color.CORAL),
            Pair(BlockType.S, Color.FIREBRICK),
            Pair(BlockType.T, Color.GOLDENROD),
            Pair(BlockType.Z, Color.MAGENTA)
    )),
    Theme2(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.MAROON),
            Pair(BlockType.J, Color.GREEN),
            Pair(BlockType.L, Color.ROYAL),
            Pair(BlockType.O, Color.SALMON),
            Pair(BlockType.S, Color.SCARLET),
            Pair(BlockType.T, Color.WHITE),
            Pair(BlockType.Z, Color.CYAN)
    )),
    Theme3(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.VIOLET),
            Pair(BlockType.J, Color.OLIVE),
            Pair(BlockType.L, Color.NAVY),
            Pair(BlockType.O, Color.LIME),
            Pair(BlockType.S, Color.CYAN),
            Pair(BlockType.T, Color.ORANGE),
            Pair(BlockType.Z, Color.RED)
    )),
    Theme4(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.RED),
            Pair(BlockType.J, Color.PURPLE),
            Pair(BlockType.L, Color.GREEN),
            Pair(BlockType.O, Color.YELLOW),
            Pair(BlockType.S, Color.ORANGE),
            Pair(BlockType.T, Color.BLUE),
            Pair(BlockType.Z, Color.CYAN)
    )),
    Theme5(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.MAGENTA),
            Pair(BlockType.J, Color.GOLDENROD),
            Pair(BlockType.L, Color.FIREBRICK),
            Pair(BlockType.O, Color.CORAL),
            Pair(BlockType.S, Color.BROWN),
            Pair(BlockType.T, Color.CHARTREUSE),
            Pair(BlockType.Z, Color.WHITE)
    )),
    Theme6(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.CYAN),
            Pair(BlockType.J, Color.WHITE),
            Pair(BlockType.L, Color.SCARLET),
            Pair(BlockType.O, Color.SALMON),
            Pair(BlockType.S, Color.ROYAL),
            Pair(BlockType.T, Color.GREEN),
            Pair(BlockType.Z, Color.MAROON)
    )),
    Theme7(mapOf(
            Pair(BlockType.Empty, Color.GRAY),
            Pair(BlockType.I, Color.RED),
            Pair(BlockType.J, Color.ORANGE),
            Pair(BlockType.L, Color.CYAN),
            Pair(BlockType.O, Color.LIME),
            Pair(BlockType.S, Color.NAVY),
            Pair(BlockType.T, Color.OLIVE),
            Pair(BlockType.Z, Color.VIOLET)
    ));

    fun next(): ColorTheme = if (this.ordinal + 1 >= values().size) Theme0 else values()[this.ordinal + 1]
}