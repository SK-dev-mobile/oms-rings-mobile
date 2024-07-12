package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.BaseInputDialog
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.EmptyStateMessage
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.GenericRow
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField

// TODO: сделать обновление экрана по swipe
// TODO: навести красоту
object InventoryManagementScreen : BaseScreen("inventory_management_screen") {
    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<InventoryManagementScreenModel>()
        val state by screenModel.state.collectAsState()


        val selectedFolder = remember(state.selectedFolderId, state.folders) {
            state.folders.find { it.id == state.selectedFolderId }
        }

        val defaultHeaderText = stringResource(Res.string.inventory_management_header)
        val title = if (state.selectedFolderId == null) {
            defaultHeaderText
        } else {
            selectedFolder?.name ?: defaultHeaderText
        }

        BackHandler(enabled = state.selectedFolderId != null) {
            screenModel.onEvent(Event.SetSelectedInventoryFolder(null))
        }

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = title,
                    onNavigationClicked = {
                        if (state.selectedFolderId == null) {
                            /* handle pop */
                        } else {
                            screenModel.onEvent(Event.SetSelectedInventoryFolder(null))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (state.selectedFolderId != null) {
                            screenModel.onEvent(Event.DisplayAddInventoryItemDialog)
                        } else {
                            screenModel.onEvent(Event.DisplayCreateFolderDialog)
                        }
                    }
                ) {
                    // Используем remember для кэширования иконки
                    val icon = remember(state.selectedFolderId) {
                        if (state.selectedFolderId != null) Icons.Rounded.Add else Icons.Rounded.CreateNewFolder
                    }
                    val contentDescription = if (state.selectedFolderId != null) {
                        stringResource(Res.string.inventory_management_add_item)
                    } else {
                        stringResource(Res.string.add_folder)
                    }
                    Icon(icon, contentDescription = contentDescription)
                }
            }
        )
        { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (state.selectedFolderId == null) {
                    FolderList(
                        folders = state.folders,
                        onFolderClick = { screenModel.onEvent(Event.SetSelectedInventoryFolder(it.id)) },
                        onDeleteFolder = { screenModel.onEvent(Event.RemoveInventoryFolder(it)) },
                        onCreateFolderClick = { screenModel.onEvent(Event.DisplayCreateFolderDialog) }
                    )
                } else {
                    selectedFolder?.let { folder ->
                        InventoryItemList(
                            items = folder.inventoryItems,
                            onInventoryItemClick = { screenModel.onEvent(Event.DisplayIncrementQuantityDialog(it)) },
                            onDeleteItem = { screenModel.onEvent(Event.RemoveInventoryItem(it)) },
                            onAddItemClick = { screenModel.onEvent(Event.DisplayAddInventoryItemDialog) }
                        )
                    }
                }
            }
        }

        if (state.isAddingFolder) {
            CreateFolderDialog(
                inputField = state.newFolderField,
                onConfirm = { screenModel.onEvent(Event.CreateInventoryFolder) },
                onDismiss = { screenModel.onEvent(Event.CloseCreateFolderDialog) }
            )
            BaseInputDialog(
                titleRes = Res.string.create_folder_dialog_title,
                confirmTextRes = Res.string.create_button_text,
                cancelTextRes = Res.string.cancel_button_text,
                labelRes = Res.string.folder_name_label,
                inputField = state.newFolderField,
                onConfirm = { screenModel.onEvent(Event.CreateInventoryFolder) },
                onDismiss = { screenModel.onEvent(Event.CloseCreateFolderDialog) }
            )
        }

        if (state.isAddingItem) {
            AddInventoryItemDialog(
                inputField = state.newItemField,
                onConfirm = { screenModel.onEvent(Event.AddInventoryItem) },
                onDismiss = { screenModel.onEvent(Event.CloseAddInventoryItemDialog) }
            )
        }

        if (state.isIncrementQuantity) {
            state.selectedItem?.let { item ->
                IncrementQuantityDialog(
                    item = item,
                    inputField = state.newQuantityField,
                    onConfirm = { incrementAmount ->
                        screenModel.onEvent(
                            Event.IncrementQuantityInventoryItem(
                                item, incrementAmount.toIntOrNull() ?: 0
                            )
                        )
                    },
                    onDismiss = { screenModel.onEvent(Event.CloseIncrementQuantityDialog) },
                )
            }
        }

    }

}

