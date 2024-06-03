package pl.put.szlaki.ui.models

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.put.szlaki.data.repository.CategoryRepository
import pl.put.szlaki.domain.CategoryName
import pl.put.szlaki.domain.Szlak
import javax.inject.Inject

class CategoryScreenModel
    @Inject
    constructor(
        private val categoryRepository: CategoryRepository,
    ) : ScreenModel {
        private val _categoriesNames = MutableStateFlow<List<CategoryName>>(emptyList())
        val categoriesNames: StateFlow<List<CategoryName>> = _categoriesNames.asStateFlow()

        init {
            screenModelScope.launch {
                categoryRepository.getAlLCategoriesNames().collect {
                    _categoriesNames.value = it
                }
            }
        }

        suspend fun getMapOfBelongingCategories(szlak: Szlak): MutableMap<CategoryName, Boolean> {
            val belongingCategories = categoryRepository.getCategoriesOfSzlak(szlak)
            val belongingCategoryNames = belongingCategories.map { it.categoryName }
            return categoriesNames.value.associateWith {
                it.categoryName in
                    belongingCategoryNames
            }.toMutableMap()
        }

        fun addNewCategory(categoryName: String) =
            screenModelScope.launch {
                categoryRepository.insertCategory(categoryName)
            }

        fun insertSzlakCategoryJoin(
            szlak: Szlak,
            categoryName: CategoryName,
        ) = screenModelScope.launch {
            categoryRepository.insertSzlakCategoryJoin(szlak, categoryName)
        }

        fun deleteSzlakCategoryJoin(
            szlak: Szlak,
            categoryName: CategoryName,
        ) = screenModelScope.launch {
            categoryRepository.deleteSzlakCategoryJoin(szlak, categoryName)
        }

        fun editCategoryName(categoryName: CategoryName) {
            screenModelScope.launch {
                categoryRepository.updateCategory(categoryName)
            }
        }

        fun swapCategoriesIds(
            categoryName1: CategoryName,
            categoryName2: CategoryName,
        ) = screenModelScope.launch {
            categoryRepository.swapCategoriesIds(categoryName1, categoryName2)
        }

        fun deleteCategory(categoryName: CategoryName) {
            screenModelScope.launch {
                categoryRepository.deleteCategory(categoryName)
            }
        }
    }
