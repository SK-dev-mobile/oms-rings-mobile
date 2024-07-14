package skdev.omsrings.mobile.domain.model


import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val address: String,
    val comment: String,
    val contactPhone: String,
    val createdAt: Timestamp,
    val createdBy: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    // Delivery Method if true then delivery, if false then pickup
    val isDelivery: Boolean,
)

@Serializable
data class OrderItem(
    val inventoryId: String,
    val quantity: Int,
)

@Serializable
enum class OrderStatus {
    NEW,
    IN_PROGRESS,
    DONE,
    CANCELED
}