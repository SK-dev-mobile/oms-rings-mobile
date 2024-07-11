package skdev.omsrings.mobile.presentation.feature_inventory_management

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.inventory_management_add_item
import omsringsmobile.composeapp.generated.resources.inventory_management_header
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.InventoryItem
import skdev.omsrings.mobile.presentation.base.BaseScreen
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
                FloatingActionButton(onClick = {/* handle screen model */ }) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(Res.string.inventory_management_add_item)
                    )
                }
            }
        ) {

        }


    }

}


@Composable
fun InventoryItemRow(
    item: InventoryItem,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceMedium),
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