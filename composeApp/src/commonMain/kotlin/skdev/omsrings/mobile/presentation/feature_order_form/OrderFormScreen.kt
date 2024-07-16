package skdev.omsrings.mobile.presentation.feature_order_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import dev.gitlive.firebase.firestore.Timestamp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.confirm_order
import omsringsmobile.composeapp.generated.resources.create_order
import omsringsmobile.composeapp.generated.resources.edit_confirm_order
import omsringsmobile.composeapp.generated.resources.edit_order
import omsringsmobile.composeapp.generated.resources.edit_place_order
import omsringsmobile.composeapp.generated.resources.order_details
import omsringsmobile.composeapp.generated.resources.place_order
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
        val state by screenModel.uiState.collectAsState()

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
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { OrderFormTopBar(state.isEditMode, state.deliveryDate, onEvent) },
        floatingActionButton = {
            OrderFormFAB(state.isEditMode) { isBottomSheetVisible = true }
        }
    ) { paddingValues ->
        ProductSelectionSection(
            modifier = Modifier.padding(paddingValues),
            state = state.productSelectionState,
            onEvent = { productSelectionEvent -> onEvent(Event.OnProductSelectionEvent(productSelectionEvent)) }
        )

        if (isBottomSheetVisible) {
            OrderDetailsBottomSheet(
                state = state,
                onEvent = onEvent,
                onDismiss = { isBottomSheetVisible = false },
                bottomSheetState = bottomSheetState
            )
        }
    }

}

@Composable
private fun OrderFormTopBar(
    isEditMode: Boolean,
    deliveryDate: String,
    onEvent: (OrderFormContract.Event) -> Unit
) {
    val title = if (isEditMode) {
        stringResource(Res.string.edit_order, deliveryDate)
    } else {
        stringResource(Res.string.create_order, deliveryDate)
    }

    RingsTopAppBar(
        title = title,
        onNavigationClicked = { onEvent(OrderFormContract.Event.OnBackClicked) }
    )
}

@Composable
private fun OrderFormFAB(
    isEditMode: Boolean,
    onClick: () -> Unit
) {
    val (text, icon) = if (isEditMode) {
        Pair(stringResource(Res.string.edit_place_order), Icons.Rounded.Edit)
    } else {
        Pair(stringResource(Res.string.place_order), Icons.Rounded.ShoppingCart)
    }

    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        text = { Text(text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailsBottomSheet(
    state: OrderFormContract.State,
    onEvent: (OrderFormContract.Event) -> Unit,
    onDismiss: () -> Unit,
    bottomSheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState
    ) {
        OrderDetailsContent(state, onEvent)
    }
}

@Composable
private fun OrderDetailsContent(
    state: OrderFormContract.State,
    onEvent: (OrderFormContract.Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
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

        ConfirmOrderButton(
            isEditMode = state.isEditMode,
            isEnabled = state.productSelectionState.selectedItems.isNotEmpty(),
            onClick = { onEvent(Event.OnSubmitClicked) }
        )
    }
}


@Composable
private fun ConfirmOrderButton(
    isEditMode: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val (text, icon) = if (isEditMode) {
        Pair(Res.string.edit_confirm_order, Icons.Rounded.Edit)
    } else {
        Pair(Res.string.confirm_order, Icons.Rounded.ShoppingCart)
    }

    EnhancedConfirmOrderButton(
        text = text,
        icon = icon,
        onClick = onClick,
        isEnabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

