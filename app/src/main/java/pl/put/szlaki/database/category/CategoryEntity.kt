package pl.put.szlaki.database.category

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.put.szlaki.database.szlaki.SzlakEntity
import pl.put.szlaki.domain.CategoryName

@Entity(tableName = "categoriesWithSzlaki", indices = [Index(value = ["categoryName"], unique = true)])
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String,
)

@Entity(
    tableName = "szlak_category_join",
    primaryKeys = ["szlakName", "categoryName"],
    foreignKeys = [
        ForeignKey(
            entity = SzlakEntity::class,
            parentColumns = ["name"],
            childColumns = ["szlakName"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryName"],
            childColumns = ["categoryName"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["szlakName", "categoryName"], unique = true)],
)
data class SzlakCategoryJoin(
    val szlakName: String,
    val categoryName: String,
)

// fun <T> SzlakCategoryJoin.asDomainModel(elements: List<T>): Category<T> {
//    return Category(
//        categoryName = this.categoryName,
//        elementsInCategory = elements,
//    )
// }

fun CategoryEntity.asDomainModel(): CategoryName {
    return CategoryName(
        categoryName = this.categoryName,
        position = this.id,
    )
}
