package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseInventoryRepository(
    private val firestore: FirebaseFirestore
) : InventoryRepository {

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

    override suspend fun getInventoryItemsByIds(ids: List<String>): DataResult<List<InventoryItem>, DataError> {
        return try {
            val items = mutableListOf<InventoryItem>()

            // Fetch all folders
            val foldersSnapshot = foldersCollection.get().documents

            // Iterate through each folder
            for (folderDoc in foldersSnapshot) {
                val folder = folderDoc.data<Folder>()

                // Filter items in this folder that match the requested ids
                val matchingItems = folder.inventoryItems.filter { it.id in ids }
                items.addAll(matchingItems)


                // If we've found all requested items, we can stop searching
                if (items.size == ids.size) break
            }

            // Check if we found all requested items
            if (items.size < ids.size) {
                val missingIds = ids - items.map { it.id }.toSet()
                DataResult.error(DataError.InventoryItem.NOT_FOUND)
            } else {
                DataResult.Success(items)
            }
        } catch (e: Exception) {
            DataResult.error(DataError.Network.UNKNOWN)
        }
    }


}
