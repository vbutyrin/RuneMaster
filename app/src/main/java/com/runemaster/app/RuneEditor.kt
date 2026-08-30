package com.runemaster.app

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChanged
import kotlin.math.min
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.hypot

data class EditorRune(
    val id: Long,
    val symbol: String,
    val name: String,
    val x: Float = .5f,
    val y: Float = .5f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val mirrorX: Boolean = false,
    val primary: Boolean = false,
    val locked: Boolean = false
)

private val editorRunes = listOf(
    "ᚠ" to "FEHU", "ᚢ" to "URUZ",
    "ᚦ" to "THURISAZ", "ᚨ" to "ANSUZ",
    "ᚱ" to "RAIDHO", "ᚲ" to "KENAZ",
    "ᚷ" to "GEBO", "ᚹ" to "WUNJO",
    "ᚺ" to "HAGALAZ", "ᚾ" to "NAUTHIZ",
    "ᛁ" to "ISA", "ᛃ" to "JERA",
    "ᛇ" to "EIHWAZ", "ᛈ" to "PERTHRO",
    "ᛉ" to "ALGIZ", "ᛊ" to "SOWILO",
    "ᛏ" to "TIWAZ", "ᛒ" to "BERKANO",
    "ᛖ" to "EHWAZ", "ᛗ" to "MANNAZ",
    "ᛚ" to "LAGUZ", "ᛜ" to "INGWAZ",
    "ᛞ" to "DAGAZ", "ᛟ" to "OTHALA"
)

