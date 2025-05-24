package skdev.omsrings.mobile.domain.usecase.feature_daily_cart

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.FolderWithItems
import skdev.omsrings.mobile.domain.model.ItemWithQuantity
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.utils.datetime.toTimestamp
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult

class GetDailyCartItemsUseCase(
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(date: LocalDate): DataResult<List<FolderWithItems>, DataError> {
        // Get orders for the date
        val orders = orderRepository.getOrdersByDay(date.toTimestamp()).notifyError(notificationManager)
        if (orders is DataResult.Error) return DataResult.error(orders.error)
        check(orders is DataResult.Success)

        // Get all inventory items from orders
        val allItemIds = orders.data.flatMap { order ->
            order.items.map { it.inventoryId }
        }.distinct()

        val inventoryItems = inventoryRepository.getInventoryItemsByIds(allItemIds).notifyError(notificationManager)
        if (inventoryItems is DataResult.Error) return DataResult.error(inventoryItems.error)
        check(inventoryItems is DataResult.Success)

        // Calculate quantities for each item
        val itemQuantities = calculateItemQuantities(orders.data)

        // Get folders with items
        val folders = inventoryRepository.getFoldersAndItems().first()
        
        // Group items by folders
        val foldersWithItems = folders.map { folder ->
            val folderItems = folder.inventoryItems.mapNotNull { item ->
                val quantity = itemQuantities[item.id] ?: 0
                if (quantity > 0) {
                    ItemWithQuantity(item, quantity)
                } else null
            }
            FolderWithItems(folder, folderItems)
        }.filter { it.items.isNotEmpty() }

        return DataResult.success(foldersWithItems)
    }

    private fun calculateItemQuantities(orders: List<Order>): Map<String, Int> {
        return orders.flatMap { order ->
            order.items.map { orderItem ->
                orderItem.inventoryId to orderItem.quantity
            }
        }.groupBy(
            { it.first },
            { it.second }
        ).mapValues { it.value.sum() }
    }
}
