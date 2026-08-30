package com.runemaster.app

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/*
 * Собственные начертания RuneMaster.
 *
 * FEHU и SOWILO зафиксированы по предоставленным
 * пользователем визуальным референсам.
 */

fun DrawScope.drawCustomRune(
    runeName: String,
    sizePx: Float,
    color: Color,
    strokeWidth: Float
): Boolean {

    when (runeName) {

        "FEHU" -> {

            /*
             * FEHU
             *
             *     |   /
             *     |  /
             *     | /
             *     |/
             *     |   /
             *     |  /
             *     | /
             *     |/
             *     |
             *     |
             */

            val w =
                strokeWidth * .82f

            // Основной вертикальный ствол.
            drawLine(
                color = color,
                start = Offset(
                    -sizePx * .15f,
                    -sizePx * .50f
                ),
                end = Offset(
                    -sizePx * .15f,
                    sizePx * .50f
                ),
                strokeWidth = w,
                cap = StrokeCap.Square
            )

            // Верхний луч.
            // От ствола справа-вверх.
            drawLine(
                color = color,
                start = Offset(
                    -sizePx * .15f,
                    -sizePx * .25f
                ),
                end = Offset(
                    sizePx * .32f,
                    -sizePx * .48f
                ),
                strokeWidth = w,
                cap = StrokeCap.Square
            )

            // Нижний луч.
            // Полностью параллелен верхнему.
            drawLine(
                color = color,
                start = Offset(
                    -sizePx * .15f,
                    sizePx * .05f
                ),
                end = Offset(
                    sizePx * .32f,
                    -sizePx * .18f
                ),
                strokeWidth = w,
                cap = StrokeCap.Square
            )

            return true
        }

        "SOWILO" -> {

            /*
             * SOWILO по референсу:
             *
             *                 ●
             *               /
             *             /
             *           /
             *     ●────●
             *       /
             *     /
             *   /
             * ●
             *
             * Три сегмента:
             *
             * нижний:
             * слева-снизу → справа-вверх
             *
             * средний:
             * справа → влево (почти горизонтально)
             *
             * верхний:
             * слева-снизу → справа-вверх
             */

            val path =
                Path().apply {

                    // Нижняя левая точка
                    moveTo(
                        -sizePx * .32f,
                        sizePx * .48f
                    )

                    // Нижняя длинная диагональ
                    lineTo(
                        sizePx * .27f,
                        sizePx * .02f
                    )

                    // Средний горизонтальный участок
                    lineTo(
                        -sizePx * .29f,
                        sizePx * .01f
                    )

                    // Верхняя длинная диагональ
                    lineTo(
                        sizePx * .29f,
                        -sizePx * .48f
                    )
                }

            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width =
                        strokeWidth * .92f,
                    cap =
                        StrokeCap.Round,
                    join =
                        StrokeJoin.Round
                )
            )

            return true
        }

        else -> return false
    }
}
