package skdev.omsrings.mobile.domain.model


import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

typealias UUID = String

@Serializable
data class Order(
    val id: UUID,
    val createdBy: String,
    val date: Timestamp,
    val address: String?,
    val comment: String?,
    val contactPhone: String?,
    val isDelivery: Boolean,
    val pickupTime: Timestamp,
    val status: OrderStatus,
    val history: List<OrderHistoryEvent>,
    val items: List<OrderItem>
)

@Serializable
data class OrderItem(
    val inventoryId: UUID,
    val quantity: Int
)

@Serializable
data class OrderHistoryEvent(
    val time: Timestamp,
    val type: OrderHistoryEventType,
    val userFullName: String,
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