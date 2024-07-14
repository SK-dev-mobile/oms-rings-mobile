package skdev.omsrings.mobile.data.base

import io.github.aakira.napier.Napier
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface BaseRepository {
    val TAG: String
        get() = this::class.simpleName ?: "BaseRepository"

    suspend fun <D : Any> withCathing(block: suspend () -> DataResult<D, DataError>): DataResult<D, DataError> {
        try {
            return block()
        } catch (e: Exception) {
            Napier.e(tag = TAG, throwable = e) { "Error: $e" }
            return DataResult.error(e.toDataError())
        }
    }

    fun Exception.toDataError(): DataError
}
