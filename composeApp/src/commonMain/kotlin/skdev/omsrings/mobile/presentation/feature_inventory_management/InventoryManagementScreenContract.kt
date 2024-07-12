package skdev.omsrings.mobile.presentation.feature_inventory_management

import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.utils.fields.FormField

object InventoryManagementScreenContract {

    data class InventoryState(
        // Info
        val folders: List<Folder> = emptyList(),

        // Folder
        val selectedFolderId: String? = null,
        val isFolderDialogVisible: Boolean = false,
        val folderField: FormField<String, StringResource>,
        val folderToEdit: Folder? = null,
        // Quantity
        val selectedItem: InventoryItem? = null,
        // Visible Dialog
        val isAddingItem: Boolean = false,
        val isIncrementQuantity: Boolean = false,
        // Field
        val newItemField: FormField<String, StringResource>,
        val newQuantityField: FormField<String, StringResource>

    )

    sealed interface Event {
        // Folder
        data object CreateOrUpdateFolder : Event
        data class DisplayFolderDialog(val folder: Folder? = null) : Event
        data object CloseFolderDialog : Event
        data class RemoveInventoryFolder(val folder: Folder) : Event

        // Dialogs
        data object DisplayAddInventoryItemDialog : Event
        data object CloseAddInventoryItemDialog : Event
        data class DisplayIncrementQuantityDialog(val item: InventoryItem) : Event
        data object CloseIncrementQuantityDialog : Event

        // Actions With Item
        data class IncrementQuantityInventoryItem(val item: InventoryItem, val additionalQuantity: Int) : Event
        data object AddInventoryItem : Event
        data class RemoveInventoryItem(val item: InventoryItem) : Event

        // Transition
        data class SetSelectedInventoryFolder(val folderId: String?) : Event
    }

    sealed interface Effect {

    }
}