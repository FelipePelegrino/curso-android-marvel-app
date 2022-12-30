package com.example.core.data.mapper

import com.example.core.data.StorageConstants
import com.example.core.domain.model.SortingType
import javax.inject.Inject

class SortingMapper @Inject constructor() {

    private val nameAscending =
        SortingType.ORDER_BY_NAME.value to SortingType.ORDER_ASCENDING.value
    private val nameDescending =
        SortingType.ORDER_BY_NAME.value to SortingType.ORDER_DESCENDING.value
    private val modifiedAscending =
        SortingType.ORDER_BY_MODIFIED.value to SortingType.ORDER_ASCENDING.value
    private val modifiedDescending =
        SortingType.ORDER_BY_MODIFIED.value to SortingType.ORDER_DESCENDING.value

    fun mapToPair(sorting: String): Pair<String, String> {
        return when (sorting) {
            StorageConstants.ORDER_BY_NAME_ASCENDING -> nameAscending
            StorageConstants.ORDER_BY_NAME_DESCENDING -> nameDescending
            StorageConstants.ORDER_BY_MODIFIED_ASCENDING -> modifiedAscending
            StorageConstants.ORDER_BY_MODIFIED_DESCENDING -> modifiedDescending
            else -> nameAscending
        }
    }

    fun mapFromPair(sortingPair: Pair<String, String>): String {
        return when (sortingPair) {
            nameAscending -> StorageConstants.ORDER_BY_NAME_ASCENDING
            nameDescending -> StorageConstants.ORDER_BY_NAME_DESCENDING
            modifiedAscending -> StorageConstants.ORDER_BY_MODIFIED_ASCENDING
            modifiedDescending -> StorageConstants.ORDER_BY_MODIFIED_DESCENDING
            else -> StorageConstants.ORDER_BY_NAME_ASCENDING
        }
    }
}