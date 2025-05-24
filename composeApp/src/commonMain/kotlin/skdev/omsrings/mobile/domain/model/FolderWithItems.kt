package skdev.omsrings.mobile.domain.model

data class FolderWithItems(
    val folder: Folder,
    val items: List<ItemWithQuantity>
)

data class ItemWithQuantity(
    val item: InventoryItem,
    val quantity: Int
)