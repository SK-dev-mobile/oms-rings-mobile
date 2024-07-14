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
import skdev.omsrings.mobile.presentation.feature_order_form.components.CommentField
import skdev.omsrings.mobile.presentation.feature_order_form.components.ConfirmOrderButton
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryDateTimeField
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryMethodSelector
import skdev.omsrings.mobile.presentation.feature_order_form.components.PhoneInput
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer


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
                onPhoneChanged = { onEvent(Event.OnPhoneChanged(it)) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            Spacer(16.dp)
            DeliveryMethodSelector(
                selectedMethod = state.deliveryMethod,
                onMethodSelected = { onEvent(Event.OnDeliveryMethodChanged(it)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(16.dp)
            if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
                AddressInput(
                    addressField = state.addressField,
                    onAddressChanged = { onEvent(Event.OnAddressChanged(it)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Spacer(16.dp)
            }
            DeliveryDateTimeField(
                initialDateTime = if (state.dateTimeField.data.value.isNotBlank()) Instant.parse(state.dateTimeField.data.value) else null,
                onDateTimeSelected = { onEvent(Event.OnDateTimeChanged(it)) },
                deliveryMethod = state.deliveryMethod,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(16.dp)
            CommentField(
                commentField = state.commentField,
                onCommentChanged = { onEvent(Event.OnCommentChanged(it)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(16.dp)
            ConfirmOrderButton(
                onClick = { onEvent(Event.OnSubmitClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
    }
}


