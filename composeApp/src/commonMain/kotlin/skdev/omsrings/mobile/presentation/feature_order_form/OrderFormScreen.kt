package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreenContract.Event
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


// TODO: добавить валидацию полей
// TODO: сделать часть в форме с выбором количества товаров
// TODO: сделать функцию переноса заказа на другой день
// TODO: сделать редактирование заказа
// TODO: сделать отправку заказа на сервер
// TODO: сделать отображение ошибок
// TODO: сделать отображение загрузки
// TODO: привести в порядок строковые ресурсы

object OrderFormScreen : BaseScreen("order_form_screen") {
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<OrderFormScreenModel>()
        val state by screenModel.state.collectAsState()

        OrderFormContent(
            state = state,
            onEvent = screenModel::onEvent
        )
    }

}


@Composable
private fun OrderFormContent(
    state: OrderFormScreenContract.State,
    onEvent: (Event) -> Unit
) {
    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = "Order Form",
                onNavigationClicked = {
                    onEvent(Event.OnBackClicked)
                }
            )
        },
        contentWindowInsets = WindowInsets.safeContent,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            PhoneInput(state, onEvent)
            Spacer(16.dp)
            DeliveryMethodSelector(
                selectedMethod = state.deliveryMethod,
                onMethodSelected = { onEvent(Event.DeliveryMethodChanged(it)) },
                enabled = !state.isLoading
            )
            Spacer(16.dp)
            if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
                AddressInput(state, onEvent)
                Spacer(16.dp)
            }
            Spacer(16.dp)
            DeliveryDateTimeField(
                state = state,
                onEvent = onEvent
            )
            Spacer(16.dp)
            CommentField(state, onEvent)
            Spacer(16.dp)
            Button(
                onClick = { /* onEvent(OrderFormScreenContract.Event.SubmitOrder) */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Оформить заказ")
            }
        }
    }
}


@Composable
private fun PhoneInput(state: OrderFormScreenContract.State, onEvent: (Event) -> Unit) {
    val (phoneValue, phoneValueSetter) = state.phoneField.data.collectAsMutableState()
    val phoneError by state.phoneField.error.collectAsState()

    PhoneField(
        modifier = Modifier.fillMaxWidth(),
        value = phoneValue,
        onValueChange = {
            phoneValueSetter(it)
            onEvent(Event.PhoneChanged(it))
        },
        isError = phoneError != null,
        supportingText = SupportingText(phoneError),
        enabled = !state.isLoading,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
    )
}

@Composable
fun DeliveryMethodSelector(
    selectedMethod: DeliveryMethod,
    onMethodSelected: (DeliveryMethod) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeliveryOption.entries.forEach { option ->
            DeliveryMethodOption(
                option = option,
                selected = selectedMethod == option.method,
                onSelected = { onMethodSelected(option.method) },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryMethodOption(
    option: DeliveryOption,
    selected: Boolean,
    onSelected: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onSelected,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private enum class DeliveryOption(
    val method: DeliveryMethod,
    val label: String,
    val icon: ImageVector
) {
    PICKUP(DeliveryMethod.PICKUP, "Самовывоз", Icons.Rounded.DirectionsCar),
    DELIVERY(DeliveryMethod.DELIVERY, "Доставка", Icons.Rounded.Home)
}

@Composable
private fun AddressInput(state: OrderFormScreenContract.State, onEvent: (Event) -> Unit) {
    val (addressValue, addressValueSetter) = state.addressField.data.collectAsMutableState()
    val addressError by state.addressField.error.collectAsState()

    TextField(
        value = addressValue,
        onValueChange = {
            addressValueSetter(it)
            onEvent(Event.AddressChanged(it))
        },
        leadingIcon = { Icon(imageVector = Icons.Rounded.Home, contentDescription = "address") },
        // TODO: заменить на ресурс и изменить на реальный текст
        placeholder = { Text("Адрес доставки") },
        isError = addressError != null,
        supportingText = SupportingText(addressError),
        enabled = !state.isLoading,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth()
    )
}


fun formatDateTime(dateTime: LocalDateTime): String {
    return buildString {
        append(dateTime.dayOfMonth.toString().padStart(2, '0'))
        append(".")
        append(dateTime.monthNumber.toString().padStart(2, '0'))
        append(".")
        append(dateTime.year)
        append(" ")
        append(dateTime.hour.toString().padStart(2, '0'))
        append(":")
        append(dateTime.minute.toString().padStart(2, '0'))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDateTimeField(
    state: OrderFormScreenContract.State,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier
) {

    val dateTime = remember(state.dateTimeField.data.value) {
        Instant.parse(state.dateTimeField.data.value)
    }

    val dateTimeFormatted = remember(dateTime) {
        formatDateTime(dateTime.toLocalDateTime(TimeZone.currentSystemDefault()))
    }

    val labelText = remember(state.deliveryMethod) {
        when (state.deliveryMethod) {
            DeliveryMethod.DELIVERY -> "Дата и время доставки"
            DeliveryMethod.PICKUP -> "Дата и время самовывоза"
        }
    }


    Column(modifier = modifier) {
        TextField(
            value = dateTimeFormatted,
            onValueChange = { },
            label = { Text(labelText) },
            readOnly = true,
            enabled = !state.isLoading,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date and time"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release && !state.isLoading) {
                            onEvent(Event.DateTimeFieldClicked)
                        }
                    }
                }
            }


        )

        if (state.showDatePicker) {
            DatePickerDialogSK(
                initialDate = dateTime,
                onDateSelected = { selectedDate ->
                    onEvent(Event.DateSelected(selectedDate))
                },
                onDismiss = { onEvent(Event.DismissDatePicker) }
            )
        }

        if (state.showTimePicker) {
            TimePickerDialog(
                initialTime = LocalTime(
                    dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour,
                    dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).minute
                ),
                onTimeSelected = { selectedTime ->
                    onEvent(Event.TimeSelected(selectedTime))
                },
                onDismiss = { onEvent(Event.DismissTimePicker) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSK(
    initialDate: Instant,
    onDateSelected: (Instant) -> Unit,
    onDismiss: () -> Unit
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val selectedDate = Instant.fromEpochMilliseconds(it)
                    onDateSelected(selectedDate)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        content = {
            DatePicker(
                state = datePickerState
            )
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите время") },
        text = {
            TimePicker(
                state = timePickerState
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)
                onTimeSelected(selectedTime)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}


@Composable
private fun CommentField(
    state: OrderFormScreenContract.State,
    onEvent: (Event) -> Unit
) {
    val (commentValue, commentValueSetter) = state.commentField.data.collectAsMutableState()
    val commentError by state.commentField.error.collectAsState()


    TextField(
        value = commentValue,
        onValueChange = {
            commentValueSetter(it)
            onEvent(Event.CommentChanged(it))
        },
        label = { Text("Комментарий к заказу") },
        isError = commentError != null,
        supportingText = SupportingText(commentError),
        enabled = !state.isLoading
    )
}