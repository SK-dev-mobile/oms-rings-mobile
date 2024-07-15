package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.internal.BackHandler
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.back_to_categories
import omsringsmobile.composeapp.generated.resources.decrease_quantity
import omsringsmobile.composeapp.generated.resources.increase_quantity
import omsringsmobile.composeapp.generated.resources.item_count
import omsringsmobile.composeapp.generated.resources.no_products_available
import omsringsmobile.composeapp.generated.resources.select_products
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.Folder
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.EmptyStateMessage
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.GenericRow
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens

data class ProductSelectionState(
    val folders: List<Folder>,
    val selectedFolderId: String?,
    val selectedItemId: String?,
    val selectedItems: Map<InventoryItem, Int>
)

sealed interface ProductSelectionEvent {
    data class OnFolderSelected(val folderId: String? = null) : ProductSelectionEvent
    data class OnItemQuantityChanged(val item: InventoryItem, val quantity: Int) : ProductSelectionEvent
}

@OptIn(InternalVoyagerApi::class)
@Composable
fun ProductSelectionSection(
    modifier: Modifier = Modifier,
    state: ProductSelectionState,
    onEvent: (ProductSelectionEvent) -> Unit
) {
    BackHandler(enabled = state.selectedFolderId != null) {
        onEvent(ProductSelectionEvent.OnFolderSelected(null))
    }


    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.select_products),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (state.selectedFolderId == null) {
            FolderList(
                folders = state.folders,
                onFolderClick = { folder -> onEvent(ProductSelectionEvent.OnFolderSelected(folder.id)) }
            )
        } else {
            val selectedFolder = state.folders.find { folder -> folder.id == state.selectedFolderId }
            selectedFolder?.let { folder ->
                InventoryItemList(
                    items = folder.inventoryItems,
                    selectedItems = state.selectedItems,
                    onItemQuantityChanged = { item, quantity ->
                        onEvent(
                            ProductSelectionEvent.OnItemQuantityChanged(
                                item,
                                quantity
                            )
                        )
                    },
                    onBackToFolders = { onEvent(ProductSelectionEvent.OnFolderSelected(null)) }
                )

            }
        }
    }
}

@Composable
private fun FolderList(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit
) {
    if (folders.isEmpty()) {
        EmptyFoldersMessage()
    } else {
        LazyColumn {
            items(folders, key = { it.id }) { folder ->
                FolderRow(
                    folder = folder,
                    onClick = { onFolderClick(folder) }
                )
            }
        }
    }
}

@Composable
private fun FolderRow(
    folder: Folder,
    onClick: () -> Unit
) {
    GenericRow(
        modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
        icon = Icons.Rounded.Folder,
        iconTint = MaterialTheme.colorScheme.primary,
        title = folder.name,
        subtitle = pluralStringResource(Res.plurals.item_count, folder.inventoryItems.size, folder.inventoryItems.size),
        onRowClick = onClick
    )
}

@Composable
private fun InventoryItemList(
    items: List<InventoryItem>,
    selectedItems: Map<InventoryItem, Int>,
    onItemQuantityChanged: (InventoryItem, Int) -> Unit,
    onBackToFolders: () -> Unit
) {
    Column {
        TextButton(
            onClick = onBackToFolders,
            modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall)
        ) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = null)
            Spacer(Dimens.spaceExtraSmall)
            Text(stringResource(Res.string.back_to_categories))
        }
        LazyColumn {
            items(items, key = { it.id }) { item ->
                InventoryItemRow(
                    item = item,
                    quantity = selectedItems[item] ?: 0,
                    onQuantityChanged = { quantity -> onItemQuantityChanged(item, quantity) }
                )
            }
        }
    }
}


@Composable
private fun InventoryItemRow(
    item: InventoryItem,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    GenericRow(
        modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceSmall),
        icon = Icons.Rounded.Inventory,
        iconTint = MaterialTheme.colorScheme.secondary,
        title = item.name,
        actions = {
            QuantityControlRow(
                quantity = quantity,
                onQuantityChanged = onQuantityChanged
            )
        }
    )
}

@Composable
private fun QuantityControlRow(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = Dimens.spaceMedium)
    ) {
        IconButton(
            onClick = { if (quantity > 0) onQuantityChanged(quantity - 1) },
            enabled = quantity > 0
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = stringResource(Res.string.decrease_quantity))
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = Dimens.spaceSmall)
                .align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = { onQuantityChanged(quantity + 1) }
        ) {
            Icon(Icons.Rounded.Add, contentDescription = stringResource(Res.string.increase_quantity))
        }
    }
}

@Composable
private fun EmptyFoldersMessage() {
    EmptyStateMessage(
        modifier = Modifier.fillMaxWidth(),
        icon = Icons.Rounded.Folder,
        title = stringResource(Res.string.no_products_available)
    )
}
