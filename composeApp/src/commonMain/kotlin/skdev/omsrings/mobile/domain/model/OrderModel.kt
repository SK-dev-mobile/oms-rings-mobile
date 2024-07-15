package skdev.omsrings.mobile.domain.model


import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String, // UUID
    val date: String, 
    val address: String?,
    val comment: String?,
    val contactPhone: String?,
    val isDelivery: Boolean,
    val pickupTime: String,
    val status: OrderStatus,
    val history: List<OrderHistoryEvent>,
    val items: List<OrderItem>
)

@Serializable
data class OrderItem(
    val inventoryId: String, // UUID as String
    val quantity: Int
)

@Serializable
data class OrderHistoryEvent(
    val time: String,
    val type: OrderHistoryEventType,
    val user: String // UUID as String
)

enum class OrderHistoryEventType {
    CREATED, COMPLETED, ARCHIVED, CHANGED
}

@Serializable
enum class OrderStatus {
    CREATED, COMPLETED, ARCHIVED
}

@Serializable
enum class DeliveryMethod {
    /**
     * Самовывоз: клиент забирает заказ самостоятельно из пункта выдачи.
     */
    PICKUP,

    /**
     * Доставка: заказ доставляется по адресу, указанному клиентом.
     */
    DELIVERY;
}