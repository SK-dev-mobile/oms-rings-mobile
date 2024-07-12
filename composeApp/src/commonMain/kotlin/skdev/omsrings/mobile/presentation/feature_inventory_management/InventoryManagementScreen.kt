package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.CreateFolderDialog
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.AddInventoryItemDialog
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.EmptyStateMessage
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar

// TODO: сделать обновление экрана по swipe
// TODO: починить валидацию
// TODO: навести красоту
object InventoryManagementScreen : BaseScreen("inventory_management_screen") {
    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<InventoryManagementScreenModel>()
        val state by screenModel.state.collectAsState()


        BackHandler(enabled = state.selectedFolderId != null) {
            screenModel.onEvent(Event.SetSelectedInventoryFolder(null))
        }

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = when {
                        state.selectedFolderId == null -> stringResource(Res.string.inventory_management_header)
                        else -> state.folders.find { it.id == state.selectedFolderId }?.name
                            ?: stringResource(Res.string.inventory_management_header)
                    },
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
                    Icon(
                        if (state.selectedFolderId != null) Icons.Rounded.Add else Icons.Rounded.CreateNewFolder,
                        contentDescription = if (state.selectedFolderId != null) stringResource(Res.string.inventory_management_add_item) else "Add Folder"
                    )
                }
            }
        )
        { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (state.selectedFolderId == null) {
                    if (state.folders.isEmpty()) {
                        EmptyFoldersMessage(
                            onAddFolderClick = { screenModel.onEvent(Event.DisplayCreateFolderDialog) }
                        )

                    } else {
                        FolderList(
                            folders = state.folders,
                            onFolderClick = { screenModel.onEvent(Event.SetSelectedInventoryFolder(it.id)) },
                            onDeleteFolder = { screenModel.onEvent(Event.RemoveInventoryFolder(it)) }
                        )
                    }
                } else {
                    val selectedFolder = state.folders.find { it.id == state.selectedFolderId }
                    if (selectedFolder != null) {
                        InventoryItemList(
                            items = selectedFolder.inventoryItems,
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

    }

}


@Composable
fun InventoryItemRow(
    item: InventoryItem,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Inventory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete Item")
            }
        }
    }
}


@Composable
fun FolderList(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit,
    onDeleteFolder: (Folder) -> Unit
) {
    LazyColumn {
        items(folders) { folder ->
            FolderRow(
                folder = folder,
                onClick = { onFolderClick(folder) },
                onDelete = { onDeleteFolder(folder) }
            )
        }
    }
}


@Composable
fun FolderRow(
    folder: Folder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium),
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${folder.inventoryItems.size} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete Folder")
            }
        }
    }
}


@Composable
fun InventoryItemList(
    items: List<InventoryItem>,
    onDeleteItem: (InventoryItem) -> Unit,
    onAddItemClick: () -> Unit
) {
    if (items.isEmpty()) {
        EmptyInventoryItemsMessage(onAddItemClick)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(items) { item ->
                InventoryItemRow(
                    item = item,
                    onDeleteClick = { onDeleteItem(item) }
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