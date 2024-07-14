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
    val initialDateTime: Instant? = null,
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
    initialDateTime: Instant?,
    onDateTimeSelected: (Instant) -> Unit,
    deliveryMethod: DeliveryMethod,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(DateTimeSelectionState(initialDateTime)) }


    val label by remember(deliveryMethod) {
        derivedStateOf {
            when (deliveryMethod) {
                DeliveryMethod.DELIVERY -> "Delivery Date and Time"
                DeliveryMethod.PICKUP -> "Pickup Date and Time"
            }
        }
    }
    TextField(
        value = formatDateTime(state.initialDateTime),
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
                        state = state.copy(showDatePicker = true)
                    }
                }
            }
        }
    )

    DateTimePickerDialog(
        state = state,
        onDateTimeChange = { newState ->
            state = newState
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    state: DateTimeSelectionState,
    onDateTimeChange: (DateTimeSelectionState) -> Unit
) {
    if (state.showDatePicker) {
        
        DatePickerDialog(
            onDismissRequest = {
                val newState = state.copy(showDatePicker = false)
                onDateTimeChange(newState)
            },
            confirmButton = {
                TextButton(onClick = {
                    state.tempSelectedDate?.let {
                        val newState = state.copy(
                            showDatePicker = false,
                            showTimePicker = true,
                        )
                        onDateTimeChange(newState)
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

    if (state.showTimePicker) {
        TimePickerDialog(
            onDismissRequest = {
                val newState = state.copy(showTimePicker = false)
                onDateTimeChange(newState)
            },
            confirmButton = {
                TextButton(onClick = {
                    state.tempSelectedTime?.let {
                        val newState = state.copy(
                            showTimePicker = false,
                            initialDateTime = state.initialDateTime,
                            tempSelectedTime = null
                        )
                        onDateTimeChange(newState)
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

    val state = rememberTimePickerState()

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


private fun formatDateTime(instant: Instant?): String {
    if (instant == null) return ""
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = localDateTime.year.toString()
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day.$month.$year $hour:$minute"
}

private fun getLabelText(deliveryMethod: DeliveryMethod): String =
    when (deliveryMethod) {
        DeliveryMethod.DELIVERY -> "Delivery Date and Time"
        DeliveryMethod.PICKUP -> "Pickup Date and Time"
    }