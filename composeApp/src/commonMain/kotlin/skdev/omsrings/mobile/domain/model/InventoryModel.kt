package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable
import skdev.omsrings.mobile.utils.uuid.randomUUID


// TODO: Add a confirmation dialog before deleting categories, especially if they contain items.

@Serializable
data class Folder(
    val id: String = randomUUID(),
    val name: String,
    val inventoryItems: List<InventoryItem> = emptyList()
) {
    fun setName(name: String): Folder = copy(name = name)
    fun addItem(item: InventoryItem): Folder = copy(inventoryItems = inventoryItems + item)
    fun updateItem(item: InventoryItem): Folder =
        copy(inventoryItems = inventoryItems.map { if (it.id == item.id) item else it })

    fun removeItem(itemId: String): Folder = copy(inventoryItems = inventoryItems.filter { it.id != itemId })
}

@Serializable
data class InventoryItem(
    val id: String = randomUUID(),
    val name: String,
    val stockQuantity: Int = 0
) {
    fun incrementQuantity(amount: Int): InventoryItem =
        copy(stockQuantity = stockQuantity + amount.coerceAtLeast(0))

    fun setName(name: String): InventoryItem = copy(name = name)
}