package skdev.omsrings.mobile.presentation.feature_inventory_management

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Effect
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.InventoryState
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.thairestaurant.utils.fields.FormField
import skdev.thairestaurant.utils.fields.flowBlock
import skdev.thairestaurant.utils.fields.validators.ValidationResult
import skdev.thairestaurant.utils.fields.validators.notBlank


class InventoryManagementScreenModel(
    val notificationManager: NotificationManager
) : BaseScreenModel<Event, Effect>(notificationManager) {


    private val _state = MutableStateFlow(
        InventoryState(
            newItemField = FormField(
                scope = screenModelScope,
                initialValue = "",
                validation = flowBlock {
                    ValidationResult.of(it) {
                        notBlank(Res.string.cant_be_blank)
                    }
                }
            )
        )
    )
    val state = _state.asStateFlow()

    override fun onEvent(event: Event) {
        when (event) {
            is Event.AddItem -> addItem()
            is Event.DeleteItem -> deleteItem(event.item)
            Event.ShowAddItemDialog -> showAddItemDialog()
        }
    }

    private fun addItem() {
        TODO("DRY")
    }

    private fun deleteItem(item: InventoryItem) {
        TODO("GO GO!")
    }

    private fun showAddItemDialog() {
        _state.update { it.copy(isAddingItem = true) }
    }
}