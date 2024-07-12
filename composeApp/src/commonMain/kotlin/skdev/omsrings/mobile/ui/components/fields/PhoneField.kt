package skdev.omsrings.mobile.ui.components.fields

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import skdev.omsrings.mobile.ui.components.fields.utils.PhoneVisualTransformation
import skdev.omsrings.mobile.utils.Constants

@Composable
fun PhoneField(
    value: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    mask: String = Constants.PhoneMask,
    maskNumber: Char = Constants.PhoneMaskChar,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Done
    ),
    leadingIcon: @Composable (() -> Unit)? = { Icon(imageVector = Icons.Rounded.Phone, "Phone") },
    keyboardActions: KeyboardActions = KeyboardActions.Default
) = TextField(
    modifier = modifier,
    value = value,
    onValueChange = { newValue ->
        val digitsOnly = newValue.filter { it.isDigit() }
        val formattedValue = when {
            digitsOnly.startsWith("8") -> digitsOnly.replaceFirst("8", "7")
            else -> digitsOnly
        }.take(11)
        onValueChange(formattedValue)
    },
    visualTransformation = PhoneVisualTransformation(mask, maskNumber),
    textStyle = MaterialTheme.typography.titleMedium,
    placeholder = { Text("+7 (999) 999-99-99") },
    leadingIcon = leadingIcon,
    supportingText = supportingText,
    isError = isError,
    enabled = enabled,
    singleLine = true,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    readOnly = readOnly,
)
