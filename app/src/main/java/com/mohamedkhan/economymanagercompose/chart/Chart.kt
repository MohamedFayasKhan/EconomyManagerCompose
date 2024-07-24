package com.mohamedkhan.economymanagercompose.chart

import android.graphics.Paint
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarChart(data: List<Pair<String, Double>>, barWidth: Dp = 30.dp, barSpacing: Dp = 16.dp, maxBarHeight: Dp = 200.dp, verticalSplit: Int = 5, textHeight: Dp = 36.dp, chartHeight: Dp = 200.dp, barColor: Color = Color.Cyan, textColor: Color = Color.Black,  showLine: Boolean = false) {

    val verticalSpacing = maxBarHeight / verticalSplit
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 50.dp)
    ) {
        val maxValue = data.maxOfOrNull { it.second } ?: 0f
        val verticalInterval = maxValue.toDouble() / verticalSplit

        Box(
            modifier = Modifier
                .height(chartHeight),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .width(textHeight)
//                .background(Color.Red)
                    .height(maxBarHeight)
            ) {
                (0..verticalSplit).forEach { index ->
                    drawContext.canvas.nativeCanvas.drawText(
                        (maxValue.toDouble() - (verticalInterval * index)).toInt().toString(),
                        barSpacing.toPx(),
                        maxBarHeight.toPx() - textHeight.toPx() - ((maxBarHeight.toPx()) - (index * verticalSpacing.toPx())),
                        Paint().apply {
                            textSize = 12.sp.toPx()
                            color = textColor.hashCode()
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .height(chartHeight)
                .horizontalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
//                    .background(Color.Blue)
                    .width((data.size.dp * (barWidth + barSpacing).value))
                    .height(maxBarHeight)
            ) {
                val canvasHeight = size.height
                val canvasWidth = size.width
                val textPaint = TextPaint().apply {
                    textSize = 12.sp.toPx()
                    color = textColor.hashCode()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                if (showLine) {
                    (0..verticalSplit).forEach { index ->
                        drawLine(
                            color = Color.Black,
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.chainPathEffect(
                                outer = PathEffect.dashPathEffect(
                                    floatArrayOf(
                                        10.dp.toPx(),
                                        5.dp.toPx()
                                    ), 0f
                                ),
                                inner = PathEffect.cornerPathEffect(16.dp.toPx())
                            ),
                            start = Offset(
                                0f,
                                maxBarHeight.toPx() - textHeight.toPx() - ((maxBarHeight.toPx()) - (index * verticalSpacing.toPx()))
                            ),
                            end = Offset(
                                canvasWidth,
                                maxBarHeight.toPx() - textHeight.toPx() - ((maxBarHeight.toPx()) - (index * verticalSpacing.toPx()))
                            )
                        )
                    }
                }
                data.forEachIndexed { index, value ->
                    val barHeight = (value.second / maxValue.toDouble()) * canvasHeight
                    drawContext.canvas.nativeCanvas.drawText(
                        TextUtils.ellipsize(
                            value.first,
                            textPaint,
                            barWidth.toPx() * 2,
                            TextUtils.TruncateAt.END
                        ).toString(),
                        (index * (barWidth.toPx() * 2 + barSpacing.toPx())) + (textHeight.toPx()),
//                    index * (barWidth.toPx() + barSpacing.toPx()) + barWidth.toPx() / 2,
                        canvasHeight - (textHeight.toPx() * 0.5f),
                        textPaint
                    )
                    drawRoundRect(
                        color = barColor,
                        size = Size(
                            width = barWidth.toPx(),
                            height = barHeight.dp.toPx()
                        ),
                        cornerRadius = CornerRadius(8f, 8f),
                        topLeft = Offset(
                            x = (index * (barWidth.toPx() * 2 + barSpacing.toPx())) + (textHeight.toPx() * 0.5f),
                            y = canvasHeight - barHeight.dp.toPx() - textHeight.toPx()
                        )
                    )
                }
            }
        }
    }
}