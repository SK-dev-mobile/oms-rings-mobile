package skdev.omsrings.mobile.domain.repository

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface OrderRepository {
    suspend fun createOrder(order: Order): DataResult<Order, DataError>
    fun getOrderById(id: String): Flow<DataResult<Order, DataError>>
    fun getAllOrders(): Flow<DataResult<List<Order>, DataError>>
    suspend fun updateOrder(order: Order): DataResult<Order, DataError>
    suspend fun deleteOrder(order: Order): DataResult<Unit, DataError>
    suspend fun getDaysInfoByRange(start: Timestamp, end: Timestamp): DataResult<Map<LocalDate, DayInfoModel>, DataError>
}