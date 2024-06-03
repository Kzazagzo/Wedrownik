package pl.put.szlaki.location

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DefaultLocationClient(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: Application,
) : LocationClient {
    private val locationFlow = MutableSharedFlow<Location?>(replay = 1)
    private var locationCallback: LocationCallback? = null

    override fun getCurrentLocationFlow() = locationFlow.asSharedFlow()

    override fun startLocationUpdates() {
        val locationRequest =
            LocationRequest.Builder(10000L)
                .setIntervalMillis(5000)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .build()

        locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let {
                        locationFlow.tryEmit(it)
                    }
                }
            }

        if (ContextCompat.checkSelfPermission(
                application,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val task: Task<Void> =
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback as LocationCallback,
                    null,
                )
            task.addOnSuccessListener {
            }.addOnFailureListener { _ ->
                locationFlow.tryEmit(null)
            }
        }
    }

    override fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationProviderClient.removeLocationUpdates(it)
        }
    }
}
