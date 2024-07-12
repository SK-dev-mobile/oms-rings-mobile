package skdev.omsrings.mobile.presentation.feature_inventory_management

import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.utils.fields.FormField

object InventoryManagementScreenContract {

    data class InventoryState(
        // Objects
        val folders: List<Folder> = emptyList(),
        // Folder
        val selectedFolderId: String? = null,
        // Quantity
        val selectedItem: InventoryItem? = null,
        // Visible Dialog
        val isAddingFolder: Boolean = false,
        val isAddingItem: Boolean = false,
        val isIncrementQuantity: Boolean = false,
        // Field
        val newFolderField: FormField<String, StringResource>,
        val newItemField: FormField<String, StringResource>,
        val newQuantityField: FormField<String, StringResource>
    )

    sealed interface Event {

        // Dialogs
        data object DisplayCreateFolderDialog : Event
        data object CloseCreateFolderDialog : Event
        data object DisplayAddInventoryItemDialog : Event
        data object CloseAddInventoryItemDialog : Event
        data class DisplayIncrementQuantityDialog(val item: InventoryItem) : Event
        data object CloseIncrementQuantityDialog : Event

        // Actions With Item
        data class IncrementQuantityInventoryItem(val item: InventoryItem, val additionalQuantity: Int) : Event
        data object AddInventoryItem : Event
        data class RemoveInventoryItem(val item: InventoryItem) : Event

        // Actions With Folder
        data class SetSelectedInventoryFolder(val folderId: String?) : Event
        data object CreateInventoryFolder : Event
        data class RemoveInventoryFolder(val folder: Folder) : Event
    }

    sealed interface Effect {

    }
}