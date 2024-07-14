package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.LocalTime
import skdev.omsrings.mobile.domain.model.DeliveryMethod


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryTimeSelector(
    deliveryMethod: DeliveryMethod,
    initialTime: String? = null,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    var isTimePickerVisible by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.let { parseHour(it) } ?: 12,
        initialMinute = selectedTime?.let { parseMinute(it) } ?: 0
    )

    val annotatedString = buildAnnotatedString {
        append(
            when (deliveryMethod) {
                DeliveryMethod.PICKUP -> "Я заберу заказ в "
                DeliveryMethod.DELIVERY -> "Пожалуйста, доставьте заказ в "
            }
        )

        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(selectedTime ?: "выбрать время")
        }

        addStringAnnotation(
            tag = "TIME_SELECTOR",
            annotation = "open",
            start = length - (selectedTime?.length ?: "выбрать время".length),
            end = length
        )
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TIME_SELECTOR", start = offset, end = offset)
                .firstOrNull()?.let {
                    isTimePickerVisible = true
                }
        }
    )

    if (isTimePickerVisible) {
        TimePickerDialog(
            onDismissRequest = { isTimePickerVisible = false },
            onTimeSelected = { hour, minute ->
                val newTime = formatTime(hour, minute)
                selectedTime = newTime
                onTimeSelected(newTime)
                isTimePickerVisible = false
            },
            timePickerState = timePickerState
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    timePickerState: TimePickerState,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Выбрать время") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismissRequest()
            }) {
                Text("Хорошо")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private fun parseHour(time: String): Int {
    return time.split(":")[0].toInt()
}

private fun parseMinute(time: String): Int {
    return time.split(":")[1].toInt()
}

private fun parseTime(time: String): LocalTime {
    if (time.isEmpty()) return LocalTime(12, 0)
    val (hour, minute) = time.split(":")
    return LocalTime(hour.toInt(), minute.toInt())
}