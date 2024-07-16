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
import io.github.aakira.napier.Napier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.ui.components.fields.TextField

data class DateTimeSelectionState(
    val selectedDateTime: Instant? = null,
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

/**
 * This component stays unused but operates as expected.
 */
@Composable
fun DeliveryDateTimeField(
    initialDateTime: Instant?,
    onDateTimeSelected: (Instant) -> Unit,
    deliveryMethod: DeliveryMethod,
    modifier: Modifier = Modifier
) {
    var state by remember {
        mutableStateOf(
            DateTimeSelectionState(selectedDateTime = initialDateTime)
        )
    }

    val handleEvent: (DateTimeEvent) -> Unit = { event ->
        state = when (event) {
            is DateTimeEvent.OnDateTimeFieldClicked -> state.copy(showDatePicker = true)
            is DateTimeEvent.OnDismissDatePicker -> state.copy(showDatePicker = false)
            is DateTimeEvent.OnDismissTimePicker -> state.copy(showTimePicker = false)
            is DateTimeEvent.OnDateSelected -> state.copy(
                tempSelectedDate = event.date,
                showDatePicker = false,
                showTimePicker = true
            )

            is DateTimeEvent.OnTimeSelected -> state.copy(tempSelectedTime = event.time)
            is DateTimeEvent.OnConfirmDateTime -> {
                val newDateTime = combineDateTime(state.tempSelectedDate, state.tempSelectedTime)
                newDateTime?.let { onDateTimeSelected(it) }
                state.copy(
                    selectedDateTime = newDateTime,
                    showTimePicker = false,
                    tempSelectedDate = null,
                    tempSelectedTime = null
                )
            }
        }
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
        value = formatDateTime(state.selectedDateTime),
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
                        handleEvent(DateTimeEvent.OnDateTimeFieldClicked)
                    }
                }
            }
        }
    )

    DateTimePickerDialog(
        state = state,
        onEvent = handleEvent
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    state: DateTimeSelectionState,
    onEvent: (DateTimeEvent) -> Unit
) {
    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDateTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.date?.toEpochDays()
                ?.times(86400000L)

        )
        Napier.d("Selected EpochMillis: ${datePickerState.selectedDateMillis}")

        DatePickerDialog(
            onDismissRequest = { onEvent(DateTimeEvent.OnDismissDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate =
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                        onEvent(DateTimeEvent.OnDateSelected(selectedDate))
                    }
                }) {
                    Text("Next")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (state.showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = state.selectedDateTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.hour ?: 0,
            initialMinute = state.selectedDateTime?.toLocalDateTime(TimeZone.currentSystemDefault())?.minute ?: 0,
            is24Hour = true
        )


        TimePickerDialog(
            onDismissRequest = { onEvent(DateTimeEvent.OnDismissTimePicker) },
            confirmButton = {
                TextButton(onClick = {
                    val selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)
                    onEvent(DateTimeEvent.OnTimeSelected(selectedTime))
                    onEvent(DateTimeEvent.OnConfirmDateTime)
                }) {
                    Text("Confirm")
                }
            }
        ) {
            TimePicker(state = timePickerState)
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


private fun combineDateTime(date: LocalDate?, time: LocalTime?): Instant? {
    if (date == null || time == null) return null
    Napier.d("Selected LocalDateTime: $date, time: $time")
    Napier.d("Selected Instant: ${LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault())}")
    return LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault())
}
