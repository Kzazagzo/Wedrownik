package pl.put.szlaki.database.szlakiHistory

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.put.szlaki.domain.HistorizedSzlak

@Entity(tableName = "szlak_history")
data class SzlakHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val travelTime: Long,
    val szlakLength: Double,
    val selectedSzlakDificulty: String,
    val compleatedPercentage: Double,
) {
    fun asDomainModel(): HistorizedSzlak {
        return HistorizedSzlak(
            name = this.name,
            travelTime = this.travelTime,
            szlakLength = this.szlakLength,
            selectedSzlakDificulty = this.selectedSzlakDificulty,
            compleatedPercentage = this.compleatedPercentage,
        )
    }
}
