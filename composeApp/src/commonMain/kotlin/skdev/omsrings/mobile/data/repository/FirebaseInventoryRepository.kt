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


    override fun getFolders(): Flow<List<Folder>> =
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
        foldersCollection.document(folderId).collection("items").document(item.id).set(item)
    }

    override suspend fun updateInventoryItem(folderId: String, item: InventoryItem) {
        foldersCollection.document(folderId)
            .collection("items")
            .document(item.id)
            .set(item)
    }

    override suspend fun deleteInventoryItem(folderId: String, itemId: String) {
        foldersCollection.document(folderId)
            .collection("items")
            .document(itemId)
            .delete()
    }


}
