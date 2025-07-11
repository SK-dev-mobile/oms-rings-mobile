package skdev.omsrings.mobile.domain.usecase.feature_day_orders

import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.data.repository.FirebaseOrderRepository
import skdev.omsrings.mobile.domain.model.OrderItem
import skdev.omsrings.mobile.domain.model.OrderStatus
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.GetUserSettingsUseCase
import skdev.omsrings.mobile.domain.utils.notifyError
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderHistoryEvent
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderInfoModel
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.datetime.toLocalDate
import skdev.omsrings.mobile.utils.datetime.toLocalDateTime
import skdev.omsrings.mobile.presentation.feature_day_orders.enitity.OrderItem as OrderItemModel
import skdev.omsrings.mobile.utils.datetime.toTimestamp
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.format.formatPhoneNumber
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.DataResult
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess
import skdev.omsrings.mobile.utils.result.map

class GetDayOrdersUseCase(
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val notificationManager: NotificationManager,
) {
    suspend operator fun invoke(date: LocalDate): DataResult<List<OrderInfoModel>, DataError> {
        var filterArchived = false

        getUserSettingsUseCase().ifError {
            Napier.e(tag = "GetDayOrdersUseCase") { "Failed to get user settings: ${it.error}" }
            return DataResult.error(error = it.error)
        }.ifSuccess {
            filterArchived = !it.data.showClearedOrders
        }

        val orders = orderRepository.getOrdersByDay(date.toTimestamp()).notifyError(
            notificationManager
        )
        if (orders is DataResult.Error) return DataResult.error(orders.error)
        check(orders is DataResult.Success)

        // Collect all items
        val allItemsFromOrders = mutableSetOf<OrderItem>()
        orders.data.map { allItemsFromOrders.addAll(it.items) }
        Napier.d(tag = "ABC") {"Collecting items from orders: $allItemsFromOrders" }

        val inventoryItems = inventoryRepository.getInventoryItemsByIds(allItemsFromOrders.map { it.inventoryId }).notifyError(
            notificationManager
        )

        if (inventoryItems is DataResult.Error) return DataResult.error(inventoryItems.error)
        check(inventoryItems is DataResult.Success)

        Napier.d(tag = "ABC") {"inventoryItems -> $inventoryItems" }

        var ordersInfoModels = orders.data.map { order ->
            OrderInfoModel(
                id = order.id,
                createdBy = order.createdBy,
                date = order.date.toLocalDate().format(DateTimePattern.SIMPLE_DATE),
                address = order.address,
                comment = order.comment,
                contactPhone = order.contactPhone?.let { formatPhoneNumber("+" + it) },
                isDelivery = order.isDelivery,
                pickupTime = order.pickupTime.toLocalDateTime().format(DateTimePattern.SIMPLE_TIME),
                status = order.status,
                history = order.history.map {
                    OrderHistoryEvent(
                        time = it.time.toLocalDateTime().format(DateTimePattern.HISTORY_EVENT_TIME),
                        type = it.type,
                        userFullName = it.userFullName
                    )
                },
                items = order.items.map { itemOrder ->
                    OrderItemModel(
                        inventoryName = inventoryItems.data.find { it.id == itemOrder.inventoryId }?.name ?: "Unknown item",
                        quantity = itemOrder.quantity
                    )
                }
            )
        }

        // Filter out archived orders if it's not allowed in user settings
        if (filterArchived) {
            ordersInfoModels = ordersInfoModels.filter { it.status != OrderStatus.ARCHIVED  }
        }

        return DataResult.success(ordersInfoModels)
    }
}
