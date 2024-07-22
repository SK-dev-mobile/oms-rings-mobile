package skdev.omsrings.mobile.presentation.feature_day_orders.enitity

import kotlinx.serialization.Serializable
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.event_type_archived
import omsringsmobile.composeapp.generated.resources.event_type_changed
import omsringsmobile.composeapp.generated.resources.event_type_completed
import omsringsmobile.composeapp.generated.resources.event_type_created
import omsringsmobile.composeapp.generated.resources.order_archived_status
import omsringsmobile.composeapp.generated.resources.order_completed_status
import omsringsmobile.composeapp.generated.resources.order_created_status
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.UUID



@Serializable
data class OrderInfoModel(
    val id: UUID,
    val createdBy: String,
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
    val inventoryName: String,
    val quantity: Int
)

@Serializable
data class OrderHistoryEvent(
    val time: String,
    val type: OrderHistoryEventType,
    val userFullName: String,
)

enum class OrderHistoryEventType(val resValue: StringResource) {
    CREATED(Res.string.event_type_created),
    COMPLETED(Res.string.event_type_completed),
    ARCHIVED(Res.string.event_type_archived),
    CHANGED(Res.string.event_type_changed)
}

@Serializable
enum class OrderStatus(val resValue: StringResource) {
    CREATED(Res.string.order_created_status),
    COMPLETED(Res.string.order_completed_status),
    ARCHIVED(Res.string.order_archived_status),
}

fun generateSampleOrderInfoList(): List<OrderInfoModel> {
    return listOf(
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1521",
            createdBy = "Alex Grazov",
            date = "2024-07-22",
            address = "123 Main St",
            comment = "Leave at front door",
            contactPhone = "123-456-7890",
            isDelivery = true,
            pickupTime = "12:00 PM",
            status = OrderStatus.CREATED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-21T10:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "John Doe"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item A",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item B",
                    quantity = 1
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1522",
            createdBy = "Alex Grazov",
            date = "2024-07-21",
            address = "456 Elm St",
            comment = "Ring the doorbell",
            contactPhone = "234-567-8901",
            isDelivery = false,
            pickupTime = "2:00 PM",
            status = OrderStatus.ARCHIVED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-20 09:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Jane Smith"
                ),
                OrderHistoryEvent(
                    time = "2024-07-20 09:30",
                    type = OrderHistoryEventType.CHANGED,
                    userFullName = "Jane Smith"
                ),
                OrderHistoryEvent(
                    time = "2024-07-20 10:00",
                    type = OrderHistoryEventType.COMPLETED,
                    userFullName = "Ivan Arhypov"
                ),
                OrderHistoryEvent(
                    time = "2024-07-20 13:10",
                    type = OrderHistoryEventType.ARCHIVED,
                    userFullName = "Jane Smith"
                ),
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item C",
                    quantity = 3
                ),
                OrderItem(
                    inventoryName = "Item D",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item D",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item D",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item D",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item D",
                    quantity = 2
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1523",
            createdBy = "Alex Grazov",
            date = "2024-07-20",
            address = "789 Pine St",
            comment = "Call on arrival",
            contactPhone = "345-678-9012",
            isDelivery = true,
            pickupTime = "4:00 PM",
            status = OrderStatus.ARCHIVED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-19T08:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Alice Brown"
                ),
                OrderHistoryEvent(
                    time = "2024-07-20T03:00:00",
                    type = OrderHistoryEventType.ARCHIVED,
                    userFullName = "Alice Brown"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item E",
                    quantity = 1
                ),
                OrderItem(
                    inventoryName = "Item F",
                    quantity = 4
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1524",
            createdBy = "Alex Grazov",
            date = "2024-07-19",
            address = "101 Maple St",
            comment = "Leave at the back door",
            contactPhone = "456-789-0123",
            isDelivery = false,
            pickupTime = "6:00 PM",
            status = OrderStatus.CREATED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-18T07:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Bob Green"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item G",
                    quantity = 2
                ),
                OrderItem(
                    inventoryName = "Item H",
                    quantity = 3
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1525",
            createdBy = "Alex Grazov",
            date = "2024-07-18",
            address = "202 Oak St",
            comment = null,
            contactPhone = "567-890-1234",
            isDelivery = true,
            pickupTime = "8:00 PM",
            status = OrderStatus.COMPLETED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-17T06:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Charlie White"
                ),
                OrderHistoryEvent(
                    time = "2024-07-18T05:00:00",
                    type = OrderHistoryEventType.COMPLETED,
                    userFullName = "Charlie White"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item I",
                    quantity = 1
                ),
                OrderItem(
                    inventoryName = "Item J",
                    quantity = 5
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1526",
            createdBy = "Alex Grazov",
            date = "2024-07-17",
            address = "303 Birch St",
            comment = "Use side entrance",
            contactPhone = "678-901-2345",
            isDelivery = false,
            pickupTime = "10:00 AM",
            status = OrderStatus.ARCHIVED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-16T05:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "David Black"
                ),
                OrderHistoryEvent(
                    time = "2024-07-17T02:00:00",
                    type = OrderHistoryEventType.ARCHIVED,
                    userFullName = "David Black"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item K",
                    quantity = 4
                ),
                OrderItem(
                    inventoryName = "Item L",
                    quantity = 1
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1527",
            createdBy = "Alex Grazov",
            date = "2024-07-16",
            address = "404 Cedar St",
            comment = "Call when at gate",
            contactPhone = "789-012-3456",
            isDelivery = true,
            pickupTime = "12:00 PM",
            status = OrderStatus.CREATED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-15T04:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Eve Blue"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item M",
                    quantity = 3
                ),
                OrderItem(
                    inventoryName = "Item N",
                    quantity = 2
                )
            )
        ),
        OrderInfoModel(
            id = "0b7d20ca-a659-474a-8485-66479b0f1528",
            createdBy = "Alex Grazov",
            date = "2024-07-15",
            address = "505 Walnut St",
            comment = "Ring bell twice",
            contactPhone = "890-123-4567",
            isDelivery = false,
            pickupTime = "2:00 PM",
            status = OrderStatus.COMPLETED,
            history = listOf(
                OrderHistoryEvent(
                    time = "2024-07-14T03:00:00",
                    type = OrderHistoryEventType.CREATED,
                    userFullName = "Frank Purple"
                ),
                OrderHistoryEvent(
                    time = "2024-07-15T01:00:00",
                    type = OrderHistoryEventType.COMPLETED,
                    userFullName = "Frank Purple"
                )
            ),
            items = listOf(
                OrderItem(
                    inventoryName = "Item M",
                    quantity = 3
                ),
                OrderItem(
                    inventoryName = "Item N",
                    quantity = 2
                )
            )
        )
    )
}