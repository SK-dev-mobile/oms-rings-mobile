package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormContract.Event
import skdev.omsrings.mobile.presentation.feature_order_form.components.AddressInput
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryDateTimeField
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryMethodSelector
import skdev.omsrings.mobile.presentation.feature_order_form.components.PhoneInput
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
    state: OrderFormContract.State,
    onEvent: (Event) -> Unit
) {
    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            PhoneInput(
                phoneField = state.phoneField,
                onPhoneChanged = { onEvent(Event.OnPhoneChanged(it)) }
            )
            Spacer(16.dp)
            DeliveryMethodSelector(
                selectedMethod = state.deliveryMethod,
                onMethodSelected = { onEvent(Event.OnDeliveryMethodChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(16.dp)
            if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
                AddressInput(
                    addressField = state.addressField,
                    onAddressChanged = { onEvent(Event.OnAddressChanged(it)) }
                )
                Spacer(16.dp)
            }
            Spacer(16.dp)
            DeliveryDateTimeField(
                initialDateTime = Instant.parse(state.dateTimeField.data.value),
                onDateTimeSelected = { onEvent(Event.OnDateTimeChanged(it)) },
                deliveryMethod = state.deliveryMethod,
                modifier = Modifier.fillMaxWidth()
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
    state: OrderFormContract.State,
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
//                            onEvent(Event.DateTimeFieldClicked)
                        }
                    }
                }
            }


        )

//        if (state.showDatePicker) {
//            DatePickerDialogSK(
//                initialDate = dateTime,
//                onDateSelected = { selectedDate ->
//                    onEvent(Event.DateSelected(selectedDate))
//                },
//                onDismiss = { onEvent(Event.DismissDatePicker) }
//            )
//        }
//
//        if (state.showTimePicker) {
//            TimePickerDialog(
//                initialTime = LocalTime(
//                    dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour,
//                    dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).minute
//                ),
//                onTimeSelected = { selectedTime ->
//                    onEvent(Event.TimeSelected(selectedTime))
//                },
//                onDismiss = { onEvent(Event.DismissTimePicker) }
//            )
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogSK(
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
private fun TimePickerDialog(
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
    state: OrderFormContract.State,
    onEvent: (Event) -> Unit
) {
    val (commentValue, commentValueSetter) = state.commentField.data.collectAsMutableState()
    val commentError by state.commentField.error.collectAsState()


    TextField(
        value = commentValue,
        onValueChange = {
            commentValueSetter(it)
            onEvent(Event.OnCommentChanged(it))
        },
        label = { Text("Комментарий к заказу") },
        isError = commentError != null,
        supportingText = SupportingText(commentError),
        enabled = !state.isLoading
    )
}