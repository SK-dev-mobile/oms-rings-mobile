package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.domain.repository.InventoryRepository

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
        val folder = folderRef.get().data<Folder>() ?: return
        val updatedFolder = update(folder)
        folderRef.set(updatedFolder)
    }


}
