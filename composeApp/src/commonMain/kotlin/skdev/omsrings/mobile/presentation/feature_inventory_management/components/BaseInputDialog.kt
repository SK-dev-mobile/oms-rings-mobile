package skdev.omsrings.mobile.presentation.feature_inventory_management.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.current_quantity
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
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    currentValue: Int? = null,
    showNewValuePreview: Boolean = false,
    previewStringRes: StringResource? = null
) {
    val (inputValue, inputSetter) = inputField.data.collectAsMutableState()
    val inputError by inputField.error.collectAsState()
    val isValid by inputField.isValid.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleRes)) },
        text = {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentValue?.let {
                    Text(
                        text = stringResource(Res.string.current_quantity, it),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Dimens.spaceMedium)
                }
                TextField(
                    value = inputValue,
                    onValueChange = {
                        inputSetter(it)
                        inputField.validate()
                    },
                    isError = inputError != null,
                    label = { Text(stringResource(labelRes)) },
                    
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Dimens.spaceSmall)
                inputError?.let {
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (showNewValuePreview && previewStringRes != null) {
                    Spacer(Dimens.spaceMedium)
                    Text(
                        text = stringResource(
                            previewStringRes,
                            (currentValue ?: 0) + (inputValue.toIntOrNull() ?: 0)
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(inputValue) },
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