package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.delivery_address
import omsringsmobile.composeapp.generated.resources.delivery_address_hint
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun DeliveryAddressField(
    addressField: FormField<String, StringResource>,
    modifier: Modifier = Modifier
) {
    val (addressValue, addressValueSetter) = addressField.data.collectAsMutableState()
    val addressError by addressField.error.collectAsState()

    TextField(
        value = addressValue,
        onValueChange = { address -> addressValueSetter(address) },
        leadingIcon = {
            Icon(
                Icons.Rounded.Home,
                contentDescription = stringResource(Res.string.delivery_address_hint)
            )
        },
        placeholder = { Text(stringResource(Res.string.delivery_address)) },
        isError = addressError != null,
        supportingText = SupportingText(addressError),
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrectEnabled = true,
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = androidx.compose.ui.text.input.ImeAction.Go,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
        )
    )
}