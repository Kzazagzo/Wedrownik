package pl.put.szlaki.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun startLocationUpdates()

    fun stopLocationUpdates()

    fun getCurrentLocationFlow(): Flow<Location?>
}
