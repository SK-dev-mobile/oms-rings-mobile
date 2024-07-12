package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.BaseInputDialog
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.EmptyStateMessage
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.GenericRow
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

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
            CreateFolderDialog(state.newFolderField, screenModel)
        }

        if (state.isAddingItem) {
            AddInventoryItemDialog(state.newItemField, screenModel)
        }

        if (state.isIncrementQuantity) {
            state.selectedItem?.let { item ->
                IncrementQuantityDialog(
                    item = item,
                    quantityField = state.newQuantityField,
                    onConfirm = { incrementAmount ->
                        screenModel.onEvent(Event.IncrementQuanitityInventoryItem(item, incrementAmount))
                    },
                    onDismiss = { screenModel.onEvent(Event.CloseIncrementQuantityDialog) }
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
fun AddInventoryItemDialog(
    newItemField: FormField<String, StringResource>,
    screenModel: InventoryManagementScreenModel,
    modifier: Modifier = Modifier
) {
    BaseInputDialog(
        titleRes = Res.string.add_item_dialog_title,
        confirmTextRes = Res.string.add_item_dialog_add_button,
        cancelTextRes = Res.string.add_item_dialog_cancel_button,
        labelRes = Res.string.add_item_dialog_item_name,
        inputField = newItemField,
        onConfirm = { screenModel.onEvent(Event.AddInventoryItem) },
        onDismiss = { screenModel.onEvent(Event.CloseAddInventoryItemDialog) },
        modifier = modifier
    )
}

@Composable
fun CreateFolderDialog(
    newFolderField: FormField<String, StringResource>,
    screenModel: InventoryManagementScreenModel,
    modifier: Modifier = Modifier
) {
    BaseInputDialog(
        titleRes = Res.string.create_folder_dialog_title,
        confirmTextRes = Res.string.create_button_text,
        cancelTextRes = Res.string.cancel_button_text,
        labelRes = Res.string.folder_name_label,
        inputField = newFolderField,
        onConfirm = { screenModel.onEvent(Event.CreateInventoryFolder) },
        onDismiss = { screenModel.onEvent(Event.CloseCreateFolderDialog) },
        modifier = modifier
    )
}

@Composable
fun IncrementQuantityDialog(
    item: InventoryItem,
    quantityField: FormField<String, StringResource>,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (quantity, setQuantity) = quantityField.data.collectAsMutableState()
    val quantityError by quantityField.error.collectAsState()
    val isValid by quantityField.isValid.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.increment_quantity_dialog_title)) },
        text = {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.current_quantity, item.stockQuantity),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Dimens.spaceMedium)
                TextField(
                    value = quantity,
                    onValueChange = {
                        setQuantity(it)
                        quantityField.validate()
                    },
                    isError = quantityError != null,
                    label = { Text(stringResource(Res.string.increment_quantity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Dimens.spaceSmall)
                quantityError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Dimens.spaceMedium)
                Text(
                    text = stringResource(
                        Res.string.new_quantity_preview,
                        item.stockQuantity + (quantity.toIntOrNull() ?: 0)
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(quantity.toIntOrNull() ?: 0) },
                enabled = isValid
            ) {
                Text(stringResource(Res.string.confirm_increment))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button_text))
            }
        }
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
        subtitle = stringResource(Res.string.item_count, folder.inventoryItems.size),
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