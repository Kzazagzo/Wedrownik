package pl.put.szlaki.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistorizedSzlak(
    val name: String,
    val travelTime: Long,
    val szlakLength: Double,
    val selectedSzlakDificulty: String,
    val compleatedPercentage: Double,
) : Parcelable
