package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.koin.koinScreenModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.inventory_management_add_item
import omsringsmobile.composeapp.generated.resources.inventory_management_emptry_items_message
import omsringsmobile.composeapp.generated.resources.inventory_management_header
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.AddFolderDialog
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.AddItemDialog
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.theme.values.Dimens

// TODO: сделать обновление экрана по swipe
// TODO: сделать папки
// TODO: сделать удаление папок
// TODO: починить валидацию
// TODO: навести красоту
object InventoryManagementScreen : BaseScreen("inventory_management_screen") {
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<InventoryManagementScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.inventory_management_header),
                    onNavigationClicked = {/* Handle pop navigator */ }
                )
            },
            floatingActionButton = {
                if (state.selectedFolderId != null) {
                    FloatingActionButton(onClick = { screenModel.onEvent(Event.ShowAddItemDialog) }) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = stringResource(Res.string.inventory_management_add_item)
                        )
                    }
                } else {
                    FloatingActionButton(onClick = { screenModel.onEvent(Event.ShowAddFolderDialog) }) {
                        Icon(
                            Icons.Rounded.CreateNewFolder,
                            contentDescription = "Add folder"
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                if (state.selectedFolderId == null) {
                    FolderList(
                        folders = state.folders,
                        onFolderClick = { screenModel.onEvent(Event.SelectFolder(it.id)) },
                        onDeleteFolder = { screenModel.onEvent(Event.DeleteFolder(it)) }
                    )
                } else {
                    val selectedFolder = state.folders.find { it.id == state.selectedFolderId }
                    if (selectedFolder != null) {
                        ItemList(
                            folderName = selectedFolder.name,
                            items = selectedFolder.items,
                            onBackClick = { screenModel.onEvent(Event.SelectFolder(null)) },
                            onDeleteItem = { screenModel.onEvent(Event.DeleteItem(it)) }
                        )
                    }
                }
            }
        }

        if (state.isAddingFolder) {
            AddFolderDialog(state.newFolderField, screenModel)
        }

        if (state.isAddingItem) {
            AddItemDialog(state.newItemField, screenModel)
        }

    }

}


@Composable
fun InventoryItemRow(
    modifier: Modifier = Modifier,
    item: InventoryItem,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(Dimens.spaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@Composable
fun EmptyInventoryMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.inventory_management_emptry_items_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
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
            FolderItem(
                folder = folder,
                onClick = { onFolderClick(folder) },
                onDelete = { onDeleteFolder(folder) }
            )
        }
    }
}


@Composable
fun FolderItem(
    folder: Folder,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(folder.name) },
        supportingContent = { Text("${folder.items.size} items") },
        leadingContent = {
            Icon(Icons.Rounded.Folder, contentDescription = null)
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete Folder")
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemList(
    folderName: String,
    items: List<InventoryItem>,
    onBackClick: () -> Unit,
    onDeleteItem: (InventoryItem) -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(folderName) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn {
            items(items) { item ->
                InventoryItemRow(
                    item = item,
                    onDeleteClick = { onDeleteItem(item) }
                )
            }
        }
    }
}