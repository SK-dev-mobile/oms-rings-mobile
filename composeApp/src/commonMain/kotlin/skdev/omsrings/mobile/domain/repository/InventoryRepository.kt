package skdev.omsrings.mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult


interface InventoryRepository {
    fun getFoldersAndItems(): Flow<List<Folder>>
    suspend fun addFolder(folder: Folder)
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folderId: String)
    suspend fun addInventoryItem(folderId: String, item: InventoryItem)
    suspend fun updateInventoryItem(folderId: String, item: InventoryItem)
    suspend fun deleteInventoryItem(folderId: String, itemId: String)
    suspend fun getInventoryItemsByIds(ids: List<String>): DataResult<List<InventoryItem>, DataError>
}