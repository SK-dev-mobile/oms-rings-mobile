package skdev.omsrings.mobile.presentation.feature_inventory_management

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementContract.Effect
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementContract.InventoryState


class InventoryManagementModel(
    val notificationManager: NotificationManager
) : BaseScreenModel<Event, Effect>(notificationManager) {
    private val _state = MutableStateFlow(InventoryState())
    val state = _state.asStateFlow()

    override

    fun onEvent(event: Event) {
        when (event) {
            is Event.AddItem -> addItem()
            is Event.DeleteItem -> deleteItem(event.item)
        }
    }

    private fun addItem() {
        TODO("DRY")
    }

    private fun deleteItem(item: InventoryItem) {
        TODO("GO GO!")
    }
}