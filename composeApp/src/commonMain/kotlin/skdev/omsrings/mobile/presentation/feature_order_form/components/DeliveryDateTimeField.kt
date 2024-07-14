package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.ui.components.fields.TextField

data class DateTimeSelectionState(
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val tempSelectedDate: LocalDate? = null,
    val tempSelectedTime: LocalTime? = null
)

sealed interface DateTimeEvent {
    object OnDateTimeFieldClicked : DateTimeEvent
    object OnDismissDatePicker : DateTimeEvent
    object OnDismissTimePicker : DateTimeEvent
    data class OnDateSelected(val date: LocalDate) : DateTimeEvent
    data class OnTimeSelected(val time: LocalTime) : DateTimeEvent
    object OnConfirmDateTime : DateTimeEvent
}

@Composable
fun DeliveryDateTimeField(
    dateTime: Instant,
    onEvent: (DateTimeEvent) -> Unit,
    deliveryMethod: DeliveryMethod,
    dateTimeSelectionState: DateTimeSelectionState,
    modifier: Modifier = Modifier
) {
    val formattedDateTime by remember(dateTime) {
        derivedStateOf { formatDateTime(dateTime) }
    }

    val label by remember(deliveryMethod) {
        derivedStateOf {
            when (deliveryMethod) {
                DeliveryMethod.DELIVERY -> "Delivery Date and Time"
                DeliveryMethod.PICKUP -> "Pickup Date and Time"
            }
        }
    }
    TextField(
        value = formattedDateTime,
        onValueChange = {},
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Rounded.DateRange, contentDescription = "Date and Time") },
        placeholder = { Text("Select date and time") },
        readOnly = true,
        modifier = modifier,
        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    if (interaction is PressInteraction.Release) {
                        onEvent(DateTimeEvent.OnDateTimeFieldClicked)
                    }
                }
            }
        }
    )

    DateTimePickerDialog(
        dateTimeSelectionState = dateTimeSelectionState,
        onEvent = onEvent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    dateTimeSelectionState: DateTimeSelectionState,
    onEvent: (DateTimeEvent) -> Unit
) {
    if (dateTimeSelectionState.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onEvent(DateTimeEvent.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    dateTimeSelectionState.tempSelectedDate?.let {
                        onEvent(DateTimeEvent.OnDateSelected(it))
                    }
                }) {
                    Text("Next")
                }
            },
        ) {
            DatePicker(
                state = rememberDatePickerState()
            )
        }
    }

    if (dateTimeSelectionState.showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { onEvent(DateTimeEvent.OnDismissTimePicker) },
            confirmButton = {
                TextButton(onClick = {
                    dateTimeSelectionState.tempSelectedTime?.let {
                        onEvent(DateTimeEvent.OnTimeSelected(it))
                    }
                }) {
                    Text("Confirm")
                }
            }
        ) {
            TimePicker(
                state = rememberTimePickerState()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select time") },
        text = content,
        confirmButton = confirmButton,
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


private fun formatDateTime(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = localDateTime.year.toString()
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day.$month.$year $hour:$minute"
}