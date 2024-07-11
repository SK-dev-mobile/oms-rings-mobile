package skdev.omsrings.mobile.presentation.feature_inventory_management

import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.utils.fields.FormField

object InventoryManagementScreenContract {

    data class InventoryState(
        val items: List<InventoryItem> = emptyList(),
        val isAddingItem: Boolean = false,
        val newItemField: FormField<String, StringResource>
    )

    sealed interface Event {
        data object AddItem : Event
        data class DeleteItem(val item: InventoryItem) : Event

        data object ShowAddItemDialog : Event

    }

    sealed interface Effect {

    }
}