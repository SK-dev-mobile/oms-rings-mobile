package skdev.omsrings.mobile.domain.usecase.feature_order

import kotlinx.coroutines.flow.Flow
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class GetFoldersAndItemsInventory(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(): DataResult<Flow<List<Folder>>, DataError> {
        return DataResult.success(repository.getFoldersAndItems())
    }
}