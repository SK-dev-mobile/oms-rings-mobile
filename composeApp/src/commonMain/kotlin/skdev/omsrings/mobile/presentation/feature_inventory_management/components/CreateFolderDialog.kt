package skdev.omsrings.mobile.presentation.feature_inventory_management.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import omsringsmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenModel
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun CreateFolderDialog(
    newFolderField: FormField<String, StringResource>,
    screenModel: InventoryManagementScreenModel,
    modifier: Modifier = Modifier
) {
    val (newFolderValue, newFolderSetter) = newFolderField.data.collectAsMutableState()
    val newFolderError: StringResource? by newFolderField.error.collectAsState()
    val isValid by newFolderField.isValid.collectAsState()

    AlertDialog(
        onDismissRequest = { screenModel.onEvent(Event.CloseCreateFolderDialog) },
        title = { Text(stringResource(Res.string.create_folder_dialog_title)) },
        text = {
            Column(modifier = modifier.fillMaxWidth()) {
                TextField(
                    value = newFolderValue,
                    onValueChange = {
                        newFolderSetter(it)
                        newFolderField.validate()
                    },
                    isError = newFolderError != null,
                    label = { Text(stringResource(Res.string.folder_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Dimens.spaceSmall)
                newFolderError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = Dimens.spaceMedium)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { screenModel.onEvent(Event.CreateInventoryFolder) }, enabled = isValid) {
                Text(stringResource(Res.string.create_button_text))
            }
        },
        dismissButton = {
            TextButton(onClick = { screenModel.onEvent(Event.CloseCreateFolderDialog) }) {
                Text(stringResource(Res.string.cancel_button_text))
            }
        }
    )
}