@Composable
fun RuneEditorScreen(onBack: () -> Unit) {

    var elements by remember {
        mutableStateOf(
            listOf(
                EditorRune(
                    id = 1,
                    symbol = "ᚷ",
                    name = "GEBO",
                    primary = true,
                    scale = 1.15f
                )
            )
        )
    }

    var selectedId by remember {
        mutableStateOf<Long?>(1)
    }

    var counter by remember {
        mutableLongStateOf(2)
    }

    var undoStack by remember {
        mutableStateOf<List<List<EditorRune>>>(emptyList())
    }

    var redoStack by remember {
        mutableStateOf<List<List<EditorRune>>>(emptyList())
    }

    fun checkpoint() {
        undoStack = (undoStack + listOf(elements)).takeLast(40)
        redoStack = emptyList()
    }

    fun undo() {
        val previous = undoStack.lastOrNull() ?: return
        redoStack = redoStack + listOf(elements)
        elements = previous
        undoStack = undoStack.dropLast(1)

        if (elements.none { it.id == selectedId }) {
            selectedId = elements.lastOrNull()?.id
        }
    }

    fun redo() {
        val next = redoStack.lastOrNull() ?: return
        undoStack = undoStack + listOf(elements)
        elements = next
        redoStack = redoStack.dropLast(1)

        if (elements.none { it.id == selectedId }) {
            selectedId = elements.lastOrNull()?.id
        }
    }

    val currentElements by rememberUpdatedState(elements)
    val currentSelected by rememberUpdatedState(selectedId)

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF080706))
            .padding(12.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text(
                    "‹ НАЗАД",
                    color = Color(0xFFD6A94C)
                )
            }

            Text(
                "RUNE EDITOR PRO",
                color = Color(0xFFF6DA8A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
        }

        Text(
            "1 палец — перемещение • 2 пальца — масштаб и вращение",
            color = Color(0xFFBBAE8B),
            fontSize = 12.sp
        )

        Spacer(Modifier.height(9.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF100D09)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {

            BoxWithConstraints(
                Modifier.fillMaxSize()
            ) {

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {

                            awaitEachGesture {

                                var activeId: Long? = null

                                while (true) {

                                    val event =
                                        awaitPointerEvent(
                                            PointerEventPass.Main
                                        )

                                    val pressed =
                                        event.changes
                                            .filter { it.pressed }

                                    if (pressed.isEmpty()) {
                                        break
                                    }

                                    val all =
                                        currentElements

                                    if (activeId == null) {

                                        val touch =
                                            pressed.first().position

                                        val hit =
                                            all.asReversed()
                                                .firstOrNull { rune ->

                                                    val cx =
                                                        size.width *
                                                            rune.x

                                                    val cy =
                                                        size.height *
                                                            rune.y

                                                    val canvasMin =
                                                        min(
                                                            size.width.toFloat(),
                                                            size.height.toFloat()
                                                        )

                                                    val radius =
                                                        canvasMin *
                                                            .15f *
                                                            rune.scale
                                                                .coerceAtLeast(.6f)

                                                    hypot(
                                                        touch.x - cx,
                                                        touch.y - cy
                                                    ) <= radius
                                                }

                                        if (hit != null) {
                                            selectedId = hit.id
                                            activeId = hit.id

                                            if (!hit.locked) {
                                                checkpoint()
                                            }
                                        } else {
                                            activeId =
                                                currentSelected

                                            val selectedRune =
                                                currentElements.find {
                                                    it.id == activeId
                                                }

                                            if (
                                                selectedRune != null &&
                                                !selectedRune.locked
                                            ) {
                                                checkpoint()
                                            }
                                        }
                                    }

                                    val id =
                                        activeId ?: continue

                                    val rune =
                                        currentElements
                                            .find { it.id == id }
                                            ?: continue

                                    if (rune.locked) continue

                                    val pan =
                                        event.calculatePan()

                                    val zoom =
                                        if (pressed.size >= 2)
                                            event.calculateZoom()
                                        else 1f

                                    val rotation =
                                        if (pressed.size >= 2)
                                            event.calculateRotation()
                                        else 0f

                                    elements =
                                        currentElements.map {
                                            if (it.id != id) it
                                            else it.copy(
                                                x = (
                                                    it.x +
                                                        pan.x /
                                                        size.width
                                                ).coerceIn(
                                                    -.25f,
                                                    1.25f
                                                ),

                                                y = (
                                                    it.y +
                                                        pan.y /
                                                        size.height
                                                ).coerceIn(
                                                    -.25f,
                                                    1.25f
                                                ),

                                                scale = (
                                                    it.scale *
                                                        zoom
                                                ).coerceIn(
                                                    .15f,
                                                    6f
                                                ),

                                                rotation = (
                                                    it.rotation +
                                                        rotation
                                                ) % 360f
                                            )
                                        }

                                    event.changes.forEach {
                                        if (
                                            it.positionChanged()
                                        ) {
                                            it.consume()
                                        }
                                    }
                                }
                            }
                        }
                ) {

                    // центральные направляющие
                    drawLine(
                        Color(0xFFD6A94C)
                            .copy(alpha = .07f),
                        Offset(
                            size.width / 2,
                            0f
                        ),
                        Offset(
                            size.width / 2,
                            size.height
                        )
                    )

                    drawLine(
                        Color(0xFFD6A94C)
                            .copy(alpha = .07f),
                        Offset(
                            0f,
                            size.height / 2
                        ),
                        Offset(
                            size.width,
                            size.height / 2
                        )
                    )

                    elements.forEach { rune ->

                        val center =
                            Offset(
                                size.width * rune.x,
                                size.height * rune.y
                            )

                        val textSize =
                            size.minDimension *
                                .22f

                        withTransform({

                            translate(
                                center.x,
                                center.y
                            )

                            rotate(rune.rotation)

                            scale(
                                if (rune.mirrorX)
                                    -rune.scale
                                else rune.scale,
                                rune.scale
                            )

                        }) {

                            // мягкое янтарное свечение
                            drawCircle(
                                color =
                                    Color(0xFFD69B32)
                                        .copy(alpha = .07f),
                                radius = textSize * .55f
                            )

                            // глубокая тень
                            drawContext.canvas
                                .nativeCanvas
                                .drawText(
                                    rune.symbol,
                                    7f,
                                    9f,
                                    Paint().apply {
                                        color =
                                            android.graphics.Color
                                                .rgb(
                                                    34,
                                                    18,
                                                    3
                                                )
                                        this.textSize =
                                            textSize
                                        textAlign =
                                            Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                )

                            // бронзовый слой
                            drawContext.canvas
                                .nativeCanvas
                                .drawText(
                                    rune.symbol,
                                    4f,
                                    5f,
                                    Paint().apply {
                                        color =
                                            android.graphics.Color
                                                .rgb(
                                                    120,
                                                    70,
                                                    15
                                                )
                                        this.textSize =
                                            textSize
                                        textAlign =
                                            Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                )

                            // основное золото
                            drawContext.canvas
                                .nativeCanvas
                                .drawText(
                                    rune.symbol,
                                    0f,
                                    0f,
                                    Paint().apply {
                                        color =
                                            android.graphics.Color
                                                .rgb(
                                                    218,
                                                    169,
                                                    66
                                                )

                                        this.textSize =
                                            textSize

                                        textAlign =
                                            Paint.Align.CENTER

                                        isAntiAlias = true

                                        setShadowLayer(
                                            14f,
                                            0f,
                                            0f,
                                            android.graphics.Color
                                                .rgb(
                                                    125,
                                                    76,
                                                    15
                                                )
                                        )
                                    }
                                )

                            // верхний блик
                            drawContext.canvas
                                .nativeCanvas
                                .drawText(
                                    rune.symbol,
                                    -1.7f,
                                    -2.2f,
                                    Paint().apply {
                                        color =
                                            android.graphics.Color
                                                .rgb(
                                                    255,
                                                    229,
                                                    150
                                                )

                                        this.textSize =
                                            textSize * .985f

                                        textAlign =
                                            Paint.Align.CENTER

                                        isAntiAlias = true
                                        alpha = 145
                                    }
                                )

                            if (rune.primary) {
                                drawCircle(
                                    color =
                                        Color(0xFFD6A94C)
                                            .copy(alpha = .20f),
                                    radius =
                                        textSize * .53f
                                )
                            }

                            if (
                                rune.id ==
                                selectedId
                            ) {
                                drawCircle(
                                    color =
                                        Color(0xFFF6DA8A)
                                            .copy(alpha = .8f),
                                    radius =
                                        textSize * .63f,
                                    style =
                                        Stroke(1.4f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EditorButton(if (undoStack.isEmpty()) "↶·" else "UNDO") {
                undo()
            }

            EditorButton(if (redoStack.isEmpty()) "↷·" else "REDO") {
                redo()
            }

            EditorButton("AUTO") {
                if (elements.isNotEmpty()) {
                    checkpoint()

                    val count = elements.size
                    elements = elements.mapIndexed { index, rune ->
                        val spacing =
                            if (count <= 1) 0f
                            else .64f / (count - 1)

                        rune.copy(
                            x =
                                if (count == 1) .5f
                                else .18f + spacing * index,
                            y = .5f,
                            rotation = 0f,
                            scale =
                                if (rune.primary) 1.15f
                                else .82f
                        )
                    }
                }
            }

            EditorButton("CENTER") {
                val id = selectedId
                if (id != null) {
                    checkpoint()
                    elements = elements.map {
                        if (it.id == id)
                            it.copy(x = .5f, y = .5f)
                        else it
                    }
                }
            }
        }

        Spacer(Modifier.height(5.dp))

        val selected =
            elements.find {
                it.id == selectedId
            }

        if (selected != null) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    "${selected.symbol} ${selected.name}",
                    color = Color(0xFFF6DA8A),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "× %.2f   %.0f°".format(
                        selected.scale,
                        selected.rotation
                    ),
                    color = Color(0xFFBBAE8B),
                    fontSize = 12.sp
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                EditorButton("↶") {
                    checkpoint()
                    elements =
                        elements.map {
                            if (
                                it.id ==
                                selected.id
                            )
                                it.copy(
                                    rotation =
                                        it.rotation - 15f
                                )
                            else it
                        }
                }

                EditorButton("↷") {
                    checkpoint()
                    elements =
                        elements.map {
                            if (
                                it.id ==
                                selected.id
                            )
                                it.copy(
                                    rotation =
                                        it.rotation + 15f
                                )
                            else it
                        }
                }

                EditorButton("⇋") {
                    checkpoint()
                    elements =
                        elements.map {
                            if (
                                it.id ==
                                selected.id
                            )
                                it.copy(
                                    mirrorX =
                                        !it.mirrorX
                                )
                            else it
                        }
                }

                EditorButton(
                    if (selected.locked)
                        "🔒"
                    else "🔓"
                ) {
                    checkpoint()
                    elements =
                        elements.map {
                            if (
                                it.id ==
                                selected.id
                            )
                                it.copy(
                                    locked =
                                        !it.locked
                                )
                            else it
                        }
                }

                EditorButton("★") {
                    checkpoint()
                    elements =
                        elements.map {
                            it.copy(
                                primary =
                                    it.id ==
                                        selected.id
                            )
                        }
                }

                EditorButton("✕") {
                    checkpoint()
                    elements =
                        elements.filter {
                            it.id !=
                                selected.id
                        }

                    selectedId =
                        elements.lastOrNull()?.id
                }
            }

            Spacer(Modifier.height(5.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                EditorButton("COPY") {
                    checkpoint()

                    val copy = selected.copy(
                        id = counter++,
                        x = (selected.x + .06f).coerceAtMost(1.1f),
                        y = (selected.y + .06f).coerceAtMost(1.1f),
                        primary = false,
                        locked = false
                    )

                    elements = elements + copy
                    selectedId = copy.id
                }

                EditorButton("UP") {
                    val index =
                        elements.indexOfFirst {
                            it.id == selected.id
                        }

                    if (index >= 0 && index < elements.lastIndex) {
                        checkpoint()

                        val list = elements.toMutableList()
                        val item = list.removeAt(index)
                        list.add(index + 1, item)
                        elements = list
                    }
                }

                EditorButton("DOWN") {
                    val index =
                        elements.indexOfFirst {
                            it.id == selected.id
                        }

                    if (index > 0) {
                        checkpoint()

                        val list = elements.toMutableList()
                        val item = list.removeAt(index)
                        list.add(index - 1, item)
                        elements = list
                    }
                }

                EditorButton("RESET") {
                    checkpoint()

                    elements = elements.map {
                        if (it.id == selected.id)
                            it.copy(
                                scale = 1f,
                                rotation = 0f,
                                mirrorX = false
                            )
                        else it
                    }
                }
            }
        }

        Spacer(Modifier.height(7.dp))

        Text(
            "ДОБАВИТЬ РУНУ",
            color = Color(0xFFD6A94C),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            items(editorRunes) { rune ->

                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF17130D)
                        )
                ) {
                    TextButton(
                        onClick = {

                            checkpoint()

                            val item =
                                EditorRune(
                                    id = counter++,
                                    symbol = rune.first,
                                    name = rune.second,
                                    x = .5f,
                                    y = .5f,
                                    scale = .8f
                                )

                            elements =
                                elements + item

                            selectedId =
                                item.id
                        }
                    ) {
                        Text(
                            rune.first,
                            color =
                                Color(0xFFF6DA8A),
                            fontSize = 27.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.EditorButton(
    text: String,
    action: () -> Unit
) {
    Button(
        onClick = action,
        modifier = Modifier.weight(1f),
        contentPadding =
            PaddingValues(
                horizontal = 1.dp,
                vertical = 6.dp
            ),
        colors =
            ButtonDefaults.buttonColors(
                containerColor =
                    Color(0xFF2B2110),
                contentColor =
                    Color(0xFFF6DA8A)
            )
    ) {
        Text(
            text,
            fontSize = 11.sp
        )
    }
}
