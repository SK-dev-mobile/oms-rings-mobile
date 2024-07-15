package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.code
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseOrderRepository(
    private val firestore: FirebaseFirestore
) : BaseRepository, OrderRepository {

    private val ordersCollection = firestore.collection("orders")

    override suspend fun createOrder(order: Order): DataResult<Order, DataError> =
        withCathing {
            val docRef = ordersCollection.document(order.id)
            docRef.set(order)
            DataResult.success(order)
        }

    override fun getOrderById(id: String): Flow<DataResult<Order, DataError>> = flow {
        try {
            val docSnapshot = ordersCollection.document(id).get()
            val order = docSnapshot.data<Order>()
            if (order != null) {
                emit(DataResult.success(order))
            } else {
                emit(DataResult.error(DataError.Order.NOT_FOUND))
            }
        } catch (e: Exception) {
            emit(DataResult.error(e.toDataError()))
        }
    }

    override fun getAllOrders(): Flow<DataResult<List<Order>, DataError>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateOrder(order: Order): DataResult<Order, DataError> =
        withCathing {
            val docRef = ordersCollection.document(order.id)
            docRef.set(order)
            DataResult.success(order)
        }

    override suspend fun deleteOrder(order: Order): DataResult<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override fun Exception.toDataError(): DataError {
        return when (this) {
            is FirebaseFirestoreException -> {
                when (code) {
                    FirestoreExceptionCode.NOT_FOUND -> DataError.Order.NOT_FOUND
                    FirestoreExceptionCode.PERMISSION_DENIED -> DataError.Order.PERMISSION_DENIED
                    else -> DataError.Network.UNKNOWN
                }
            }

            else -> DataError.Order.UNKNOWN
        }
    }
}
