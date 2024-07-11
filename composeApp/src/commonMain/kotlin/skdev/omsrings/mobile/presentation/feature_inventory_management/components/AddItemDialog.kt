package skdev.omsrings.mobile.presentation.feature_inventory_management.components


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenModel
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun AddItemDialog(newItemField: FormField<String, StringResource>, screenModel: InventoryManagementScreenModel) {
    val (newItemValue, newItemSetter) = newItemField.data.collectAsMutableState()
    val newItemError: StringResource? by newItemField.error.collectAsState()

    AlertDialog(
        onDismissRequest = { /* screenModel.onEvent(Event.HideAddItemDialog) */ },
        title = { Text(stringResource(Res.string.add_item_dialog_title)) },
        text = {
            TextField(
                value = newItemValue,
                onValueChange = newItemSetter,
                isError = newItemError != null,
                supportingText = SupportingText(newItemError?.let { stringResource(it) }),
                label = { Text(stringResource(Res.string.add_item_dialog_item_name)) }
            )
        },
        confirmButton = {
            Button(onClick = { /* screenModel.onEvent(Event.ConfirmAddItem) */ }) {
                Text(stringResource(Res.string.add_item_dialog_add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = { /*  screenModel.onEvent(Event.HideAddItemDialog) */ }) {
                Text(stringResource(Res.string.add_item_dialog_cancel_button))
            }
        }
    )
}