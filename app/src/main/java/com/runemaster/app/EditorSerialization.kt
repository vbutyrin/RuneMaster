package com.runemaster.app

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val editorJson = Json {
    prettyPrint = true
    encodeDefaults = true
}

fun serializeEditorRunes(
    values: List<EditorRune>
): String {

    val snapshots =
        values.mapIndexed { index, rune ->
            RuneLayerSnapshot(
                rune = rune.name,
                symbol = rune.symbol,
                x = rune.x,
                y = rune.y,
                scale = rune.scale,
                rotation = rune.rotation,
                mirrored = rune.mirrorX,
                primary = rune.primary,
                locked = rune.locked,
                layer = index
            )
        }

    return editorJson.encodeToString(
        snapshots
    )
}
