package skdev.omsrings.mobile.domain.model

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