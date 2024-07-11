package skdev.omsrings.mobile.presentation.feature_inventory_management

import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementContract.Effect

class InventoryManagementModel(
    val notificationManager: NotificationManager
) : BaseScreenModel<Event, Effect>(notificationManager) {
    override fun onEvent(event: Event) {
        TODO("Not yet implemented")
    }
}