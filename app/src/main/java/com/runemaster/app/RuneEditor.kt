package com.runemaster.app

import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

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
    "ᚠ" to "FEHU",
    "ᚢ" to "URUZ",
    "ᚦ" to "THURISAZ",
    "ᚨ" to "ANSUZ",
    "ᚱ" to "RAIDHO",
    "ᚲ" to "KENAZ",
    "ᚷ" to "GEBO",
    "ᚹ" to "WUNJO",
    "ᚺ" to "HAGALAZ",
    "ᚾ" to "NAUTHIZ",
    "ᛁ" to "ISA",
    "ᛃ" to "JERA",
    "ᛇ" to "EIHWAZ",
    "ᛈ" to "PERTHRO",
    "ᛉ" to "ALGIZ",
    "ᛊ" to "SOWILO",
    "ᛏ" to "TIWAZ",
    "ᛒ" to "BERKANO",
    "ᛖ" to "EHWAZ",
    "ᛗ" to "MANNAZ",
    "ᛚ" to "LAGUZ",
    "ᛜ" to "INGWAZ",
    "ᛞ" to "DAGAZ",
    "ᛟ" to "OTHALA"
)

private data class EditorSnapshot(
    val runes: List<EditorRune>,
    val selectedId: Long?
)

@Composable
fun RuneEditorScreen(
    onBack: () -> Unit
) {
    var elements by remember {
        mutableStateOf(
            listOf(
                EditorRune(
                    id = 1,
                    symbol = "ᚷ",
                    name = "GEBO",
                    scale = 1.15f,
                    primary = true
                )
            )
        )
    }

    var selectedId by remember { mutableStateOf<Long?>(1L) }
    var nextId by remember { mutableLongStateOf(2L) }

    var undoStack by remember {
        mutableStateOf<List<EditorSnapshot>>(emptyList())
    }

    var redoStack by remember {
        mutableStateOf<List<EditorSnapshot>>(emptyList())
    }

    fun snapshot() =
        EditorSnapshot(
            elements.map { it.copy() },
            selectedId
        )

    fun rememberBeforeChange() {
        undoStack = (undoStack + snapshot()).takeLast(40)
        redoStack = emptyList()
    }

    fun updateSelected(
        block: (EditorRune) -> EditorRune
    ) {
        val id = selectedId ?: return
        elements = elements.map {
            if (it.id == id) block(it) else it
        }
    }

    val selected =
        elements.find { it.id == selectedId }

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
            "1 палец — перемещение • 2 пальца — масштаб и свободное вращение",
            color = Color(0xFFBBAE8B),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
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
                val density =
                    androidx.compose.ui.platform.LocalDensity.current

                val canvasWidthPx =
                    with(density) {
                        maxWidth.toPx()
                    }

                val canvasHeightPx =
                    with(density) {
                        maxHeight.toPx()
                    }

                RuneCompositionCanvas(
                    runes = elements,
                    selectedId = selectedId,
                    modifier = Modifier.fillMaxSize()
                )

                /*
                 * Жестовый слой расположен поверх canvas.
                 *
                 * ВАЖНО:
                 * координаты rune.x/y хранятся относительно
                 * самого холста.
                 *
                 * rotation больше НЕ применяется к координате
                 * центра руны. Изменяется только угол glyph.
                 */
                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(
                            elements,
                            selectedId,
                            canvasWidthPx,
                            canvasHeightPx
                        ) {
                            detectTransformGestures(
                                panZoomLock = false
                            ) {
                                centroid,
                                pan,
                                zoom,
                                rotation ->

                                val currentId =
                                    selectedId

                                /*
                                 * Если выбранной руны нет,
                                 * пытаемся найти ближайшую.
                                 */
                                if (currentId == null) {
                                    val nearest =
                                        findRuneAt(
                                            elements,
                                            centroid,
                                            size.width.toFloat(),
                                            size.height.toFloat()
                                        )

                                    if (nearest != null) {
                                        selectedId =
                                            nearest.id
                                    }

                                    return@detectTransformGestures
                                }

                                val current =
                                    elements.find {
                                        it.id == currentId
                                    } ?: return@detectTransformGestures

                                if (current.locked) {
                                    return@detectTransformGestures
                                }

                                /*
                                 * Если начало жеста находится
                                 * возле другой руны, выбираем её.
                                 */
                                val touched =
                                    findRuneAt(
                                        elements,
                                        centroid,
                                        size.width.toFloat(),
                                        size.height.toFloat()
                                    )

                                val target =
                                    touched ?: current

                                if (
                                    target.id != selectedId
                                ) {
                                    selectedId = target.id
                                }

                                if (target.locked) {
                                    return@detectTransformGestures
                                }

                                elements =
                                    elements.map { rune ->

                                        if (
                                            rune.id != target.id
                                        ) {
                                            rune
                                        } else {

                                            val nx =
                                                (
                                                    rune.x +
                                                    pan.x /
                                                    size.width
                                                        .toFloat()
                                                )
                                                    .coerceIn(
                                                        .03f,
                                                        .97f
                                                    )

                                            val ny =
                                                (
                                                    rune.y +
                                                    pan.y /
                                                    size.height
                                                        .toFloat()
                                                )
                                                    .coerceIn(
                                                        .03f,
                                                        .97f
                                                    )

                                            rune.copy(
                                                x = nx,
                                                y = ny,
                                                scale =
                                                    (
                                                        rune.scale *
                                                        zoom
                                                    )
                                                        .coerceIn(
                                                            .22f,
                                                            5f
                                                        ),

                                                /*
                                                 * Rotation относится
                                                 * ТОЛЬКО к знаку.
                                                 * Центр x/y остаётся
                                                 * на месте.
                                                 */
                                                rotation =
                                                    normalizeAngle(
                                                        rune.rotation +
                                                        rotation
                                                    )
                                            )
                                        }
                                    }
                            }
                        }
                )
            }
        }

        Spacer(Modifier.height(7.dp))

        if (selected != null) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "${selected.symbol} ${selected.name}",
                        color = Color(0xFFF6DA8A),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Угол ${selected.rotation.toInt()}°  •  Масштаб ${"%.2f".format(selected.scale)}×",
                        color = Color(0xFFBBAE8B),
                        fontSize = 11.sp
                    )
                }

                if (selected.primary) {
                    Text(
                        "★ ГЛАВНАЯ",
                        color = Color(0xFFD6A94C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(5.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                EditorButton("↶") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            rotation =
                                normalizeAngle(
                                    it.rotation - 15f
                                )
                        )
                    }
                }

                EditorButton("↷") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            rotation =
                                normalizeAngle(
                                    it.rotation + 15f
                                )
                        )
                    }
                }

                EditorButton("⇋") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            mirrorX = !it.mirrorX
                        )
                    }
                }

                EditorButton("180") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            rotation =
                                normalizeAngle(
                                    it.rotation + 180f
                                )
                        )
                    }
                }

                EditorButton(
                    if (selected.locked) "🔒" else "🔓"
                ) {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            locked = !it.locked
                        )
                    }
                }

                EditorButton("★") {
                    rememberBeforeChange()

                    elements =
                        elements.map {
                            it.copy(
                                primary =
                                    it.id == selected.id
                            )
                        }
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                EditorButton("−") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            scale =
                                (it.scale - .1f)
                                    .coerceAtLeast(.22f)
                        )
                    }
                }

                EditorButton("+") {
                    rememberBeforeChange()
                    updateSelected {
                        it.copy(
                            scale =
                                (it.scale + .1f)
                                    .coerceAtMost(5f)
                        )
                    }
                }

                EditorButton("⧉") {
                    rememberBeforeChange()

                    val source =
                        elements.find {
                            it.id == selectedId
                        }

                    if (source != null) {
                        val duplicated =
                            source.copy(
                                id = nextId++,
                                x =
                                    (source.x + .06f)
                                        .coerceAtMost(.94f),
                                y =
                                    (source.y + .06f)
                                        .coerceAtMost(.94f),
                                primary = false
                            )

                        elements =
                            elements + duplicated

                        selectedId =
                            duplicated.id
                    }
                }

                EditorButton("◎") {
                    rememberBeforeChange()

                    updateSelected {
                        it.copy(
                            x = .5f,
                            y = .5f
                        )
                    }
                }

                EditorButton("✕") {
                    rememberBeforeChange()

                    val id = selectedId

                    elements =
                        elements.filter {
                            it.id != id
                        }

                    selectedId =
                        elements.lastOrNull()?.id
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (undoStack.isNotEmpty()) {
                        val previous =
                            undoStack.last()

                        redoStack =
                            redoStack + snapshot()

                        undoStack =
                            undoStack.dropLast(1)

                        elements =
                            previous.runes

                        selectedId =
                            previous.selectedId
                    }
                },
                enabled = undoStack.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("UNDO")
            }

            OutlinedButton(
                onClick = {
                    if (redoStack.isNotEmpty()) {
                        val next =
                            redoStack.last()

                        undoStack =
                            undoStack + snapshot()

                        redoStack =
                            redoStack.dropLast(1)

                        elements =
                            next.runes

                        selectedId =
                            next.selectedId
                    }
                },
                enabled = redoStack.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("REDO")
            }
        }

        Text(
            "ДОБАВИТЬ РУНУ",
            color = Color(0xFFD6A94C),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(4.dp),
            contentPadding =
                PaddingValues(vertical = 4.dp)
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
                            rememberBeforeChange()

                            val item =
                                EditorRune(
                                    id = nextId++,
                                    symbol =
                                        rune.first,
                                    name =
                                        rune.second,
                                    x = .5f,
                                    y = .5f,
                                    scale = .85f
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
                            fontSize = 28.sp
                        )
                    }
                }
            }
        }
    }
}

