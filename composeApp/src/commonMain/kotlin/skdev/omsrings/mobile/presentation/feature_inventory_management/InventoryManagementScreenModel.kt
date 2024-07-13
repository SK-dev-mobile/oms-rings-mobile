package skdev.omsrings.mobile.presentation.feature_inventory_management

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.quantity_must_be_positive_number
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
import skdev.omsrings.mobile.utils.fields.validators.matchRegex
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager


class InventoryManagementScreenModel(
    val notificationManager: NotificationManager
) : BaseScreenModel<Event, Effect>(notificationManager) {


    private val _state = MutableStateFlow(
        InventoryState(
            folderField = createFolderField(),
            itemField = createItemField(),
            quantityField = createNewQuantityField()
        )
    )
    val state = _state.asStateFlow()

    private fun createFolderField() = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock { value ->
            ValidationResult.of(value) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    private fun createItemField() = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock { value ->
            ValidationResult.of(value) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    private fun createNewQuantityField() = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock { value ->
            ValidationResult.of(value) {
                notBlank(Res.string.cant_be_blank).matchRegex(
                    Res.string.quantity_must_be_positive_number,
                    Regex("^[1-9]\\d*$")
                )
            }

        }
    )


    override fun onEvent(event: Event) {
        when (event) {
            // Folder
            is Event.SetSelectedInventoryFolder -> setSelectedInventoryFolder(event.folderId)
            is Event.CreateOrUpdateFolder -> createOrUpdateFolder()
            is Event.RemoveInventoryFolder -> removeInventoryFolder(event.folder)
            is Event.DisplayFolderDialog -> displayFolderDialog(event.folder)
            Event.CloseFolderDialog -> closeFolderDialog()

            // Item
            Event.AddOrUpdateInventoryItem -> addOrUpdateInventoryItem()
            is Event.DisplayInventoryItemDialog -> displayInventoryItemDialog(event.item)
            is Event.RemoveInventoryItem -> removeInventoryItem(event.item)
            Event.CloseInventoryItemDialog -> closeInventoryItemDialog()

            // Item Quantity
            is Event.IncrementQuantityInventoryItem -> incrementQuantityInventoryItem(event.item)
            is Event.DisplayIncrementQuantityDialog -> displayIncrementQuantityDialog(event.item)
            Event.CloseIncrementQuantityDialog -> closeIncrementQuantityDialog()
        }
    }

    private fun createOrUpdateFolder() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.folderField)) {
                val folderName = currentState.folderField.data.value
                _state.update { state ->
                    val updatedFolders = if (state.folderToEdit != null) {
                        state.folders.map { folder ->
                            if (folder.id == state.folderToEdit.id) folder.setName(folderName) else folder
                        }
                    } else {
                        state.folders + Folder(name = folderName)
                    }
                    state.copy(
                        folders = updatedFolders
                    )
                }
            }
        }
        closeFolderDialog()
    }

    private fun displayFolderDialog(folder: Folder?) {
        _state.update {
            it.copy(
                isFolderDialogVisible = true,
                folderField = createFolderField().apply { data.value = folder?.name ?: "" },
                folderToEdit = folder
            )
        }
    }

    private fun closeFolderDialog() {
        _state.update {
            it.copy(
                isFolderDialogVisible = false,
                folderField = createFolderField(),
                folderToEdit = null
            )
        }
    }

    private fun removeInventoryFolder(folder: Folder) {
        _state.update { state ->
            state.copy(
                folders = state.folders - folder,
                selectedFolderId = if (state.selectedFolderId == folder.id) null else state.selectedFolderId
            )
        }
    }

    private fun addOrUpdateInventoryItem() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.itemField)) {
                val itemName = currentState.itemField.data.value
                _state.update { state ->
                    val updatedFolders = state.folders.map { folder ->
                        if (folder.id == state.selectedFolderId) {
                            val updatedItems = if (state.itemToEdit != null) {
                                folder.inventoryItems.map { item ->
                                    if (item.id == state.itemToEdit.id) item.setName(itemName) else item
                                }
                            } else {
                                folder.inventoryItems + InventoryItem(name = itemName)
                            }
                            folder.copy(inventoryItems = updatedItems)
                        } else {
                            folder
                        }
                    }
                    state.copy(
                        folders = updatedFolders,
                    )
                }
            }
        }
        closeInventoryItemDialog()
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

    private fun displayInventoryItemDialog(item: InventoryItem?) {
        _state.update {
            it.copy(
                isItemDialogVisible = true,
                itemField = createItemField().apply { data.value = item?.name ?: "" },
                itemToEdit = item
            )
        }
    }

    private fun closeInventoryItemDialog() {
        _state.update {
            it.copy(
                isItemDialogVisible = false,
                itemField = createItemField(),
                itemToEdit = null
            )
        }
    }


    private fun displayIncrementQuantityDialog(item: InventoryItem) {
        _state.update { it.copy(incrementQuantityDialogVisible = true, selectedItem = item) }
    }

    private fun closeIncrementQuantityDialog() {
        _state.update {
            it.copy(
                incrementQuantityDialogVisible = false,
                selectedItem = null,
                quantityField = createNewQuantityField()
            )
        }
    }

    private fun incrementQuantityInventoryItem(item: InventoryItem) {
        if (_state.value.quantityField.validate()) {
            val incrementQuantity = _state.value.quantityField.value().toIntOrNull() ?: 0
            _state.update { state ->
                state.copy(
                    folders = state.folders.map { folder ->
                        if (folder.id == state.selectedFolderId) {
                            folder.copy(inventoryItems = folder.inventoryItems.map {
                                if (it.id == item.id) it.incrementQuantity(incrementQuantity) else it
                            })
                        } else {
                            folder
                        }
                    }
                )
            }
            closeIncrementQuantityDialog()
        }
    }
}