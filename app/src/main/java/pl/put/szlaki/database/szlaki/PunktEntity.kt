package pl.put.szlaki.database.szlaki

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.put.szlaki.domain.Punkt

@Entity(
    tableName = "punkty",
    foreignKeys = [
        ForeignKey(
            entity = SzlakEntity::class,
            parentColumns = ["name"],
            childColumns = ["szlakName"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["szlakName"])],
)
data class PunktEntity(
    val szlakName: String,
    @PrimaryKey(autoGenerate = true)
    val pointNumber: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double?,
    val time: String?,
    val roadToNextPoint: Double?,
    val visited: Boolean,
)

fun PunktEntity.asDomainModel(): Punkt {
    return Punkt(
        szlakName = this.szlakName,
        pointNumber = this.pointNumber,
        coordinates =
            Punkt.Coordinates(
                latitude = this.latitude,
                longitude = this.longitude,
            ),
        elevation = this.elevation,
        time = this.time,
        roadToNextPoint = this.roadToNextPoint,
        visited = this.visited,
    )
}

fun List<PunktEntity>.asDomainModel(): List<Punkt> {
    return map { it.asDomainModel() }
}