private fun normalizeAngle(
    angle: Float
): Float {
    var result = angle % 360f

    if (result > 180f) {
        result -= 360f
    }

    if (result < -180f) {
        result += 360f
    }

    return result
}

private fun findRuneAt(
    runes: List<EditorRune>,
    position: Offset,
    width: Float,
    height: Float
): EditorRune? {

    if (width <= 0f || height <= 0f) {
        return null
    }

    /*
     * Идём с конца:
     * верхний визуальный слой выбирается первым.
     */
    return runes.asReversed()
        .firstOrNull { rune ->

            val rx =
                width * rune.x

            val ry =
                height * rune.y

            val dx =
                position.x - rx

            val dy =
                position.y - ry

            val distance =
                sqrt(
                    dx * dx +
                    dy * dy
                )

            val radius =
                width
                    .coerceAtMost(height) *
                    .15f *
                    rune.scale
                    .coerceAtLeast(.7f)

            distance <= radius
        }
}

@Composable
private fun RowScope.EditorButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        contentPadding =
            PaddingValues(
                horizontal = 1.dp,
                vertical = 5.dp
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
            label,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun RuneCompositionCanvas(
    runes: List<EditorRune>,
    selectedId: Long?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {

        val minSide =
            size.width.coerceAtMost(
                size.height
            )

        runes.forEach { rune ->

            /*
             * Это настоящий неподвижный центр
             * конкретной руны.
             */
            val centerX =
                size.width * rune.x

            val centerY =
                size.height * rune.y

            /*
             * Размер символа.
             */
            val glyphSize =
                minSide *
                .24f *
                rune.scale

            /*
             * Важно:
             * Canvas.rotate получает pivotX/pivotY.
             * Поэтому вращение происходит вокруг
             * центра самой руны, а не origin Canvas.
             */
            with(
                drawContext.canvas
                    .nativeCanvas
            ) {
                save()

                translate(
                    centerX,
                    centerY
                )

                rotate(
                    rune.rotation
                )

                scale(
                    if (rune.mirrorX) -1f
                    else 1f,
                    1f
                )

                /*
                 * Paint FontMetrics нужен,
                 * чтобы геометрический центр glyph
                 * совпадал с (0,0).
                 */
                val basePaint =
                    Paint().apply {
                        textSize = glyphSize
                        textAlign =
                            Paint.Align.CENTER
                        isAntiAlias = true
                    }

                val fm =
                    basePaint.fontMetrics

                val baseline =
                    -(
                        fm.ascent +
                        fm.descent
                    ) / 2f

                // мягкая глубокая тень
                drawText(
                    rune.symbol,
                    5f,
                    baseline + 7f,
                    Paint(basePaint).apply {
                        color =
                            android.graphics.Color
                                .rgb(
                                    35,
                                    18,
                                    3
                                )

                        setShadowLayer(
                            15f,
                            2f,
                            4f,
                            android.graphics.Color
                                .BLACK
                        )
                    }
                )

                // бронзовая глубина
                drawText(
                    rune.symbol,
                    2.8f,
                    baseline + 3.5f,
                    Paint(basePaint).apply {
                        color =
                            android.graphics.Color
                                .rgb(
                                    116,
                                    69,
                                    16
                                )
                    }
                )

                // основное золото
                drawText(
                    rune.symbol,
                    0f,
                    baseline,
                    Paint(basePaint).apply {
                        color =
                            android.graphics.Color
                                .rgb(
                                    215,
                                    166,
                                    61
                                )

                        setShadowLayer(
                            9f,
                            0f,
                            0f,
                            android.graphics.Color
                                .rgb(
                                    112,
                                    66,
                                    11
                                )
                        )
                    }
                )

                // верхний золотой блик
                drawText(
                    rune.symbol,
                    -1.4f,
                    baseline - 1.8f,
                    Paint(basePaint).apply {
                        color =
                            android.graphics.Color
                                .rgb(
                                    255,
                                    225,
                                    139
                                )

                        alpha = 155
                    }
                )

                restore()
            }

            if (rune.primary) {
                drawCircle(
                    color =
                        Color(0xFFD6A94C)
                            .copy(alpha = .16f),
                    center =
                        Offset(
                            centerX,
                            centerY
                        ),
                    radius =
                        glyphSize * .58f
                )
            }

            if (rune.id == selectedId) {
                drawCircle(
                    color =
                        Color(0xFFF6DA8A)
                            .copy(alpha = .68f),
                    center =
                        Offset(
                            centerX,
                            centerY
                        ),
                    radius =
                        glyphSize * .62f,
                    style =
                        Stroke(
                            width = 1.7f
                        )
                )

                drawCircle(
                    color =
                        Color(0xFFF6DA8A),
                    center =
                        Offset(
                            centerX,
                            centerY
                        ),
                    radius = 3.5f
                )
            }

            if (rune.locked) {
                drawCircle(
                    color =
                        Color(0xFFD6A94C),
                    center =
                        Offset(
                            centerX +
                            glyphSize * .54f,
                            centerY -
                            glyphSize * .54f
                        ),
                    radius = 4f
                )
            }
        }
    }
}
