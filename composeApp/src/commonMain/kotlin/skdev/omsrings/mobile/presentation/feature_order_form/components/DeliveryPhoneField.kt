package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun DeliveryPhoneField(
    phoneField: FormField<String, StringResource>,
    modifier: Modifier = Modifier
) {
    val (phoneValue, phoneValueSetter) = phoneField.data.collectAsMutableState()
    val phoneError by phoneField.error.collectAsState()

    PhoneField(
        modifier = modifier,
        value = phoneValue,
        onValueChange = { phoneValueSetter(it) },
        isError = phoneError != null,
        supportingText = SupportingText(phoneError)
    )
}