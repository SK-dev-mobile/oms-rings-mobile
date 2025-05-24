package skdev.omsrings.mobile.presentation.feature_auth.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.continue_process
import omsringsmobile.composeapp.generated.resources.email
import omsringsmobile.composeapp.generated.resources.enter_your_data
import omsringsmobile.composeapp.generated.resources.full_name
import omsringsmobile.composeapp.generated.resources.password
import omsringsmobile.composeapp.generated.resources.password_confirm
import omsringsmobile.composeapp.generated.resources.password_reset
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreenContract
import skdev.omsrings.mobile.presentation.feature_auth.OnAction
import skdev.omsrings.mobile.presentation.feature_auth.components.RoleSelector
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole
import skdev.omsrings.mobile.ui.components.fields.PasswordField
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.IconSize
import skdev.omsrings.mobile.utils.compose.HideKeyboardOnUpdate
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun SignUpContent(
    modifier: Modifier = Modifier,
    onAction: OnAction,
    updating: Boolean,
    emailField: FormField<String, StringResource>,
    passwordField: FormField<String, StringResource>,
    passwordConfirmField: FormField<String, StringResource>,
    phoneField: FormField<String, StringResource>,
    nameField: FormField<String, StringResource>,
) {
    val (emailValue, emailSetter) = emailField.data.collectAsMutableState()
    val emailError: StringResource? by emailField.error.collectAsState()

    val (passwordValue, passwordSetter) = passwordField.data.collectAsMutableState()
    val passwordError: StringResource? by passwordField.error.collectAsState()

    val (confirmPasswordValue, confirmPasswordSetter) = passwordConfirmField.data.collectAsMutableState()
    val confirmPasswordError: StringResource? by passwordConfirmField.error.collectAsState()

    val (phoneValue, phoneSetter) = phoneField.data.collectAsMutableState()
    val phoneError: StringResource? by phoneField.error.collectAsState()

    val (nameValue, nameSetter) = nameField.data.collectAsMutableState()
    val nameError: StringResource? by nameField.error.collectAsState()

    var userRole: UserRole by remember { mutableStateOf(UserRole.CONTRAGENT) }

    HideKeyboardOnUpdate(updating)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.size(IconSize.ExtraLarge),
            imageVector = Icons.Rounded.HowToReg,
            contentDescription = stringResource(Res.string.password_reset),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Dimens.spaceLarge)

        Text(
            text = stringResource(Res.string.enter_your_data),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Dimens.spaceLarge)

        RoleSelector(
            value = userRole,
            onChangeValue = { userRole = it }
        )

        Spacer(Dimens.spaceSmall)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = emailValue,
            onValueChange = emailSetter,
            label = {
                Text(stringResource(Res.string.email))
            },
            supportingText = SupportingText(emailError),
            isError = emailError != null,
            enabled = !updating,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Dimens.spaceSmall)

        PasswordField(
            modifier = Modifier.fillMaxWidth(),
            value = passwordValue,
            onValueChange = passwordSetter,
            label = {
                Text(stringResource(Res.string.password))
            },
            supportingText = SupportingText(passwordError),
            isError = passwordError != null,
            enabled = !updating,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Dimens.spaceSmall)

        PasswordField(
            modifier = Modifier.fillMaxWidth(),
            value = confirmPasswordValue,
            onValueChange = confirmPasswordSetter,
            label = {
                Text(stringResource(Res.string.password_confirm))
            },
            supportingText = SupportingText(confirmPasswordError),
            isError = confirmPasswordError != null,
            enabled = !updating,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Dimens.spaceSmall)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameValue,
            onValueChange = nameSetter,
            label = {
                Text(stringResource(Res.string.full_name))
            },
            supportingText = SupportingText(nameError),
            isError = nameError != null,
            enabled = !updating,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Dimens.spaceSmall)

        PhoneField(
            modifier = Modifier.fillMaxWidth(),
            value = phoneValue,
            onValueChange = phoneSetter,
            supportingText = SupportingText(phoneError),
            isError = phoneError != null,
            enabled = !updating,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                onAction(AuthScreenContract.Event.OnSignUpClicked(userRole))
            }
        )

        Spacer(Dimens.spaceLarge)

        Button(
            enabled = !updating,
            onClick = { onAction(AuthScreenContract.Event.OnSignUpClicked(userRole)) }
        ) {
            Text(
                text = stringResource(Res.string.continue_process),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
