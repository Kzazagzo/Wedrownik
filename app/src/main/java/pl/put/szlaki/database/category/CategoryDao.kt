package pl.put.szlaki.database.category

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categoriesWithSzlaki")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("UPDATE categoriesWithSzlaki SET categoryName = :categoryName WHERE id = :categoryId")
    suspend fun updateCategory(
        categoryId: Long,
        categoryName: String,
    )

    @Query("SELECT * FROM szlak_category_join WHERE szlakName = :szlakName")
    fun getCategoriesOfSzlaki(szlakName: String): Flow<List<SzlakCategoryJoin>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSzlakCategoryJoin(szlakCategoryJoin: SzlakCategoryJoin)

    @Query("DELETE FROM categoriesWithSzlaki WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM szlak_category_join WHERE categoryName = :categoryName")
    fun getCategoryContents(categoryName: String): Flow<List<SzlakCategoryJoin>>

    @Query("UPDATE categoriesWithSzlaki SET id = :newPosition WHERE id = :oldPosition")
    suspend fun updateCategoryPosition(
        oldPosition: Long,
        newPosition: Long,
    )

    @Transaction
    suspend fun swapCategoriesIds(
        categoryId1: Long,
        categoryId2: Long,
    ) {
        val tempPosition: Long = -1L
        updateCategoryPosition(categoryId1, tempPosition)
        updateCategoryPosition(categoryId2, categoryId1)
        updateCategoryPosition(tempPosition, categoryId2)
    }

    @Delete
    suspend fun deleteSzlakCategoryJoin(szlakCategoryJoin: SzlakCategoryJoin)

    @Query("UPDATE categoriesWithSzlaki SET id = :newId WHERE id = :oldId")
    suspend fun updateCategoryId(
        oldId: Long,
        newId: Long,
    )
}
