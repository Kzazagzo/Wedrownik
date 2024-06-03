package pl.put.szlaki.ui.components.screen.szlakiItemScreen

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.component
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import pl.put.szlaki.domain.Szlak
import pl.put.szlaki.ui.components.common.NamedDividerFragment
import pl.put.szlaki.util.TimeUtils

@SuppressLint("RestrictedApi")
@Composable
fun ChartSzlakFragment(
    modifier: Modifier = Modifier,
    szlak: Szlak,
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000),
        )
    }

    val colors = listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.primary)
    NamedDividerFragment(text = "Zmianna przewyższenia")
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    listOf(
                        rememberLineSpec(
                            shader =
                                TopBottomShader(
                                    DynamicShader.color(
                                        colors[0],
                                    ),
                                    DynamicShader.color(
                                        colors[1],
                                    ),
                                ),
                            backgroundShader =
                                TopBottomShader(
                                    DynamicShader.compose(
                                        DynamicShader.component(
                                            componentSize = 6.dp,
                                            component =
                                                rememberShapeComponent(
                                                    shape = Shape.Pill,
                                                    color = colors[0],
                                                    margins = Dimensions.of(1.dp),
                                                ),
                                        ),
                                        DynamicShader.verticalGradient(
                                            arrayOf(Color.Black, Color.Transparent),
                                        ),
                                        PorterDuff.Mode.DST_IN,
                                    ),
                                    DynamicShader.compose(
                                        DynamicShader.component(
                                            componentSize = 5.dp,
                                            component =
                                                rememberShapeComponent(
                                                    shape = Shape.Rectangle,
                                                    color = colors[1],
                                                    margins = Dimensions.of(horizontal = 2.dp),
                                                ),
                                            checkeredArrangement = false,
                                        ),
                                        DynamicShader.verticalGradient(
                                            arrayOf(Color.Transparent, Color.Black),
                                        ),
                                        PorterDuff.Mode.DST_IN,
                                    ),
                                ),
                        ),
                    ),
                ),
                startAxis =
                    rememberStartAxis(
                        label =
                            rememberAxisLabelComponent(
                                color = MaterialTheme.colorScheme.onBackground,
                                background =
                                    rememberShapeComponent(
                                        shape = Shape.Pill,
                                        color = Color.Transparent,
                                        strokeColor = MaterialTheme.colorScheme.outlineVariant,
                                        strokeWidth = 1.dp,
                                    ),
                                padding = Dimensions.of(horizontal = 6.dp, vertical = 2.dp),
                                margins = Dimensions.of(end = 8.dp),
                            ),
                        axis = null,
                        tick = null,
                        guideline =
                            rememberLineComponent(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape =
                                    remember {
                                        Shape.dashed(
                                            shape = Shape.Pill,
                                            dashLength = 4.dp,
                                            gapLength = 8.dp,
                                        )
                                    },
                            ),
                        itemPlacer = remember { AxisItemPlacer.Vertical.count(count = { 4 }) },
                        valueFormatter = { y, _, _ ->
                            "%.0f m".format(y)
                        },
                    ),
                bottomAxis =
                    rememberBottomAxis(
                        valueFormatter =
                            { x, _, _ ->
                                "%.2f km".format(x)
                            },
                        guideline = null,
                        itemPlacer =
                            remember {
                                AxisItemPlacer.Horizontal.default(
                                    spacing = (szlak.punkty.size / 10).toDouble().toInt(),
                                    addExtremeLabelPadding = true,
                                )
                            },
                    ),
            ),
        model =
            CartesianChartModel(
                LineCartesianLayerModel.build {
                    val x = mutableListOf<Double>() // Lista do przechowywania sum
                    var currentSum = 0.0

                    szlak.punkty.forEach {
                        currentSum += it.roadToNextPoint ?: 0.0
                        x.add(TimeUtils.roundNumber(currentSum))
                    }
                    series(
                        x = x,
                        y =
                            szlak.punkty.map {
                                szlak.punkty[0].elevation?.let { it1 ->
                                    it.elevation?.minus(it1)
                                } ?: 0
                            } as Collection<Number>,
                    )
                },
            ),
        modifier = Modifier,
        horizontalLayout = HorizontalLayout.fullWidth(),
        zoomState =
            rememberVicoZoomState(
                initialZoom = Zoom.Content,
            ),
    )
}
