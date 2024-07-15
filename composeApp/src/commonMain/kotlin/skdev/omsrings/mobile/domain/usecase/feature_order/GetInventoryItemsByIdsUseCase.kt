package skdev.omsrings.mobile.domain.usecase.feature_order

import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class GetInventoryItemsByIdsUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(ids: List<String>): DataResult<List<InventoryItem>, DataError> {
        return repository.getInventoryItemsByIds(ids)
    }
}