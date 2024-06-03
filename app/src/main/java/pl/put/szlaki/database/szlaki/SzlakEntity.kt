package pl.put.szlaki.database.szlaki

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.put.szlaki.domain.Punkt
import pl.put.szlaki.domain.Szlak
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Entity(tableName = "szlaki", indices = [Index(value = ["name"], unique = true)])
data class SzlakEntity(
    @PrimaryKey
    val name: String,
    val link: String?,
    val creationTime: String?,
    @Embedded
    val bounds: BoundsEntity?,
    val stopWatchTime: Long?,
    val selectedSzlakDificulty: String,
) {
    data class BoundsEntity(
        val maxlat: Double,
        val maxlon: Double,
        val minlat: Double,
        val minlon: Double,
    )
}

fun calculateSzlakLength(punkty: List<Punkt>): Double {
    if (punkty.any { it.roadToNextPoint == null } or
        punkty.all {
            it
                .roadToNextPoint == 0.0
        }
    ) {
        punkty.windowed(2) { (p1, p2) ->
            val lat1 = Math.toRadians(p1.coordinates.latitude)
            val lat2 = Math.toRadians(p2.coordinates.latitude)
            val lon1 = Math.toRadians(p1.coordinates.longitude)
            val lon2 = Math.toRadians(p2.coordinates.longitude)

            val dLat = lat2 - lat1
            val dLon = lon2 - lon1

            val a = sin(dLat / 2f).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2f).pow(2)
            val distance = 6371.0f * 2f * asin(sqrt(a))
            val dEle = p1.elevation?.let { p2.elevation?.minus(it) }
            val elevationDifference = dEle?.let { it / 1000.0f } ?: 0.0
            p2.roadToNextPoint = sqrt(distance.pow(2) + (elevationDifference.pow(2)))
        }
    }
    return punkty.sumOf { it.roadToNextPoint ?: 0.0 }
}

fun SzlakEntity.asDomainModel(punkty: List<Punkt>): Szlak {
    return Szlak(
        name = this.name,
        link = this.link,
        creationTime = this.creationTime,
        bounds =
            this.bounds?.let { _ ->
                Szlak.Bounds(
                    maxlat = this.bounds.maxlat,
                    maxlon = this.bounds.maxlon,
                    minlat = this.bounds.minlat,
                    minlon = this.bounds.minlon,
                )
            },
        stopWatchTime = this.stopWatchTime ?: 0L,
        punkty = punkty,
        szlakLength = calculateSzlakLength(punkty),
        selectedSzlakDificulty = selectedSzlakDificulty,
    )
}
