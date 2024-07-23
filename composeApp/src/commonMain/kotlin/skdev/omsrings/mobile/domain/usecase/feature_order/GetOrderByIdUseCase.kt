package skdev.omsrings.mobile.domain.usecase.feature_order

import kotlinx.coroutines.flow.Flow
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class GetOrderByIdUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String): DataResult<Order, DataError> {
        return repository.getOrderById(orderId)
    }
}