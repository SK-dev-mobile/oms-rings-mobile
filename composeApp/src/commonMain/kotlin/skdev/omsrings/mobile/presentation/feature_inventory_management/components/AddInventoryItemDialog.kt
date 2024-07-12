package skdev.omsrings.mobile.presentation.feature_inventory_management.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenModel
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun AddInventoryItemDialog(
    newItemField: FormField<String, StringResource>,
    screenModel: InventoryManagementScreenModel,
    modifier: Modifier = Modifier
) {
    val (newItemValue, newItemSetter) = newItemField.data.collectAsMutableState()
    val newItemError by newItemField.error.collectAsState()
    val isValid by newItemField.isValid.collectAsState()

    AlertDialog(
        onDismissRequest = { screenModel.onEvent(Event.CloseAddInventoryItemDialog) },
        title = { Text(stringResource(Res.string.add_item_dialog_title)) },
        text = {
            Column(modifier = modifier.fillMaxWidth()) {
                TextField(
                    value = newItemValue,
                    onValueChange = {
                        newItemSetter(it)
                        newItemField.validate()
                    },
                    isError = newItemError != null,
                    supportingText = { newItemError?.let { Text(stringResource(it)) } },
                    label = { Text(stringResource(Res.string.add_item_dialog_item_name)) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { screenModel.onEvent(Event.AddInventoryItem) },
                enabled = isValid
            ) {
                Text(stringResource(Res.string.add_item_dialog_add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = { screenModel.onEvent(Event.CloseAddInventoryItemDialog) }) {
                Text(stringResource(Res.string.add_item_dialog_cancel_button))
            }
        }
    )
}