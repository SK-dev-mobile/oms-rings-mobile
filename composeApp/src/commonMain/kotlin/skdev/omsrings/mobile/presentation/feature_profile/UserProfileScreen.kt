package skdev.omsrings.mobile.presentation.feature_profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.koin.koinScreenModel
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.edit_profile
import omsringsmobile.composeapp.generated.resources.save_changes
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

object UserProfileScreen : BaseScreen("user_profile_screen") {

    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<UserProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val updating by screenModel.updating.collectAsState()

        UserProfileContent(
            uiState = uiState,
            updating = updating,
            onEvent = screenModel::onEvent
        )

    }

}

@Composable
private fun UserProfileContent(
    uiState: UserProfileContract.UIState,
    updating: Boolean,
    onEvent: (UserProfileContract.Event) -> Unit
) {
    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = stringResource(Res.string.edit_profile),
                actions = {
                    Icon(
                        modifier = Modifier.padding(end = Dimens.spaceMedium),
                        imageVector = Icons.Rounded.Save,
                        contentDescription = stringResource(Res.string.save_changes)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(Dimens.spaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserInfo(
                fullName = uiState.fullName.data.value,
                phoneNumber = uiState.phoneNumber.data.value
            )
            Spacer(Dimens.spaceLarge)
            UserProfileForm(
                uiState = uiState,
                enabled = !updating
            )
            Spacer(Dimens.spaceLarge)
            SaveButton(
                isEnabled = uiState.isDataChanged && !updating,
                onClick = { onEvent(UserProfileContract.Event.OnSaveProfile) }
            )
            Spacer(Dimens.spaceLarge)
            LogoutButton(
                onClick = { onEvent(UserProfileContract.Event.OnLogout) }
            )
        }

    }
}

@Composable
private fun UserInfo(fullName: String, phoneNumber: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Dimens.spaceSmall)
        Text(
            text = phoneNumber,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UserProfileForm(
    uiState: UserProfileContract.UIState,
    enabled: Boolean
) {
    val (fullNameValue, fullNameSetter) = uiState.fullName.data.collectAsMutableState()
    val fullNameError by uiState.fullName.error.collectAsState()

    val (phoneValue, phoneSetter) = uiState.phoneNumber.data.collectAsMutableState()
    val phoneError by uiState.phoneNumber.error.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = fullNameValue,
            onValueChange = fullNameSetter,
            label = {
                Text("Full Name")
            },
            supportingText = SupportingText(fullNameError),
            isError = fullNameError != null,
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = true,
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
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
private fun SaveButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Rounded.Save, contentDescription = stringResource(Res.string.save_changes))
        Spacer(Dimens.spaceSmall)
        Text(stringResource(Res.string.save_changes))
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedButton(
            onClick = onClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = "Logout")
            Spacer(Dimens.spaceSmall)
            Text("Logout")
        }
    }
}