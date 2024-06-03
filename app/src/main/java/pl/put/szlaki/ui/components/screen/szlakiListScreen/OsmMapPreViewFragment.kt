package pl.put.szlaki.ui.components.screen.szlakiListScreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import pl.put.szlaki.R
import pl.put.szlaki.domain.Szlak

@SuppressLint("ClickableViewAccessibility")
@Composable
fun OsmMapPreViewComponent(
    modifier: Modifier = Modifier,
    szlak: Szlak,
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = "MapApp"

    AndroidView(
        modifier = modifier.clip(RoundedCornerShape(2.dp)),
        factory = {
            MapView(context).apply {
                maxZoomLevel = 20.0
                minZoomLevel = 4.0
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
                setOnTouchListener { _, _ -> true }
                setUseDataConnection(true)

                this.overlays.add(
                    Polyline().apply {
                        outlinePaint.color = Color.RED
                        outlinePaint.strokeCap = Paint.Cap.ROUND
                        outlinePaint.isAntiAlias = true
                        outlinePaint.strokeWidth = 3f
                        setPoints(szlak.conertToGeoPoints(false))
                    },
                )
                this.overlays.add(
                    Polyline().apply {
                        outlinePaint.color = Color.BLACK
                        outlinePaint.strokeCap = Paint.Cap.ROUND
                        outlinePaint.isAntiAlias = true
                        outlinePaint.strokeWidth = 3f
                        setPoints(szlak.conertToGeoPoints(true))
                    },
                )
                szlak.bounds?.let {
                    this.controller.setCenter(it.getBoundsCenterGeoPoints())
                    this.controller.setZoom(it.getBoundsZoom(this))
                }
                if (szlak.punkty.isNotEmpty()) {
                    this.overlays.add(
                        Marker(this).apply {
                            position =
                                GeoPoint(
                                    szlak.punkty[0].coordinates.latitude,
                                    szlak.punkty[0].coordinates.longitude,
                                )
                            icon =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.baseline_place_24,
                                )?.apply {
                                    setTint(Color.GREEN)
                                }
                        },
                    )
                    this.overlays.add(
                        Marker(this).apply {
                            position =
                                GeoPoint(
                                    szlak.punkty[szlak.punkty.lastIndex].coordinates.latitude,
                                    szlak.punkty[szlak.punkty.lastIndex].coordinates.longitude,
                                )
                            icon =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.baseline_place_24,
                                )?.apply {
                                    setTint(Color.RED)
                                }
                        },
                    )
                }
            }
        },
    )
}
