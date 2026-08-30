package com.runemaster.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import kotlin.math.PI

data class EditorRune(
    val id: Long,
    val symbol: String,
    val name: String,
    val x: Float = 0.5f,
    val y: Float = 0.5f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val mirrorX: Boolean = false,
    val primary: Boolean = false
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
                    x = .5f,
                    y = .5f,
                    scale = 1.15f,
                    primary = true
                )
            )
        )
    }

    var selectedId by remember { mutableStateOf<Long?>(1) }
    var counter by remember { mutableLongStateOf(2) }

    val available = listOf(
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

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF080706))
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) {
                Text("‹ НАЗАД", color = Color(0xFFD6A94C))
            }

            Text(
                "RUNE EDITOR",
                color = Color(0xFFF6DA8A),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
        }

        Text(
            "Перемещайте, масштабируйте и вращайте выбранную руну",
            color = Color(0xFFBBAE8B),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(10.dp))

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
                Modifier
                    .fillMaxSize()
                    .pointerInput(selectedId, elements) {
                        detectTransformGestures {
                            centroid,
                            pan,
                            zoom,
                            rotation ->

                            val id = selectedId
                                ?: return@detectTransformGestures

                            val w = size.width.toFloat()
                            val h = size.height.toFloat()

                            elements = elements.map { rune ->
                                if (rune.id != id) rune
                                else rune.copy(
                                    x = (
                                        rune.x +
                                        pan.x / w
                                    ).coerceIn(0.05f, .95f),

                                    y = (
                                        rune.y +
                                        pan.y / h
                                    ).coerceIn(0.05f, .95f),

                                    scale = (
                                        rune.scale * zoom
                                    ).coerceIn(.3f, 4f),

                                    rotation =
                                        rune.rotation + rotation
                                )
                            }
                        }
                    }
            ) {
                RuneCompositionCanvas(
                    runes = elements,
                    selectedId = selectedId,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val selected =
            elements.find { it.id == selectedId }

        if (selected != null) {
            Text(
                "${selected.symbol} ${selected.name}",
                color = Color(0xFFF6DA8A),
                fontWeight = FontWeight.Bold
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {
                SmallEditorButton("↶") {
                    elements = elements.map {
                        if (it.id == selected.id)
                            it.copy(
                                rotation =
                                    it.rotation - 15f
                            )
                        else it
                    }
                }

                SmallEditorButton("↷") {
                    elements = elements.map {
                        if (it.id == selected.id)
                            it.copy(
                                rotation =
                                    it.rotation + 15f
                            )
                        else it
                    }
                }

                SmallEditorButton("⇋") {
                    elements = elements.map {
                        if (it.id == selected.id)
                            it.copy(
                                mirrorX = !it.mirrorX
                            )
                        else it
                    }
                }

                SmallEditorButton("180°") {
                    elements = elements.map {
                        if (it.id == selected.id)
                            it.copy(
                                rotation =
                                    it.rotation + 180f
                            )
                        else it
                    }
                }

                SmallEditorButton("★") {
                    elements = elements.map {
                        it.copy(
                            primary =
                                it.id == selected.id
                        )
                    }
                }

                SmallEditorButton("✕") {
                    elements =
                        elements.filter {
                            it.id != selected.id
                        }
                    selectedId =
                        elements.lastOrNull()?.id
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "ДОБАВИТЬ РУНУ",
            color = Color(0xFFD6A94C),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement =
                Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = available.size
            ) { index ->

                val rune = available[index]

                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF17130D)
                        )
                ) {
                    TextButton(
                        onClick = {
                            val item =
                                EditorRune(
                                    id = counter++,
                                    symbol = rune.first,
                                    name = rune.second,
                                    x = .5f,
                                    y = .5f,
                                    scale = .9f
                                )

                            elements =
                                elements + item

                            selectedId = item.id
                        }
                    ) {
                        Column {
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
}

@Composable
private fun RowScope.SmallEditorButton(
    label: String,
    action: () -> Unit
) {
    Button(
        onClick = action,
        modifier = Modifier.weight(1f),
        contentPadding =
            PaddingValues(
                horizontal = 2.dp,
                vertical = 6.dp
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2B2110),
            contentColor = Color(0xFFF6DA8A)
        )
    ) {
        Text(label, fontSize = 12.sp)
    }
}

@Composable
private fun RuneCompositionCanvas(
    runes: List<EditorRune>,
    selectedId: Long?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {

        runes.forEach { rune ->

            val center =
                Offset(
                    size.width * rune.x,
                    size.height * rune.y
                )

            withTransform({
                translate(
                    center.x,
                    center.y
                )

                rotate(rune.rotation)

                scale(
                    scaleX =
                        rune.scale *
                            if (rune.mirrorX)
                                -1f
                            else 1f,
                    scaleY = rune.scale
                )
            }) {

                val textSize =
                    size.minDimension * .22f

                // глубокая тень
                drawContext.canvas.nativeCanvas.drawText(
                    rune.symbol,
                    7f,
                    9f,
                    Paint().apply {
                        color =
                            android.graphics.Color
                                .rgb(25, 13, 2)
                        this.textSize = textSize
                        textAlign =
                            Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )

                // бронзовый объём
                drawContext.canvas.nativeCanvas.drawText(
                    rune.symbol,
                    4f,
                    5f,
                    Paint().apply {
                        color =
                            android.graphics.Color
                                .rgb(128, 76, 18)
                        this.textSize = textSize
                        textAlign =
                            Paint.Align.CENTER
                        isAntiAlias = true
                    }
                )

                // основное золото
                drawContext.canvas.nativeCanvas.drawText(
                    rune.symbol,
                    0f,
                    0f,
                    Paint().apply {
                        color =
                            android.graphics.Color
                                .rgb(214, 169, 76)
                        this.textSize = textSize
                        textAlign =
                            Paint.Align.CENTER
                        isAntiAlias = true
                        setShadowLayer(
                            12f,
                            0f,
                            0f,
                            android.graphics.Color
                                .rgb(120, 75, 15)
                        )
                    }
                )

                // световой блик
                drawContext.canvas.nativeCanvas.drawText(
                    rune.symbol,
                    -1.8f,
                    -2.2f,
                    Paint().apply {
                        color =
                            android.graphics.Color
                                .rgb(255, 226, 145)
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
                                .copy(alpha = .23f),
                        radius =
                            textSize * .54f
                    )
                }

                if (rune.id == selectedId) {
                    drawCircle(
                        color =
                            Color(0xFFF6DA8A)
                                .copy(alpha = .65f),
                        radius =
                            textSize * .62f,
                        style =
                            androidx.compose.ui
                                .graphics.drawscope
                                .Stroke(
                                    width = 1.5f
                                )
                    )
                }
            }
        }
    }
}
