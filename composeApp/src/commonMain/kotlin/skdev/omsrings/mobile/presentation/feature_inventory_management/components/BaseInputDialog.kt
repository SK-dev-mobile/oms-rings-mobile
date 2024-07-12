package skdev.omsrings.mobile.presentation.feature_inventory_management.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun BaseInputDialog(
    titleRes: StringResource,
    confirmTextRes: StringResource,
    cancelTextRes: StringResource,
    labelRes: StringResource,
    inputField: FormField<String, StringResource>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (inputValue, inputSetter) = inputField.data.collectAsMutableState()
    val inputError by inputField.error.collectAsState()
    val isValid by inputField.isValid.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleRes)) },
        text = {
            Column(modifier = modifier.fillMaxWidth()) {
                TextField(
                    value = inputValue,
                    onValueChange = {
                        inputSetter(it)
                        inputField.validate()
                    },
                    isError = inputError != null,
                    label = { Text(stringResource(labelRes)) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceMedium)
                )
                Spacer(Dimens.spaceSmall)
                inputError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = Dimens.spaceMedium)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = isValid
            ) {
                Text(stringResource(confirmTextRes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(cancelTextRes))
            }
        }
    )
}