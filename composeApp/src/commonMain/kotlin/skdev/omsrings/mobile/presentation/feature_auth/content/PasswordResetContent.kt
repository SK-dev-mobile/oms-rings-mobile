package skdev.omsrings.mobile.presentation.feature_auth.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.continue_process
import omsringsmobile.composeapp.generated.resources.email
import omsringsmobile.composeapp.generated.resources.enter_email_for_password_reset
import omsringsmobile.composeapp.generated.resources.enter_your_data
import omsringsmobile.composeapp.generated.resources.password_reset
import omsringsmobile.composeapp.generated.resources.password_reset_form_will_be_sended_to_your_email
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
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState


@Composable
fun PasswordResetContent(
    modifier: Modifier = Modifier,
    updating: Boolean,
    emailField: FormField<String, StringResource>,
    onAction: OnAction,
) {
    val (emailValue, emailSetter) = emailField.data.collectAsMutableState()
    val emailError: StringResource? by emailField.error.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.size(IconSize.ExtraLarge),
            imageVector = Icons.Rounded.KeyOff,
            contentDescription = stringResource(Res.string.password_reset),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Dimens.spaceLarge)

        Text(
            text = stringResource(Res.string.enter_email_for_password_reset),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Dimens.spaceSmall)

        Text(
            text = stringResource(Res.string.password_reset_form_will_be_sended_to_your_email),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
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


        Spacer(Dimens.spaceLarge)

        Button(
            enabled = !updating,
            onClick = { onAction(AuthScreenContract.Event.OnResetPasswordClicked) }
        ) {
            Text(
                text = stringResource(Res.string.continue_process),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
