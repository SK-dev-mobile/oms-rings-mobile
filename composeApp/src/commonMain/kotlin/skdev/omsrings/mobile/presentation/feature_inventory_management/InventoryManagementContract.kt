package skdev.omsrings.mobile.presentation.feature_inventory_management

import skdev.omsrings.mobile.domain.model.InventoryItem

object InventoryManagementContract {

    data class State(
        val items: List<InventoryItem> = emptyList()
    )

    sealed interface Event {
        data object AddItem : Event
        data class DeleteItem(val item: InventoryItem)

    }

    sealed interface Effect {

    }
}