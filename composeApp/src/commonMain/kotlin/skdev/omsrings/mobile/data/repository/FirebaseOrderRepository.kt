package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseOrderRepository(
    private val firestore: FirebaseFirestore
) : OrderRepository {
    override suspend fun createOrder(order: Order): DataResult<Order, DataError> {
        TODO("Not yet implemented")
    }

    override fun getOrderById(id: String): Flow<DataResult<Order, DataError>> {
        TODO("Not yet implemented")
    }

    override fun getAllOrders(): Flow<DataResult<List<Order>, DataError>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order): DataResult<Order, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrder(order: Order): DataResult<Unit, DataError> {
        TODO("Not yet implemented")
    }


}