@Composable
fun IncrementQuantityDialog(
    item: InventoryItem,
    inputField: FormField<String, StringResource>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BaseInputDialog(
        titleRes = Res.string.increment_quantity_dialog_title,
        confirmTextRes = Res.string.confirm_increment,
        cancelTextRes = Res.string.cancel_button_text,
        labelRes = Res.string.increment_quantity_label,
        inputField = inputField,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        keyboardType = KeyboardType.Number,
        currentValue = item.stockQuantity,
        showNewValuePreview = true,
        previewStringRes = Res.string.new_quantity_preview
    )
}

@Composable
fun FolderList(
    folders: List<Folder>,
    onCreateFolderClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    onDeleteFolder: (Folder) -> Unit
) {
    if (folders.isEmpty()) {
        EmptyFoldersMessage(onAddFolderClick = onCreateFolderClick)
    } else {
        LazyColumn {
            items(
                items = folders,
                key = { it.id } // Используем key для оптимизации обновлений списка
            ) { folder ->
                // Используем remember для кэширования колбэков
                val onClickHandler = remember(folder) { { onFolderClick(folder) } }
                val onDeleteHandler = remember(folder) { { onDeleteFolder(folder) } }

                FolderRow(
                    folder = folder,
                    onClick = onClickHandler,
                    onDelete = onDeleteHandler
                )
            }
        }
    }
}


@Composable
fun InventoryItemList(
    items: List<InventoryItem>,
    onInventoryItemClick: (InventoryItem) -> Unit,
    onDeleteItem: (InventoryItem) -> Unit,
    onAddItemClick: () -> Unit
) {
    if (items.isEmpty()) {
        EmptyInventoryItemsMessage(onAddItemClick)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Dimens.spaceSmall)
        ) {
            items(
                items = items,
                key = { it.id } // Используем key для оптимизации обновлений списка
            ) { item ->
                // Используем remember для кэширования колбэка
                val onClickHandler = remember(item) { { onInventoryItemClick(item) } }
                val onDeleteHandler = remember(item) { { onDeleteItem(item) } }
                InventoryItemRow(
                    item = item,
                    onClick = onClickHandler,
                    onDeleteClick = onDeleteHandler
                )
            }
        }
    }
}


// Использование для пустого списка папок
@Composable
fun EmptyFoldersMessage(onAddFolderClick: () -> Unit) {
    EmptyStateMessage(
        modifier = Modifier.fillMaxSize(),
        icon = Icons.Rounded.Folder,
        title = stringResource(Res.string.empty_folders_title),
        description = stringResource(Res.string.empty_folders_description),
        actionText = stringResource(Res.string.add_folder),
        onActionClick = onAddFolderClick
    )
}

// Использование для пустого списка товаров
@Composable
fun EmptyInventoryItemsMessage(onAddItemClick: () -> Unit) {
    EmptyStateMessage(
        modifier = Modifier.fillMaxSize(),
        icon = Icons.Rounded.Inventory,
        title = stringResource(Res.string.empty_inventory_items_title),
        description = stringResource(Res.string.empty_inventory_items_description),
        actionText = stringResource(Res.string.add_inventory_item),
        onActionClick = onAddItemClick
    )
}


@Composable
fun CreateFolderDialog(
    inputField: FormField<String, StringResource>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseInputDialog(
        titleRes = Res.string.create_folder_dialog_title,
        confirmTextRes = Res.string.create_button_text,
        cancelTextRes = Res.string.cancel_button_text,
        labelRes = Res.string.folder_name_label,
        inputField = inputField,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun AddInventoryItemDialog(
    inputField: FormField<String, StringResource>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseInputDialog(
        titleRes = Res.string.add_item_dialog_title,
        confirmTextRes = Res.string.add_item_dialog_add_button,
        cancelTextRes = Res.string.add_item_dialog_cancel_button,
        labelRes = Res.string.add_item_dialog_item_name,
        inputField = inputField,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}


@Composable
fun FolderRow(
    folder: Folder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GenericRow(
        icon = Icons.Rounded.Folder,
        iconTint = MaterialTheme.colorScheme.primary,
        title = folder.name,
        subtitle = pluralStringResource(Res.plurals.item_count, folder.inventoryItems.size, folder.inventoryItems.size),
        onRowClick = onClick,
        onDeleteClick = onDelete
    )
}

@Composable
fun InventoryItemRow(
    item: InventoryItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    GenericRow(
        icon = Icons.Rounded.Inventory,
        iconTint = MaterialTheme.colorScheme.secondary,
        title = item.name,
        onRowClick = onClick,
        onDeleteClick = onDeleteClick,
        subtitle = stringResource(Res.string.item_stock, item.stockQuantity)
    )
}