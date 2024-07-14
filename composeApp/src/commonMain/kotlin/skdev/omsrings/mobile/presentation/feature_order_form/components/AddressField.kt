package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun AddressInput(
    addressField: FormField<String, StringResource>,
    onAddressChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (addressValue, addressValueSetter) = addressField.data.collectAsMutableState()
    val addressError by addressField.error.collectAsState()

    TextField(
        value = addressValue,
        onValueChange = { address ->
            addressValueSetter(address)
            onAddressChanged(address)
        },
        label = { Text("Delivery Address") },
        leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = "Address") },
        placeholder = { Text("Enter your address") },
        isError = addressError != null,
        supportingText = SupportingText(addressError),
        modifier = modifier
    )
}