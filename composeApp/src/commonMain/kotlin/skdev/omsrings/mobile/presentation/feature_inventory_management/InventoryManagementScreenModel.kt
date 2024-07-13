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
import skdev.omsrings.mobile.domain.repository.InventoryRepository
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
    private val inventoryRepository: InventoryRepository,
    notificationManager: NotificationManager,
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

    init {
        loadFolders()
    }

    private fun loadFolders() {
        screenModelScope.launch {
            inventoryRepository.getFoldersAndItems().collect { folders ->
                _state.update { it.copy(folders = folders) }
            }
        }
    }


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
            is Event.IncrementQuantityInventoryItem -> incrementQuantityInventoryItem(
                event.item,
                event.additionalQuantity
            )

            is Event.DisplayIncrementQuantityDialog -> displayIncrementQuantityDialog(event.item)
            Event.CloseIncrementQuantityDialog -> closeIncrementQuantityDialog()
        }
    }

    private fun createOrUpdateFolder() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.folderField)) {
                val folderName = currentState.folderField.data.value
                val folder = currentState.folderToEdit?.copy(name = folderName) ?: Folder(name = folderName)
                if (currentState.folderToEdit != null) {
                    inventoryRepository.updateFolder(folder)
                } else {
                    inventoryRepository.addFolder(folder)
                }
                closeFolderDialog()
            }
        }
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
        screenModelScope.launch {
            inventoryRepository.deleteFolder(folder.id)
            _state.update { it.copy(selectedFolderId = if (it.selectedFolderId == folder.id) null else it.selectedFolderId) }
        }
    }

    private fun addOrUpdateInventoryItem() {
        screenModelScope.launch {
            val currentState = _state.value
            if (validateAll(currentState.itemField) && currentState.selectedFolderId != null) {
                val itemName = currentState.itemField.data.value
                val item = currentState.itemToEdit?.copy(name = itemName) ?: InventoryItem(name = itemName)
                if (currentState.itemToEdit != null) {
                    inventoryRepository.updateInventoryItem(currentState.selectedFolderId, item)
                } else {
                    inventoryRepository.addInventoryItem(currentState.selectedFolderId, item)
                }
                closeInventoryItemDialog()
            }
        }
    }

    private fun removeInventoryItem(item: InventoryItem) {
        screenModelScope.launch {
            val currentState = _state.value
            if (currentState.selectedFolderId != null) {
                inventoryRepository.deleteInventoryItem(currentState.selectedFolderId, item.id)
            }
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

    private fun incrementQuantityInventoryItem(item: InventoryItem, additionalQuantity: Int) {
        screenModelScope.launch {
            val currentState = _state.value
            if (currentState.selectedFolderId != null) {
                val updatedItem = item.copy(stockQuantity = item.stockQuantity + additionalQuantity)
                inventoryRepository.updateInventoryItem(currentState.selectedFolderId, updatedItem)
            }
            closeIncrementQuantityDialog()
        }
    }
}