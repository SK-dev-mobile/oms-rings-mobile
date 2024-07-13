package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.launch

@Composable
fun EnhancedTimeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    label: @Composable (() -> Unit)? = null
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val formatted = formatTimeInput(newValue.text, textFieldValue.text)
            val newCursorPosition = when {
                formatted.length > textFieldValue.text.length -> newValue.selection.start + 1
                formatted.length < textFieldValue.text.length -> newValue.selection.start - 1
                else -> newValue.selection.start
            }
            textFieldValue = TextFieldValue(formatted, TextRange(newCursorPosition.coerceIn(0, formatted.length)))
            onValueChange(formatted)
        },
        modifier = modifier,
        label = label,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Select time"
                )
            }
        }
    )

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = { showDialog = false },
            onTimeSelected = { hours, minutes ->
                val newValue = String.format("%02d:%02d", hours, minutes)
                scope.launch {
                    textFieldValue = TextFieldValue(newValue, TextRange(newValue.length))
                    onValueChange(newValue)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    var selectedHours by remember { mutableStateOf(0) }
    var selectedMinutes by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberPicker(
                    value = selectedHours,
                    onValueChange = { selectedHours = it },
                    range = 0..23
                )
                NumberPicker(
                    value = selectedMinutes,
                    onValueChange = { selectedMinutes = it },
                    range = 0..59
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(selectedHours, selectedMinutes) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "▲",
            modifier = Modifier.clickable {
                val newValue = if (value == range.last) range.first else value + 1
                onValueChange(newValue)
            }
        )
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "▼",
            modifier = Modifier.clickable {
                val newValue = if (value == range.first) range.last else value - 1
                onValueChange(newValue)
            }
        )
    }
}

private fun formatTimeInput(input: String, previousInput: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return when {
        digitsOnly.isEmpty() -> ""
        digitsOnly.length == 1 -> digitsOnly
        digitsOnly.length == 2 -> {
            val hours = digitsOnly.toInt().coerceAtMost(23)
            hours.toString().padStart(2, '0')
        }
        digitsOnly.length == 3 -> {
            val hours = digitsOnly.take(2).toInt().coerceAtMost(23)
            "${hours.toString().padStart(2, '0')}:${digitsOnly.last()}"
        }
        digitsOnly.length >= 4 -> {
            val hours = digitsOnly.take(2).toInt().coerceAtMost(23)
            val minutes = digitsOnly.substring(2, 4).toInt().coerceAtMost(59)
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
        }
        else -> previousInput
    }
}