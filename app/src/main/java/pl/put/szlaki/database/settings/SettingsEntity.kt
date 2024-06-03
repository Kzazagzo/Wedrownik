package pl.put.szlaki.database.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import pl.put.szlaki.domain.Setting

enum class SettingType {
    STRING,
    INT,
    BOOL,
}

@Entity(tableName = "settings")
@TypeConverters(SettingsConverter::class)
data class SettingsEntity(
    @PrimaryKey
    val settingName: String,
    val settingValue: String,
    val type: String,
    val typeDetails: String?,
)

class SettingsConverter {
    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromDetails(value: Any?): String? {
        return value?.let {
            when (it) {
                is List<*> -> {
                    moshi.adapter<List<String>>(
                        Types.newParameterizedType(List::class.java, String::class.java),
                    ).toJson(it.filterIsInstance<String>())
                }
                is IntRange -> {
                    moshi.adapter(IntRange::class.java).toJson(it)
                }
                else -> null
            }
        }
    }

    @TypeConverter
    fun toDetails(value: String?): Any? {
        return value?.let {
            try {
                // yyy to jest legalne?
                val adapter = moshi.adapter<Map<String, Any>>(Map::class.java)
                val map = adapter.fromJson(value)
                when (map?.get("type")) {
                    "STRING" -> {
                        moshi.adapter<List<String>>(
                            Types.newParameterizedType(List::class.java, String::class.java),
                        ).fromJson(value)
                    }

                    "INT " -> {
                        moshi.adapter(IntRange::class.java).fromJson(value)
                    }

                    else -> null
                }
            } catch (e: Exception) {
                throw Exception("MOSHI MOSHI")
            }
        }
    }
}

fun SettingsEntity.asDomainModel(): Setting<*, *> {
    val detailsObject = SettingsConverter().toDetails(typeDetails)
    return when (SettingType.valueOf(type)) {
        SettingType.STRING ->
            Setting(
                settingName = settingName,
                settingValue = settingValue,
                typeDetails = detailsObject as? List<String>,
            )
        SettingType.INT ->
            Setting(
                settingName = settingName,
                settingValue = settingValue.toInt(),
                typeDetails = detailsObject as? IntRange,
            )
        SettingType.BOOL ->
            Setting(
                settingName = settingName,
                settingValue = settingValue.toBoolean(),
                typeDetails = null,
            )
    }
}
