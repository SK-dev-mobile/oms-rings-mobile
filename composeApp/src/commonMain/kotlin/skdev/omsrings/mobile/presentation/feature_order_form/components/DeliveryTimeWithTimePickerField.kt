package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTimeWithTimePickerField(
    timeField: FormField<String, StringResource>,
    onTimeSelected: (String) -> Unit,
    deliveryMethod: DeliveryMethod,
    modifier: Modifier = Modifier
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val (timeValue, timeValueSetter) = timeField.data.collectAsMutableState()
    val timeError by timeField.error.collectAsState()

    val text = when (deliveryMethod) {
        DeliveryMethod.PICKUP -> "Заберу в "
        DeliveryMethod.DELIVERY -> "Доставить в "
    }


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Dimens.spaceSmall)
        TextField(
            value = timeValue,
            onValueChange = {
                timeValueSetter(it)
                onTimeSelected(it)
            },
            modifier = Modifier,
            readOnly = true,
            isError = timeError != null,
            supportingText = timeError?.let { SupportingText(it) },
            placeholder = { Text("Select Time") },
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is androidx.compose.foundation.interaction.PressInteraction.Release) {
                            showTimePickerDialog = true
                        }
                    }
                }
            }
        )
    }
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = timeValue.let { parseTime(it).hour },
            initialMinute = timeValue.let { parseTime(it).minute },
            is24Hour = true
        )

        TimePickerDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    timeValueSetter(formatTime(LocalTime(timePickerState.hour, timePickerState.minute)))
                    onTimeSelected(formatTime(LocalTime(timePickerState.hour, timePickerState.minute)))
                    showTimePickerDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select time") },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

private fun formatTime(time: LocalTime): String {
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

private fun parseTime(time: String): LocalTime {
    if (time.isEmpty()) return LocalTime(12, 0)
    val (hour, minute) = time.split(":")
    return LocalTime(hour.toInt(), minute.toInt())
}