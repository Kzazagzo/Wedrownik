package pl.put.szlaki.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.database.szlaki.PunktEntity
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Parcelize
data class Punkt(
    // Bo przypisywaniem zajmuje się db
    val szlakName: String,
    val pointNumber: Long,
    val coordinates: Coordinates,
    val elevation: Double?,
    val time: String?,
    var roadToNextPoint: Double?,
    var visited: Boolean,
) : Parcelable {
    @Parcelize
    data class Coordinates(
        val latitude: Double,
        val longitude: Double,
    ) : Parcelable

    fun asDatabaseModel(): PunktEntity {
        return PunktEntity(
            szlakName = this.szlakName,
            latitude = this.coordinates.latitude,
            longitude = this.coordinates.longitude,
            elevation = this.elevation,
            time = this.time,
            roadToNextPoint = this.roadToNextPoint,
            visited = this.visited,
        )
    }

    fun calculateDistanceBetweenOtherPoint(point: Punkt): Double {
        val lat1 = Math.toRadians(this.coordinates.latitude)
        val lat2 = Math.toRadians(point.coordinates.latitude)
        val lon1 = Math.toRadians(this.coordinates.longitude)
        val lon2 = Math.toRadians(point.coordinates.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2f).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2f).pow(2)
        val distance = 6371.0f * 2f * asin(sqrt(a))
        val dEle = this.elevation?.let { point.elevation?.minus(it) }
        val elevationDifference = dEle?.let { it / 1000.0f } ?: 0.0
        return sqrt(distance.pow(2) + (elevationDifference.pow(2)))
    }

    fun calculateBearing(punkt: Punkt): Float {
        val dLon = Math.toRadians(punkt.coordinates.longitude - this.coordinates.longitude)
        val y = sin(dLon) * cos(Math.toRadians(punkt.coordinates.latitude))
        val x =
            cos(Math.toRadians(this.coordinates.latitude)) * sin(Math.toRadians(punkt.coordinates.latitude)) -
                sin(Math.toRadians(this.coordinates.latitude)) *
                cos(
                    Math.toRadians(
                        punkt
                            .coordinates
                            .latitude,
                    ),
                ) * cos(dLon)
        val bearing = atan2(y, x)
        return Math.toDegrees(bearing).toFloat()
    }
}
