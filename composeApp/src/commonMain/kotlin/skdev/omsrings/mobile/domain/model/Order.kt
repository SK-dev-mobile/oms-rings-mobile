package skdev.omsrings.mobile.domain.model


import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val phone: String,
    val deliveryMethod: DeliveryMethod,
    val address: String?,
    val dateTime: String,
    val comment: String,
)