package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.launch
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormContract.Event
import skdev.omsrings.mobile.presentation.feature_order_form.components.AddressInput
import skdev.omsrings.mobile.presentation.feature_order_form.components.CommentField
import skdev.omsrings.mobile.presentation.feature_order_form.components.ConfirmOrderButton
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryDateTimeField
import skdev.omsrings.mobile.presentation.feature_order_form.components.DeliveryMethodSelector
import skdev.omsrings.mobile.presentation.feature_order_form.components.PhoneInput
import skdev.omsrings.mobile.presentation.feature_order_form.components.ProductSelectionSection
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar


// TODO: добавить валидацию полей
// TODO: сделать часть в форме с выбором количества товаров
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderFormContent(
    state: OrderFormContract.State,
    onEvent: (Event) -> Unit
) {

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = "Order Form",
                onNavigationClicked = {
                    onEvent(Event.OnBackClicked)
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    isBottomSheetVisible = true
                    coroutineScope.launch { bottomSheetState.show() }
                },
                icon = { Icon(Icons.Rounded.ShoppingCart, contentDescription = "Order Details") },
                text = { Text("Order Details") },
            )
        },
        contentWindowInsets = WindowInsets.safeContent,
    ) { paddingValues ->
        ProductSelectionSection(
            modifier = Modifier.padding(paddingValues),
            state = state.productSelectionState,
            onEvent = { productSelectionEvent ->
                onEvent(Event.OnProductSelectionEvent(productSelectionEvent))
            }
        )
        if (isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isBottomSheetVisible = false },
                sheetState = bottomSheetState
            ) {
                OrderDetailsBottomSheet(state, onEvent)
            }
        }
    }

}


@Composable
private fun OrderDetailsBottomSheet(
    state: OrderFormContract.State,
    onEvent: (Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Order Details",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PhoneInput(
            phoneField = state.phoneField,
            onPhoneChanged = { onEvent(Event.OnPhoneChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeliveryMethodSelector(
            selectedMethod = state.deliveryMethod,
            onMethodSelected = { onEvent(Event.OnDeliveryMethodChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
            AddressInput(
                addressField = state.addressField,
                onAddressChanged = { onEvent(Event.OnAddressChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        DeliveryDateTimeField(
            initialDateTime = state.dateTimeField.data.value.takeIf { it.isNotBlank() }
                ?.let { kotlinx.datetime.Instant.parse(it) },
            onDateTimeSelected = { onEvent(Event.OnDateTimeChanged(it)) },
            deliveryMethod = state.deliveryMethod,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CommentField(
            commentField = state.commentField,
            onCommentChanged = { onEvent(Event.OnCommentChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        ConfirmOrderButton(
            onClick = { onEvent(Event.OnSubmitClicked) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


