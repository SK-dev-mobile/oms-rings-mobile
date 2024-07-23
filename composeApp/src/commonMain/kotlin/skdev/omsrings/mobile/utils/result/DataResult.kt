package skdev.omsrings.mobile.utils.result

import skdev.omsrings.mobile.utils.error.Error


internal typealias RootError = Error

/**
 * Sealed class for handling success and error states
 *
 * @param D Data type
 * @param E Error type, see [skdev.omsrings.mobile.utils.error.DataError]
 * */
sealed interface DataResult<out D : Any, out E : RootError> {
    data class Success<out D : Any, out E : RootError>(val data: D) : DataResult<D, E>
    data class Error<out D : Any, out E : RootError>(val error: E) : DataResult<D, E>

    companion object {
        fun <D : Any, E : RootError> success(data: D): DataResult<D, E> = Success(data)

        fun <D : Any, E : RootError> error(error: E): DataResult<D, E> = Error(error)

        val <D : Any, E : RootError> DataResult<D, E>.isSuccess: Boolean
            get() = this is Success

        val <D : Any, E : RootError> DataResult<D, E>.isError: Boolean
            get() = this is Error

        fun <D : Any, E : RootError> DataResult<D, E>.getOrThrow(): D {
            return when (this) {
                is Success -> data
                is Error -> kotlin.error(this.error)
            }
        }
    }
}

/**
 * Map some data in RootError (use it for changing data type)
 *
 * **Like this:**
 * - DTO -> MODEL
 */
fun <I : Any, O : Any, E : RootError> DataResult<I, E>.map(mapper: (I) -> O): DataResult<O, E> {
    return when (this) {
        is DataResult.Success -> DataResult.Success(mapper(data))
        is DataResult.Error -> DataResult.Error(error)
    }
}

/**
 * Do smth if Result is success
 */
suspend inline fun <D : Any, E : RootError> DataResult<D, E>.ifSuccess(body: suspend (result: DataResult.Success<D, E>) -> Unit): DataResult<D, E> {
    if (this is DataResult.Success) {
        body(this)
    }
    return this
}

/**
 * Do smth if Result is error
 */
suspend inline fun <D : Any, E : RootError> DataResult<D, E>.ifError(body: suspend (result: DataResult.Error<D, E>) -> Unit): DataResult<D, E> {
    if (this is DataResult.Error) {
        body(this)
    }
    return this
}
