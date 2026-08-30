package com.runemaster.app

data class EditorFormulaInput(
    val title: String,
    val intention: String,
    val primaryRune: String,
    val supportingRunes: List<String>
)
