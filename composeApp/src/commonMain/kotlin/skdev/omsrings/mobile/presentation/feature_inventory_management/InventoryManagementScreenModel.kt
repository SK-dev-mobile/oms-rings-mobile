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
            Event.AddInventoryItem -> addInventoryItem()
            is Event.RemoveInventoryItem -> removeInventoryItem(event.item)
            Event.DisplayAddInventoryItemDialog -> displayAddInventoryItemDialog()
            Event.CloseAddInventoryItemDialog -> closeAddInventoryItemDialog()
            Event.CreateInventoryFolder -> createInventoryFolder()
            is Event.RemoveInventoryFolder -> removeInventoryFolder(event.folder)
            is Event.SetSelectedInventoryFolder -> setSelectedInventoryFolder(event.folderId)
            Event.DisplayCreateFolderDialog -> displayCreateFolderDialog()
            Event.CloseCreateFolderDialog -> closeCreateFolderDialog()
        }
    }

    private fun createInventoryFolder() {
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
        closeCreateFolderDialog()
    }

    private fun removeInventoryFolder(folder: Folder) {
        _state.update { state ->
            state.copy(
                folders = state.folders - folder,
                selectedFolderId = if (state.selectedFolderId == folder.id) null else state.selectedFolderId
            )
        }
    }

    private fun addInventoryItem() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.newItemField) && currentState.selectedFolderId != null) {
                val newItem = InventoryItem(name = currentState.newItemField.data.value)
                _state.update { state ->
                    state.copy(
                        folders = state.folders.map { folder ->
                            if (folder.id == state.selectedFolderId) {
                                folder.copy(inventoryItems = folder.inventoryItems + newItem)
                            } else {
                                folder
                            }
                        },
                        newItemField = createNewItemField()
                    )
                }
            }
        }
        closeAddInventoryItemDialog()
    }

    private fun removeInventoryItem(item: InventoryItem) {
        _state.update { state ->
            state.copy(
                folders = state.folders.map { folder ->
                    if (folder.id == state.selectedFolderId) {
                        folder.copy(inventoryItems = folder.inventoryItems - item)
                    } else {
                        folder
                    }
                }
            )
        }
    }

    private fun setSelectedInventoryFolder(folderId: String?) {
        _state.update { it.copy(selectedFolderId = folderId) }
    }

    private fun displayAddInventoryItemDialog() {
        _state.update { it.copy(isAddingItem = true) }
    }

    private fun closeAddInventoryItemDialog() {
        _state.update {
            it.copy(
                isAddingItem = false,
                newItemField = createNewItemField()
            )
        }
    }

    private fun displayCreateFolderDialog() {
        _state.update { it.copy(isAddingFolder = true) }
    }

    private fun closeCreateFolderDialog() {
        _state.update {
            it.copy(
                isAddingFolder = false,
                newFolderField = createNewFolderField()
            )
        }
    }
}