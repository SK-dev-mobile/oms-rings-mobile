package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable

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