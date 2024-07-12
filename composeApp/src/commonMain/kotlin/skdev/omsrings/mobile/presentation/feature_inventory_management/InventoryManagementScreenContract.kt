package skdev.omsrings.mobile.presentation.feature_inventory_management

import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.utils.fields.FormField

object InventoryManagementScreenContract {

    data class InventoryState(
        val folders: List<Folder> = emptyList(),
        val selectedFolderId: String? = null,
        val isAddingFolder: Boolean = false,
        val isAddingItem: Boolean = false,
        val newFolderField: FormField<String, StringResource>,
        val newItemField: FormField<String, StringResource>
    )

    sealed interface Event {

        // Dialogs
        data object ShowAddFolderDialog : Event
        data object HideAddFolderDialog : Event
        data object ShowAddItemDialog : Event
        data object HideAddItemDialog : Event

        // Actions With Item
        data object AddItem : Event
        data class DeleteItem(val item: InventoryItem) : Event

        // Actions With Folder
        data class SelectFolder(val folderId: String?) : Event
        data object AddFolder : Event
        data class DeleteFolder(val folder: Folder) : Event
    }

    sealed interface Effect {

    }
}