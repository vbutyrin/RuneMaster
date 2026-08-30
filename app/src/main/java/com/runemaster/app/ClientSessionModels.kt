package com.runemaster.app

import kotlinx.serialization.Serializable

@Serializable
data class RuneLayerSnapshot(
    val rune: String,
    val symbol: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val mirrored: Boolean,
    val primary: Boolean,
    val locked: Boolean,
    val layer: Int
)

data class ClientSessionDraft(
    val clientName: String,
    val clientNotes: String,
    val problem: String,
    val practitionerNotes: String,
    val formula: EditorFormulaInput
)

data class ClientEditorInput(
    val draft: ClientSessionDraft
)
