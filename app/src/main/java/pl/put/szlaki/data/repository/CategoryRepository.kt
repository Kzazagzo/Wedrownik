package pl.put.szlaki.data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pl.put.szlaki.database.AppDatabase
import pl.put.szlaki.database.category.CategoryEntity
import pl.put.szlaki.database.category.SzlakCategoryJoin
import pl.put.szlaki.database.category.asDomainModel
import pl.put.szlaki.database.szlaki.SzlakEntity
import pl.put.szlaki.database.szlaki.asDomainModel
import pl.put.szlaki.domain.Category
import pl.put.szlaki.domain.CategoryName
import pl.put.szlaki.domain.Szlak
import javax.inject.Inject

class CategoryRepository
    @Inject
    constructor(
        private val appDatabase: AppDatabase,
    ) {
        val categoriesWithSzlaki: Flow<List<Category<Szlak>>> =
            flow {
                val categoriesFlow = appDatabase.categoryDao.getAllCategories()
                val szlakiWithoutCategoryFlow =
                    appDatabase.szlakiTurystyczneDao.getSzlakiWithoutCategory()

                combine(
                    categoriesFlow,
                    szlakiWithoutCategoryFlow,
                ) { categories, szlakiWithoutCategory ->
                    val result = mutableListOf<Category<SzlakEntity>>()

                    if (szlakiWithoutCategory.isNotEmpty() || categoriesFlow.first().isEmpty()) {
                        result.add(Category("Domyślna kategoria", -1L, szlakiWithoutCategory))
                    }

                    categories.forEach { category ->
                        val contents =
                            getCategoryContents(category).map {
                                it.map {
                                    appDatabase.szlakiTurystyczneDao.getSzlakByName(it.szlakName)
                                }
                            }.first()

                        result.add(Category(category.categoryName, category.id, contents))
                    }

                    result.map { category ->
                        val enrichedContents =
                            category.elementsInCategory?.map {
                                var a = appDatabase.szlakiTurystyczneDao.getPunktyFromSzlakByName(it.name)
                                while (a.isEmpty()) {
                                    a = appDatabase.szlakiTurystyczneDao.getPunktyFromSzlakByName(it.name)
                                }
                                it.asDomainModel(
                                    a.asDomainModel(),
                                )
                            } ?: emptyList()
                        Category(category.categoryName, category.position, enrichedContents)
                    }
                }.collect {
                    emit(it)
                }
            }

        fun getAlLCategoriesNames(): Flow<List<CategoryName>> {
            return appDatabase.categoryDao.getAllCategories().map { entities ->
                entities.map { it.asDomainModel() }
            }
        }

        @WorkerThread
        suspend fun insertSzlakCategoryJoin(
            szlak: Szlak,
            category: CategoryName,
        ) {
            appDatabase.categoryDao.insertSzlakCategoryJoin(
                SzlakCategoryJoin(
                    szlak.name,
                    category.categoryName,
                ),
            )
        }

        @WorkerThread
        suspend fun deleteSzlakCategoryJoin(
            szlak: Szlak,
            category: CategoryName,
        ) {
            appDatabase.categoryDao.deleteSzlakCategoryJoin(
                SzlakCategoryJoin(
                    szlak.name,
                    category.categoryName,
                ),
            )
        }

        @WorkerThread
        suspend fun getCategoriesOfSzlak(szlak: Szlak): List<SzlakCategoryJoin> {
            return appDatabase.categoryDao.getCategoriesOfSzlaki(szlak.name).first()
        }

        @WorkerThread
        suspend fun updateCategory(category: CategoryName) {
            appDatabase.categoryDao.updateCategory(category.position, category.categoryName)
        }

        @WorkerThread
        suspend fun deleteCategory(category: CategoryName) {
            appDatabase.categoryDao.deleteCategory(category.position)
        }

        @WorkerThread
        suspend fun insertCategory(category: String) {
            appDatabase.categoryDao.insertCategory(CategoryEntity(categoryName = category))
        }

        @WorkerThread
        suspend fun swapCategoriesIds(
            category1: CategoryName,
            category2: CategoryName,
        ) {
            appDatabase.categoryDao.swapCategoriesIds(category1.position, category2.position)
        }

        @WorkerThread
        private fun getCategoryContents(category: CategoryEntity): Flow<List<SzlakCategoryJoin>> {
            return appDatabase.categoryDao.getCategoryContents(category.categoryName)
        }
    }
