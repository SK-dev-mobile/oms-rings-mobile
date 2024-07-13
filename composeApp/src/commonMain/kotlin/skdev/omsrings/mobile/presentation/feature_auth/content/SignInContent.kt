package skdev.omsrings.mobile.presentation.feature_auth.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyOff
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.continue_process
import omsringsmobile.composeapp.generated.resources.create_account
import omsringsmobile.composeapp.generated.resources.dont_have_account
import omsringsmobile.composeapp.generated.resources.email
import omsringsmobile.composeapp.generated.resources.enter_your_data
import omsringsmobile.composeapp.generated.resources.forgot_password
import omsringsmobile.composeapp.generated.resources.password
import omsringsmobile.composeapp.generated.resources.password_reset
import omsringsmobile.composeapp.generated.resources.welcome_to_rings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreenContract
import skdev.omsrings.mobile.presentation.feature_auth.OnAction
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.IconSize
import skdev.omsrings.mobile.utils.compose.HideKeyboardOnUpdate
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun SignInContent(
    modifier: Modifier = Modifier,
    onAction: OnAction,
    updating: Boolean,
    emailField: FormField<String, StringResource>,
    passwordField: FormField<String, StringResource>,
) {
    val (emailValue, emailSetter) = emailField.data.collectAsMutableState()
    val emailError: StringResource? by emailField.error.collectAsState()

    val (passwordValue, passwordSetter) = passwordField.data.collectAsMutableState()
    val passwordError: StringResource? by passwordField.error.collectAsState()

    HideKeyboardOnUpdate(updating)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.size(IconSize.ExtraLarge),
            imageVector = Icons.Rounded.WavingHand,
            contentDescription = stringResource(Res.string.password_reset),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Dimens.spaceLarge)

        Text(
            text = stringResource(Res.string.welcome_to_rings),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Dimens.spaceSmall)

        Text(
            text = stringResource(Res.string.enter_your_data),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Dimens.spaceLarge)

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
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Dimens.spaceSmall)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = passwordValue,
            onValueChange = passwordSetter,
            label = {
                Text(stringResource(Res.string.password))
            },
            supportingText = SupportingText(passwordError),
            isError = passwordError != null,
            enabled = !updating,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                onAction(AuthScreenContract.Event.OnSignInClicked)
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onAction(AuthScreenContract.Event.OnForgotPasswordClicked) }
            ) {
                Text(
                    text = stringResource(Res.string.forgot_password),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Dimens.spaceLarge)

        Button(
            enabled = !updating,
            onClick = { onAction(AuthScreenContract.Event.OnSignInClicked) }
        ) {
            Text(
                text = stringResource(Res.string.continue_process),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Dimens.spaceExtraSmall)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.dont_have_account),
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(
                onClick = { onAction(AuthScreenContract.Event.OnCreateAccountClicked) }
            ) {
                Text(
                    text = stringResource(Res.string.create_account),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
