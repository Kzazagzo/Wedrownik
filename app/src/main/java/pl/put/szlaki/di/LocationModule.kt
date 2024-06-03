package pl.put.szlaki.di

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.put.szlaki.location.DefaultLocationClient
import pl.put.szlaki.location.LocationClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun provideLocationClient(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application,
    ): LocationClient =
        DefaultLocationClient(
            fusedLocationProviderClient = fusedLocationProviderClient,
            application = application,
        )
}
