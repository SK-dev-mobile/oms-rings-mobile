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
import omsringsmobile.composeapp.generated.resources.edit_order
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


class OrderFormScreen(
    private val selectedDate: Timestamp,
    private val orderId: String? = null
) : BaseScreen("order_form_screen") {
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<OrderFormScreenModel> { parametersOf(selectedDate, orderId) }
        val state by screenModel.state.collectAsState()

        LaunchedEffect(orderId) {
            orderId?.let { screenModel.onEvent(Event.LoadExistingOrder(it)) }
        }

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


    // В зависимости от режима редактирования формируем заголовок экрана
    val topAppBarTitle = if (state.isEditMode) {
        stringResource(Res.string.edit_order, state.deliveryDate)
    } else {
        stringResource(Res.string.create_order, state.deliveryDate)
    }


    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = topAppBarTitle,
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
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Dimens.spaceMedium)

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
            isEnabled = state.productSelectionState.selectedItems.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()

        )
    }
}


