package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun DeliveryTimeSelector(
    deliveryMethod: DeliveryMethod,
    timeField: FormField<String, StringResource>,
    modifier: Modifier = Modifier
) {
    val (timeValue, onTimeValueChange) = timeField.data.collectAsMutableState()
    val timeError by timeField.error.collectAsState()

    Column(modifier = modifier) {
        TimeSelector(
            deliveryMethod = deliveryMethod,
            timeValue = timeValue,
            onTimeSelected = onTimeValueChange
        )
        timeError?.let { error ->
            ErrorMessage(error = error)

        }
    }
}

@Composable
private fun ErrorMessage(error: StringResource) {
    Text(
        text = stringResource(error),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(start = Dimens.spaceMedium, top = Dimens.spaceSmall)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSelector(
    deliveryMethod: DeliveryMethod,
    timeValue: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isTimePickerVisible by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = timeValue.parseHour(),
        initialMinute = timeValue.parseMinute(),
        is24Hour = true
    )

    Card(
        onClick = { isTimePickerVisible = true },
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spaceMedium).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = "Время доставки"
            )
            Spacer(Dimens.spaceSmall)
            Text(
                text = buildTimeSelectorText(deliveryMethod, timeValue),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (isTimePickerVisible) {
        TimePickerDialog(
            onDismissRequest = { isTimePickerVisible = false },
            onTimeSelected = { hour, minute ->
                onTimeSelected(formatTime(hour, minute))
                isTimePickerVisible = false
            },
            timePickerState = timePickerState
        )
    }


}

@Composable
private fun buildTimeSelectorText(deliveryMethod: DeliveryMethod, timeValue: String) = buildAnnotatedString {
    append(
        when (deliveryMethod) {
            DeliveryMethod.PICKUP -> "Я заберу заказ в "
            DeliveryMethod.DELIVERY -> "Пожалуйста, доставьте заказ в "
        }
    )

    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
        append(timeValue.ifBlank { "выбрать время" })
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
        text = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute) }) {
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

private fun String.parseHour(): Int = if (isBlank()) 12 else split(":")[0].toInt()
private fun String.parseMinute(): Int = if (isBlank()) 0 else split(":")[1].toInt()
private fun formatTime(hour: Int, minute: Int): String =
    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"