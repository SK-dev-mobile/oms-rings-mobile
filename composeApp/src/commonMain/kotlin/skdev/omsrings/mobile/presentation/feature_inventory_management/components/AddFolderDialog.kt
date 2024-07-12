package skdev.omsrings.mobile.presentation.feature_inventory_management.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenModel
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun AddFolderDialog(
    newFolderField: FormField<String, StringResource>,
    screenModel: InventoryManagementScreenModel
) {
    val (newFolderValue, newFolderSetter) = newFolderField.data.collectAsMutableState()
    val newFolderError: StringResource? by newFolderField.error.collectAsState()

    AlertDialog(
        onDismissRequest = { screenModel.onEvent(Event.CloseCreateFolderDialog) },
        title = { Text("Add Folder") },
        text = {
            TextField(
                value = newFolderValue,
                onValueChange = newFolderSetter,
                isError = newFolderError != null,
                supportingText = SupportingText(newFolderError?.let { stringResource(it) }),
                label = { Text("Folder Name") }
            )
        },
        confirmButton = {
            Button(onClick = { screenModel.onEvent(Event.CreateInventoryFolder) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { screenModel.onEvent(Event.CloseCreateFolderDialog) }) {
                Text("Cancel")
            }
        }
    )
}