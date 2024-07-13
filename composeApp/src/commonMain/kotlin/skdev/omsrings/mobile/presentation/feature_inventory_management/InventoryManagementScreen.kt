package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
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
                            screenModel.onEvent(Event.DisplayInventoryItemDialog())
                        } else {
                            screenModel.onEvent(Event.DisplayFolderDialog())
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
                        onCreateFolderClick = { screenModel.onEvent(Event.DisplayFolderDialog()) },
                        onFolderClick = { folder -> screenModel.onEvent(Event.SetSelectedInventoryFolder(folder.id)) },
                        onEditFolderClick = { folder -> screenModel.onEvent(Event.DisplayFolderDialog(folder)) },
                        onDeleteFolder = { folder -> screenModel.onEvent(Event.RemoveInventoryFolder(folder)) }
                    )
                } else {
                    selectedFolder?.let { folder ->
                        InventoryItemList(
                            items = folder.inventoryItems,
                            onIncrementQuantity = { screenModel.onEvent(Event.DisplayIncrementQuantityDialog(it)) },
                            onEditItem = { screenModel.onEvent(Event.DisplayInventoryItemDialog(it)) },
                            onDeleteItem = { screenModel.onEvent(Event.RemoveInventoryItem(it)) },
                            onAddItemClick = { screenModel.onEvent(Event.DisplayInventoryItemDialog()) }
                        )
                    }
                }
            }
        }

        if (state.isFolderDialogVisible) {
            FolderDialog(
                inputField = state.folderField,
                isEditing = state.folderToEdit != null,
                onConfirm = { screenModel.onEvent(Event.CreateOrUpdateFolder) },
                onDismiss = { screenModel.onEvent(Event.CloseFolderDialog) }
            )
        }

        if (state.isItemDialogVisible) {
            InventoryItemDialog(
                inputField = state.itemField,
                isEditing = state.itemToEdit != null,
                onConfirm = { screenModel.onEvent(Event.AddOrUpdateInventoryItem) },
                onDismiss = { screenModel.onEvent(Event.CloseInventoryItemDialog) }
            )
        }

        if (state.incrementQuantityDialogVisible) {
            state.selectedItem?.let { item ->
                IncrementQuantityDialog(
                    item = item,
                    inputField = state.quantityField,
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
fun FolderList(
    folders: List<Folder>,
    onCreateFolderClick: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    onEditFolderClick: (Folder) -> Unit,
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
                val onEditHandler = remember(folder) { { onEditFolderClick(folder) } }
                val onDeleteHandler = remember(folder) { { onDeleteFolder(folder) } }

                FolderRow(
                    folder = folder,
                    onClick = onClickHandler,
                    onEdit = onEditHandler,
                    onDelete = onDeleteHandler
                )
            }
        }
    }
}


@Composable
fun InventoryItemList(
    items: List<InventoryItem>,
    onIncrementQuantity: (InventoryItem) -> Unit,
    onEditItem: (InventoryItem) -> Unit,
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
                key = { it.id }
            ) { item ->
                val onIncrementHandler = remember(item) { { onIncrementQuantity(item) } }
                val onEditHandler = remember(item) { { onEditItem(item) } }
                val onDeleteHandler = remember(item) { { onDeleteItem(item) } }
                InventoryItemRow(
                    item = item,
                    onIncrementQuantity = onIncrementHandler,
                    onEditClick = onEditHandler,
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
fun FolderDialog(
    inputField: FormField<String, StringResource>,
    isEditing: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseInputDialog(
        titleRes = if (isEditing) Res.string.update_folder_dialog_title else Res.string.create_folder_dialog_title,
        confirmTextRes = if (isEditing) Res.string.update_button_text else Res.string.create_button_text,
        cancelTextRes = Res.string.cancel_button_text,
        labelRes = Res.string.folder_name_label,
        inputField = inputField,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun InventoryItemDialog(
    inputField: FormField<String, StringResource>,
    isEditing: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseInputDialog(
        titleRes = if (isEditing) Res.string.update_item_dialog_title else Res.string.add_item_dialog_title,
        confirmTextRes = if (isEditing) Res.string.update_button_text else Res.string.add_item_dialog_add_button,
        cancelTextRes = Res.string.add_item_dialog_cancel_button,
        labelRes = Res.string.add_item_dialog_item_name,
        inputField = inputField,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
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
fun FolderRow(
    folder: Folder,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GenericRow(
        icon = Icons.Rounded.Folder,
        iconTint = MaterialTheme.colorScheme.primary,
        title = folder.name,
        subtitle = pluralStringResource(Res.plurals.item_count, folder.inventoryItems.size, folder.inventoryItems.size),
        onRowClick = onClick,
        primaryAction = {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Rounded.Edit,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "test"
                )
            }
        },
        secondaryAction = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(Res.string.delete)
                )
            }
        }
    )
}

@Composable
fun InventoryItemRow(
    item: InventoryItem,
    onIncrementQuantity: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    GenericRow(
        icon = Icons.Rounded.Inventory,
        iconTint = MaterialTheme.colorScheme.secondary,
        title = item.name,
        subtitle = stringResource(Res.string.item_stock, item.stockQuantity),
        primaryAction = {
            IconButton(onClick = onIncrementQuantity) {
                Icon(
                    Icons.Rounded.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(Res.string.increment_quantity_label)
                )
            }
        },
        secondaryAction = {
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Rounded.Edit,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = ""
                )
            }
        },
        tertiaryAction = {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Rounded.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = stringResource(Res.string.delete)
                )
            }
        }
    )
}