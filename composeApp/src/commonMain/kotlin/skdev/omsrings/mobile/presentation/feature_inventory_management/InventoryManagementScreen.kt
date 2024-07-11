package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
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
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.components.AddItemDialog
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.theme.values.Dimens

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
                FloatingActionButton(onClick = { screenModel.onEvent(Event.ShowAddItemDialog) }) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(Res.string.inventory_management_add_item)
                    )
                }
            }
        ) { paddingValues ->
            if (state.items.isEmpty()) {
                EmptyInventoryMessage(modifier = Modifier.padding(paddingValues))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    items(state.items) { item ->
                        InventoryItemRow(
                            modifier = Modifier.fillMaxWidth(),
                            item = item,
                            onDeleteClick = { screenModel.onEvent(Event.DeleteItem(item)) }
                        )
                    }
                }
            }
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