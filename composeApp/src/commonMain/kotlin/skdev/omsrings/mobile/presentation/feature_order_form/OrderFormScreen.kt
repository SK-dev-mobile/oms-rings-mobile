package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreenContract.Event
import skdev.omsrings.mobile.presentation.feature_order_form.components.EnhancedTimeInput
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


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
            DeliveryMethodField(
                selectedMethod = state.deliveryMethod,
                onMethodSelected = { onEvent(Event.DeliveryMethodChanged(it)) },
                enabled = !state.isLoading
            )
            Spacer(16.dp)
            if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
                AddressInput(state, onEvent)
                Spacer(16.dp)
            }
            TimePickerField(state = state, onEvent = onEvent)
            Spacer(16.dp)
            DatePickerField(
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
private fun DeliveryMethodField(
    selectedMethod: DeliveryMethod,
    onMethodSelected: (DeliveryMethod) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Способ доставки",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        DeliveryMethod.entries.forEach { method ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = selectedMethod == method,
                        onClick = { onMethodSelected(method) },
                        enabled = enabled
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == method,
                    onClick = null,
                    enabled = enabled
                )
                Text(
                    text = when (method) {
                        DeliveryMethod.PICKUP -> "Самовывоз"
                        DeliveryMethod.DELIVERY -> "Доставка"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
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

@Composable
private fun TimePickerField(
    state: OrderFormScreenContract.State,
    onEvent: (Event) -> Unit
) {
    val (timeValue, onTimeValueChanged) = state.timeField.data.collectAsMutableState()
    val timeError by state.timeField.error.collectAsState()

    EnhancedTimeInput(
        value = timeValue,
        onValueChange = { newValue ->
            onTimeValueChanged(newValue)
            onEvent(OrderFormScreenContract.Event.TimeChanged(newValue))
        },
        modifier = Modifier.fillMaxWidth(),
        isError = timeError != null,
        label = { Text("Время доставки") }
    )

    if (timeError != null) {
        Text(
            text = timeError.toString(),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}


@Composable
private fun DatePickerField(
    state: OrderFormScreenContract.State,
    onEvent: (Event) -> Unit
) {
    val (dateValue, onDateValueChanged) = state.timeField.data.collectAsMutableState()
    val dateError by state.timeField.error.collectAsState()

    val label = if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
        "Дата доставки"
    } else {
        "Дата самовывоза"
    }

    TextField(
        value = dateValue,
        onValueChange = {
            onDateValueChanged(it)
            onEvent(Event.TimeChanged(it))
        },
        label = { Text(label) },
        isError = dateError != null,
        supportingText = SupportingText(dateError),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isLoading
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