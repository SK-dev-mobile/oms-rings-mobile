package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.create_order
import omsringsmobile.composeapp.generated.resources.order_details
import omsringsmobile.composeapp.generated.resources.place_order
import omsringsmobile.composeapp.generated.resources.tap_to_place_order
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import skdev.omsrings.mobile.domain.model.DeliveryMethod
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormContract.Event
import skdev.omsrings.mobile.presentation.feature_order_form.components.*
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens


// TODO: сделать редактирование заказа
// TODO: сделать отображение загрузки
// TODO: привести в порядок строковые ресурсы

class OrderFormScreen(
    private val selectedDate: Timestamp
) : BaseScreen("order_form_screen") {
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<OrderFormScreenModel> {
            parametersOf(selectedDate)
        }
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
                title = stringResource(Res.string.create_order),
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
                icon = {
                    Icon(
                        Icons.Rounded.ShoppingCart,
                        contentDescription = stringResource(Res.string.tap_to_place_order)
                    )
                },
                text = { Text(stringResource(Res.string.place_order)) }
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
            .padding(Dimens.spaceMedium)
    ) {
        Text(
            text = stringResource(Res.string.order_details),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = Dimens.spaceMedium)
        )

        DeliveryPhoneField(
            phoneField = state.contactPhoneField,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Dimens.spaceMedium)

        DeliveryMethodSelector(
            selectedMethod = state.deliveryMethod,
            onMethodSelected = { onEvent(Event.OnDeliveryMethodChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Dimens.spaceMedium)

        if (state.deliveryMethod == DeliveryMethod.DELIVERY) {
            DeliveryAddressField(
                addressField = state.deliveryAddressField,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Dimens.spaceMedium)
        }

        DeliveryTimeSelector(
            modifier = Modifier.fillMaxWidth(),
            timeField = state.deliveryTimeField,
            deliveryMethod = state.deliveryMethod
        )
        Spacer(Dimens.spaceMedium)

        CommentField(
            commentField = state.deliveryCommentField,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Dimens.spaceLarge)

        EnhancedConfirmOrderButton(
            onClick = { onEvent(Event.OnSubmitClicked) },
            isEnabled = true,
            modifier = Modifier.fillMaxWidth()

        )
    }
}


