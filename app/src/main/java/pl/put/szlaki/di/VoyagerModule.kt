package pl.put.szlaki.di

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import pl.put.szlaki.ui.models.CategoryScreenModel
import pl.put.szlaki.ui.models.HistoryScreenModel
import pl.put.szlaki.ui.models.SettingsScreenModel
import pl.put.szlaki.ui.models.SzlakiScreenModel
import javax.inject.Singleton

// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
//                                      NIE USUWAĆ                                                //
// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
@Module
@InstallIn(SingletonComponent::class)
object AppContext {
    @Provides
    @Singleton
    fun provideAppContext(
        @ApplicationContext appContext: Context,
    ): Context {
        return appContext
    }
}

// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
//                                      NIE USUWAĆ                                                //
// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
@Module
@InstallIn(ActivityComponent::class)
abstract class VoyagerModule {
    @Binds
    @IntoMap
    @ScreenModelKey(SzlakiScreenModel::class)
    abstract fun uno(szlakiScreenModel: SzlakiScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SettingsScreenModel::class)
    abstract fun dos(settingsScreenModel: SettingsScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(CategoryScreenModel::class)
    abstract fun tres(categoryScreenModel: CategoryScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(HistoryScreenModel::class)
    abstract fun quatro(historyScreenModel: HistoryScreenModel): ScreenModel
}
// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
//                                      NIE USUWAĆ                                                //
// BOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻEBOŻE
