package pl.put.szlaki.ui.components.screen.szlakiItemScreen

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import pl.put.szlaki.ui.models.SzlakiScreenModel

@Composable
fun OsmMapViewComponent(
    modifier: Modifier = Modifier,
    screenModel: SzlakiScreenModel,
    szlak: Szlak,
    eventFlow: Boolean,
) {
    val context = LocalContext.current

    LaunchedEffect(eventFlow) {
        // Nic nie robi, wymusza rekompozycję
    }

    val mapViewFactory =
        remember {
            MapView(context).apply {
                maxZoomLevel = 20.0
                minZoomLevel = 4.0
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                isHorizontalMapRepetitionEnabled = false
                isVerticalMapRepetitionEnabled = false
                setMultiTouchControls(true)
                setUseDataConnection(true)
            }
        }

    val location by screenModel.currentLocation.collectAsState()

//    DisposableEffect(Unit) {
//        onDispose {
//            screenModel.stopLocationUpdates()
//        }
//    }

    Configuration.getInstance().userAgentValue = "MapApp"
    val userColor = MaterialTheme.colorScheme.primary.toArgb()

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapViewFactory },
        update = { mapViewFactory ->
            mapViewFactory.overlays.add(
                Polyline().apply {
                    outlinePaint.color = Color.RED
                    outlinePaint.strokeCap = Paint.Cap.ROUND
                    outlinePaint.isAntiAlias = true
                    outlinePaint.strokeWidth = 3f
                    setPoints(szlak.conertToGeoPoints(false))
                },
            )
            mapViewFactory.overlays.add(
                Polyline().apply {
                    outlinePaint.color = Color.BLACK
                    outlinePaint.strokeCap = Paint.Cap.ROUND
                    outlinePaint.isAntiAlias = true
                    outlinePaint.strokeWidth = 3f
                    setPoints(szlak.conertToGeoPoints(true))
                },
            )
            szlak.bounds?.let {
                val center =
                    it.getBoundsCenterGeoPoints()

                mapViewFactory.controller.setCenter(
                    GeoPoint(
                        center.latitude,
                        center
                            .longitude,
                    ),
                )
                mapViewFactory.controller.setZoom(it.getBoundsZoom(mapViewFactory))
            }

            mapViewFactory.overlays.add(
                Marker(mapViewFactory).apply {
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
            mapViewFactory.overlays.add(
                Marker(mapViewFactory).apply {
                    position =
                        GeoPoint(
                            szlak.punkty.get(szlak.punkty.lastIndex).coordinates.latitude,
                            szlak.punkty.get(szlak.punkty.lastIndex).coordinates.longitude,
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

            location.let {
                if (it != null) {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    mapViewFactory.overlays.clear()
                    mapViewFactory.controller.setCenter(geoPoint)
                    mapViewFactory.overlays.add(
                        Marker(mapViewFactory).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.twotone_navigation_24,
                                )?.apply {
                                    setTint(userColor)
                                }
                            title = "Your Location"
                        },
                    )
                }
            }

            mapViewFactory.invalidate()
        },
    )
}
