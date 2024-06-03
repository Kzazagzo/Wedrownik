package pl.put.szlaki.domain

import android.os.Parcelable
import android.util.Half.toFloat
import kotlinx.parcelize.Parcelize
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import pl.put.szlaki.database.szlaki.SzlakEntity
import pl.put.szlaki.database.szlakiHistory.SzlakHistoryEntity
import pl.put.szlaki.ui.components.screen.szlakiItemScreen.TravelSpeedConverter
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.min

@Parcelize
data class Szlak(
    val name: String,
    val link: String?,
    val creationTime: String?,
    var bounds: Bounds?,
    var stopWatchTime: Long,
    val punkty: List<Punkt>,
    val szlakLength: Double?,
    val selectedSzlakDificulty: String,
) : Parcelable {
    @Parcelize
    data class Bounds(
        val maxlat: Double,
        val maxlon: Double,
        val minlat: Double,
        val minlon: Double,
    ) : Parcelable {
        fun asDatabaseModel(): SzlakEntity.BoundsEntity {
            return SzlakEntity.BoundsEntity(
                this.maxlat,
                this.maxlon,
                this.minlat,
                this.minlon,
            )
        }

        fun getBoundsCenterGeoPoints(): GeoPoint {
            return GeoPoint(((maxlat + minlat) / 2), ((maxlon + minlon) / 2))
        }

        fun getBoundsZoom(mapView: MapView): Double {
            val latitudeSpan = maxlat - minlat
            val longitudeSpan = maxlon - minlon
            return floor(min(log2(360 / latitudeSpan), log2(360 / longitudeSpan)))
        }
    }

    fun toHistoryAsDatabaseModel(): SzlakHistoryEntity {
        return SzlakHistoryEntity(
            name = this.name,
            travelTime = this.stopWatchTime,
            szlakLength = this.szlakLength ?: 0.0,
            selectedSzlakDificulty = this.selectedSzlakDificulty,
            compleatedPercentage = this.calculateRoadEta() / (this.szlakLength ?: 1.0),
        )
    }

    fun calculateBounds(): Bounds {
        return Bounds(
            maxlon = this.punkty.maxOf { it.coordinates.longitude },
            minlon = this.punkty.minOf { it.coordinates.longitude },
            maxlat = this.punkty.maxOf { it.coordinates.latitude },
            minlat = this.punkty.minOf { it.coordinates.latitude },
        )
    }

    fun asDatabaseModel(): SzlakEntity {
        return SzlakEntity(
            name = this.name,
            link = this.link,
            creationTime = this.creationTime,
            bounds =
                this.bounds?.let {
                    SzlakEntity.BoundsEntity(
                        maxlat = it.maxlat,
                        maxlon = it.maxlon,
                        minlat = it.minlat,
                        minlon = it.minlon,
                    )
                },
            stopWatchTime = stopWatchTime,
            selectedSzlakDificulty = selectedSzlakDificulty,
        )
    }

    fun conertToGeoPoints(visitedOnly: Boolean): List<GeoPoint> {
        return punkty.filter { it.visited == visitedOnly }.map {
            GeoPoint(
                it.coordinates.latitude,
                it
                    .coordinates
                    .longitude,
            )
        }
    }

    fun calculateProgress(): Float {
        return if (this.szlakLength != null && this.szlakLength > 0) {
            (calculateRoadEta().toFloat() / this.szlakLength.toFloat())
        } else {
            0f
        }
    }

    fun calculateRoadEta(): Double {
        return this.punkty.filter { it.visited }.sumOf { it.roadToNextPoint ?: 0.0 }
    }

    fun calculateETA(): Long {
        return this.punkty.sumOf {
            if (!it.visited) {
                TravelSpeedConverter.calculateTimeAssignSpeed(
                    this.selectedSzlakDificulty,
                    it.roadToNextPoint ?: 0.0,
                )
            } else {
                0L
            }
        }
    }

    fun resetSzlak() {
        this.punkty.forEach {
            it.visited = false
        }
        stopWatchTime = 0L
    }
}
