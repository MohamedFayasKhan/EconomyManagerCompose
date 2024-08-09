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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedkhan.economymanagercompose.ui.theme.white

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


@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius:Float = 300f,
    innerRadius:Float = 150f,
    transparentWidth:Float = 35f,
    input:List<PieChartInput>,
    centerText:String = ""
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
//                .fillMaxSize()
                .pointerInput(true){}
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x= width/2f,y= height/2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            input.forEach { pieChartInput ->
                val scale = if(pieChartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale){
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height - radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle-angleToDraw/2f-90f
                var factor = 1f
                if(rotateAngle>90f){
                    rotateAngle = (rotateAngle+180).mod(360f)
                    factor = -0.92f
                }
                val percentage = (pieChartInput.value/totalValue.toFloat()*100).toInt()

                drawContext.canvas.nativeCanvas.apply {
                    if(percentage>3){
                        rotate(rotateAngle){
                            drawMultilineText(
                                "${pieChartInput.description}\n$percentage %",
                                circleCenter.x,
                                circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                                Paint().apply {
                                    textSize = 13.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = white.toArgb()
                                }
                            )
                        }
                    }
                }
//                return@Canvas
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = white.copy(alpha = 0.6f).toArgb()
                        setShadowLayer(10f,0f,0f, Color.Gray.toArgb())
                    }
                )
            }

            drawCircle(
                color = white.copy(0.2f),
                radius = innerRadius+transparentWidth/2f
            )
        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius/1.5f))
                .padding(4.dp),
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class PieChartInput(
    val color:Color,
    val value:Int,
    val description:String,
    val isTapped:Boolean = false
)

private fun DrawScope.drawMultilineText(
    text: String,
    x: Float,
    y: Float,
    paint: Paint
) {
    val lines = text.split("\n")
    lines.forEachIndexed { index, line ->
        drawContext.canvas.nativeCanvas.drawText(
            line,
            x,
            y + index * paint.textSize,
            paint
        )
    }
}