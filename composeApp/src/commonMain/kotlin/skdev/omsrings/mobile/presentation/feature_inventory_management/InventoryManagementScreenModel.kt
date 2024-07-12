package skdev.omsrings.mobile.presentation.feature_inventory_management

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Effect
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.InventoryState
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validateAll
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager


class InventoryManagementScreenModel(
    val notificationManager: NotificationManager
) : BaseScreenModel<Event, Effect>(notificationManager) {


    private val _state = MutableStateFlow(
        InventoryState(
            newFolderField = createNewFolderField(),
            newItemField = createNewItemField()
        )
    )
    val state = _state.asStateFlow()

    private fun createNewFolderField() = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    private fun createNewItemField() = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )


    override fun onEvent(event: Event) {
        when (event) {
            is Event.AddItem -> addItem()
            is Event.DeleteItem -> deleteItem(event.item)
            is Event.ShowAddItemDialog -> showAddItemDialog()
            is Event.HideAddItemDialog -> hideAddItemDialog()
            is Event.AddFolder -> addFolder()
            is Event.DeleteFolder -> deleteFolder(event.folder)
            is Event.SelectFolder -> selectFolder(event.folderId)
            Event.ShowAddFolderDialog -> showAddFolderDialog()
            Event.HideAddFolderDialog -> hideAddFolderDialog()
        }
    }

    private fun addFolder() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.newFolderField)) {
                val newFolder = Folder(name = currentState.newFolderField.data.value)
                _state.update {
                    it.copy(
                        folders = it.folders + newFolder,
                        newFolderField = createNewFolderField()
                    )
                }
            }
        }
        hideAddFolderDialog()
    }

    private fun deleteFolder(folder: Folder) {
        _state.update { state ->
            state.copy(
                folders = state.folders - folder,
                selectedFolderId = if (state.selectedFolderId == folder.id) null else state.selectedFolderId
            )
        }
    }

    private fun addItem() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.newItemField) && currentState.selectedFolderId != null) {
                val newItem = InventoryItem(name = currentState.newItemField.data.value)
                _state.update { state ->
                    state.copy(
                        folders = state.folders.map { folder ->
                            if (folder.id == state.selectedFolderId) {
                                folder.copy(items = folder.items + newItem)
                            } else {
                                folder
                            }
                        },
                        newItemField = createNewItemField()
                    )
                }
            }
        }
        hideAddItemDialog()
    }

    private fun deleteItem(item: InventoryItem) {
        _state.update { state ->
            state.copy(
                folders = state.folders.map { folder ->
                    if (folder.id == state.selectedFolderId) {
                        folder.copy(items = folder.items - item)
                    } else {
                        folder
                    }
                }
            )
        }
    }

    private fun selectFolder(folderId: String?) {
        _state.update { it.copy(selectedFolderId = folderId) }
    }

    private fun showAddItemDialog() {
        _state.update { it.copy(isAddingItem = true) }
    }

    private fun hideAddItemDialog() {
        _state.update {
            it.copy(
                isAddingItem = false,
                newItemField = createNewItemField()
            )
        }
    }

    private fun showAddFolderDialog() {
        _state.update { it.copy(isAddingFolder = true) }
    }

    private fun hideAddFolderDialog() {
        _state.update {
            it.copy(
                isAddingFolder = false,
                newFolderField = createNewFolderField()
            )
        }
    }
}