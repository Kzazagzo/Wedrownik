package pl.put.szlaki.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import pl.put.szlaki.database.category.CategoryEntity

@Parcelize
data class Category<T>(
    val categoryName: String,
    val position: Long,
    val elementsInCategory: @RawValue List<T>?,
) : Parcelable

@Parcelize
data class CategoryName(
    var categoryName: String,
    val position: Long,
) : Parcelable

fun Category<*>.asDatabaseModel(): CategoryEntity {
    return CategoryEntity(
        categoryName = this.categoryName,
    )
}
