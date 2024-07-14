package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.datetime.Instant
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
                initialDateTime = if (state.dateTimeField.data.value.isNotBlank()) Instant.parse(state.dateTimeField.data.value) else null,
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