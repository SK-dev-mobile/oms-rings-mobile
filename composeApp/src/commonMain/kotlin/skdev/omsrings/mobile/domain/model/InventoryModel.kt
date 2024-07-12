package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable
import skdev.omsrings.mobile.utils.uuid.randomUUID


// TODO: Add a confirmation dialog before deleting categories, especially if they contain items.

@Serializable
data class Folder(
    val id: String = randomUUID(),
    val name: String,
    val items: List<InventoryItem> = emptyList()
)

@Serializable
data class InventoryItem(
    val id: String = randomUUID(),
    val name: String
)
