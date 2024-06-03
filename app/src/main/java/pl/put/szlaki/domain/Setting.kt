package pl.put.szlaki.domain

import android.os.Parcelable
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize
import pl.put.szlaki.database.settings.SettingType
import pl.put.szlaki.database.settings.SettingsEntity

@Parcelize
data class Setting<T, U>(
    val settingName: String,
    val settingValue: @RawValue T,
    val typeDetails: @RawValue U,
) : Parcelable

fun Setting<*, *>.asDatabaseModel(): SettingsEntity {
    return SettingsEntity(
        settingName = this.settingName,
        settingValue = this.settingValue.toString(),
        type =
            when (settingValue) {
                is String -> SettingType.STRING
                is Int -> SettingType.INT
                is Boolean -> SettingType.BOOL
                else -> throw IllegalArgumentException("AAAAAA")
            }.toString(),
        typeDetails = typeDetails.toString(),
    )
}
