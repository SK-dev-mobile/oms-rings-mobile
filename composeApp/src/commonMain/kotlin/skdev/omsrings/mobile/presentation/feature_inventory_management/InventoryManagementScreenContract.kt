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

        // InventoryItem
        val isItemDialogVisible: Boolean = false,
        val itemToEdit: InventoryItem? = null,
        val itemField: FormField<String, StringResource>,

        // Inventory Item Quantity
        val selectedItem: InventoryItem? = null,
        val incrementQuantityDialogVisible: Boolean = false,
        val quantityField: FormField<String, StringResource>

    )

    sealed interface Event {
        // Event for navigating between folders (in, out)
        data class SetSelectedInventoryFolder(val folderId: String?) : Event

        // Folder
        data object CreateOrUpdateFolder : Event
        data class DisplayFolderDialog(val folder: Folder? = null) : Event
        data object CloseFolderDialog : Event
        data class RemoveInventoryFolder(val folder: Folder) : Event


        // Item
        data object AddOrUpdateInventoryItem : Event
        data class DisplayInventoryItemDialog(val item: InventoryItem? = null) : Event
        data object CloseInventoryItemDialog : Event
        data class RemoveInventoryItem(val item: InventoryItem) : Event

        // Inventory Item Quantity
        data class IncrementQuantityInventoryItem(val item: InventoryItem, val additionalQuantity: Int) : Event
        data class DisplayIncrementQuantityDialog(val item: InventoryItem) : Event
        data object CloseIncrementQuantityDialog : Event

        // Back navigation
        data object OnBackClicked : Event


    }

    sealed interface Effect {
        object NavigateBack: Effect
    }
}