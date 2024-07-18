package skdev.omsrings.mobile.data.repository

import dev.gitlive.firebase.firestore.Filter
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.code
import dev.gitlive.firebase.firestore.toMilliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import skdev.omsrings.mobile.data.base.BaseRepository
import skdev.omsrings.mobile.data.model.DayInfoDTO
import skdev.omsrings.mobile.data.utils.toLocalDate
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.domain.model.Order
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class FirebaseOrderRepository(
    private val firestore: FirebaseFirestore
) : BaseRepository, OrderRepository {

    private val ordersCollection = firestore.collection("orders")
    private val daysInfoCollection = firestore.collection("days_info")

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
            emit(DataResult.success(order))
        } catch (e: Exception) {
            emit(DataResult.error(e.toDataError()))
        }
    }

    override fun getAllOrders(): Flow<DataResult<List<Order>, DataError>> {
        return flow {
            try {
                val querySnapshot = ordersCollection.get()
                val orders = querySnapshot.documents.mapNotNull { it.data<Order>() }
                emit(DataResult.success(orders))
            } catch (e: Exception) {
                emit(DataResult.error(e.toDataError()))
            }
        }
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

    override suspend fun getDaysInfoByRange(
        start: Timestamp,
        end: Timestamp
    ): DataResult<Map<LocalDate, DayInfoModel>, DataError> {
        return try {
            val ordersQuerySnapshot = ordersCollection.where {
                ("date" greaterThanOrEqualTo start) and ("date" lessThanOrEqualTo end)
            }.get()
            val result = mutableMapOf<LocalDate, DayInfoModel>()
            val orders = ordersQuerySnapshot.documents.mapNotNull { it.data<Order>() }
            val daysInfoQuerySnapshot = daysInfoCollection.where {
                ("date" greaterThanOrEqualTo start) and ("date" lessThanOrEqualTo end)
            }.get()
            val daysInfo = daysInfoQuerySnapshot.documents.mapNotNull { it.data<DayInfoDTO>() }

            for (order in orders) {
                val date = order.date.toLocalDate()
                result.put(
                    date,
                    DayInfoModel(
                        isEdited = true,
                        isLocked = false
                    )
                )
            }

            for (dayInfo in daysInfo) {
                val date = dayInfo.date.toLocalDate()
                if (result.containsKey(date)) {
                    val newValue = result.get(date)?.copy(isLocked = dayInfo.isLocked)
                    checkNotNull(newValue)
                    result.put(date, newValue)
                } else {
                    result.put(
                        date,
                        DayInfoModel(
                            isEdited = false,
                            isLocked = dayInfo.isLocked
                        )
                    )
                }
            }

            DataResult.success(result)
        } catch (e: Exception) {
            DataResult.error(e.toDataError())
        }
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
