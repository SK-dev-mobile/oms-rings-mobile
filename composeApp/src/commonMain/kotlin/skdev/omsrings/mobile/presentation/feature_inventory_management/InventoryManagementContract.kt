package skdev.omsrings.mobile.presentation.feature_inventory_management

import skdev.omsrings.mobile.domain.model.InventoryItem

object InventoryManagementContract {

    data class InventoryState(
        val items: List<InventoryItem> = emptyList()
    )

    sealed interface Event {
        data object AddItem : Event
        data class DeleteItem(val item: InventoryItem) : Event

    }

    sealed interface Effect {

    }
}