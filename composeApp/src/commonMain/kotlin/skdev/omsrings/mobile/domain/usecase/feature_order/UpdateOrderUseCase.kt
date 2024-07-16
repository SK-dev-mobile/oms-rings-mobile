package skdev.omsrings.mobile.domain.usecase.feature_order

import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class UpdateOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order): DataResult<Order, DataError> {
        return repository.updateOrder(order)
    }
}