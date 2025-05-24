package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.code
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseInventoryRepository(
    private val firestore: FirebaseFirestore
) : BaseRepository, InventoryRepository {

    private val foldersCollection = firestore.collection("folders")


    override fun getFoldersAndItems(): Flow<List<Folder>> =
        foldersCollection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { it.data { Folder.serializer() } }
        }


    override suspend fun addFolder(folder: Folder) {
        foldersCollection.document(folder.id).set(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        foldersCollection.document(folder.id).set(folder)
    }

    override suspend fun deleteFolder(folderId: String) {
        foldersCollection.document(folderId).delete()
    }

    override suspend fun addInventoryItem(folderId: String, item: InventoryItem) {
        updateFolderItems(folderId) { it.addItem(item) }
    }

    override suspend fun updateInventoryItem(folderId: String, item: InventoryItem) {
        updateFolderItems(folderId) { it.updateItem(item) }
    }

    override suspend fun deleteInventoryItem(folderId: String, itemId: String) {
        updateFolderItems(folderId) { it.removeItem(itemId) }
    }

    private suspend fun updateFolderItems(folderId: String, update: (Folder) -> Folder) {
        val folderRef = foldersCollection.document(folderId)
        val folder = folderRef.get().data<Folder>()
        val updatedFolder = update(folder)
        folderRef.set(updatedFolder)
    }

    override suspend fun getInventoryItemsByIds(ids: List<String>): DataResult<List<InventoryItem>, DataError> =
        withCathing {
            val items = mutableListOf<InventoryItem>()
            val uniqueIds = ids.toSet()
            Napier.d(tag = TAG) { "Поиск товаров с ID: $uniqueIds" }

            // Fetch all folders
            val foldersSnapshot = foldersCollection.get().documents

            // Iterate through each folder
            for (folderDoc in foldersSnapshot) {
                val folder = folderDoc.data<Folder>()

                // Filter items in this folder that match the requested ids
                val matchingItems = folder.inventoryItems.filter { it.id in uniqueIds }
                items.addAll(matchingItems)

                // If we've found all requested items, we can stop searching
                if (items.size == uniqueIds.size) break
            }

            // Check if we found all requested items
            if (items.size < uniqueIds.size) {
                val missingIds = ids - items.map { it.id }.toSet()
                Napier.e(tag = TAG) { "Не удалось найти товары с ID: $missingIds" }
                DataResult.error(DataError.InventoryItem.NOT_FOUND)
            } else {
                Napier.d(tag = TAG) { "Найденные товары: $items" }
                DataResult.Success(items)
            }

        }

    /**
     * Обновляет количество нескольких товаров в инвентаре одновременно.
     *
     * Эта функция используется для массового обновления остатков товаров, например, при закрытии заказа.
     * Она выполняет все обновления в рамках одной транзакции Firebase, что гарантирует атомарность операции
     * и предотвращает возникновение ошибок при одновременном доступе к данным.
     *
     * @param items Список пар, где каждая пара содержит ID товара и количество, которое нужно вычесть из текущего остатка.
     * @return [DataResult.Success] с [Unit], если все обновления прошли успешно,
     *         или [DataResult.Error] с соответствующей ошибкой, если что-то пошло не так.
     *
     * Пример использования:
     * ```
     * val items = listOf("itemUUID1" to 2, "itemUUID2" to 1)
     * val result = inventoryRepository.updateMultipleInventoryItems(items)
     * when (result) {
     *     is DataResult.Success -> println("Остатки товаров успешно обновлены")
     *     is DataResult.Error -> println("Ошибка при обновлении остатков: ${result.error}")
     * }
     * ```
     *
     * @throws IllegalStateException если какой-либо из указанных товаров не найден в инвентаре.
     */
    override suspend fun updateMultipleInventoryItems(items: List<Pair<String, Int>>): DataResult<Unit, DataError> {
        return try {
            firestore.runTransaction {
                items.forEach { (itemId, quantityChange) ->
                    var itemUpdated = false
                    foldersCollection.get().documents.forEach { folderDoc ->
                        val folder = folderDoc.data<Folder>()
                        val updatedItems = folder.inventoryItems.map { item ->
                            if (item.id == itemId) {
                                itemUpdated = true
                                item.copy(
                                    stockQuantity = (item.stockQuantity - quantityChange).coerceAtLeast(
                                        0
                                    )
                                )
                            } else {
                                item
                            }
                        }
                        if (itemUpdated) {
                            set(folderDoc.reference, folder.copy(inventoryItems = updatedItems))
                            return@forEach
                        }
                    }
                    if (!itemUpdated) {
                        throw IllegalStateException("Товар не найден: $itemId")
                    }
                }
            }
            DataResult.Success(Unit)
        } catch (e: Exception) {
            Napier.e(e, tag = TAG) { "Ошибка при обновлении нескольких товаров: ${e.message}" }
            DataResult.error(DataError.InventoryItem.UPDATE_FAILED)
        }
    }


    companion object {
        const val TAG = "FirebaseInventoryRepository"
    }

    override fun Exception.toDataError(): DataError {
        return when (this) {
            is FirebaseFirestoreException -> {
                when (code) {
                    FirestoreExceptionCode.NOT_FOUND -> DataError.Order.NOT_FOUND
                    FirestoreExceptionCode.PERMISSION_DENIED -> DataError.Order.PERMISSION_DENIED
                    else -> DataError.Network.UNKNOWN
                }
            }

            else -> DataError.Order.UNKNOWN
        }
    }
}
