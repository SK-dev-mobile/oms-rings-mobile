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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Person
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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.edit_profile
import omsringsmobile.composeapp.generated.resources.full_name
import omsringsmobile.composeapp.generated.resources.logout
import omsringsmobile.composeapp.generated.resources.save_changes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreen
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState
import skdev.omsrings.mobile.utils.flow.observeAsEffects

object UserProfileScreen : BaseScreen("user_profile_screen") {

    @Composable
    override fun MainContent() {
        val navigation = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<UserProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val updating by screenModel.updating.collectAsState()


        UserProfileContent(
            navigation = navigation,
            uiState = uiState,
            fullNameField = screenModel.fullNameField,
            phoneNumberField = screenModel.phoneNumberField,
            updating = updating,
            onEvent = screenModel::onEvent
        )

        screenModel.effects.observeAsEffects { effect ->
            when (effect) {
                UserProfileContract.Effect.LoggedOut -> {
                    navigation.push(AuthScreen)
                }
            }
        }

    }


}

@Composable
private fun UserProfileContent(
    navigation: Navigator,
    uiState: UserProfileContract.UIState,
    fullNameField: FormField<String, StringResource>,
    phoneNumberField: FormField<String, StringResource>,
    updating: Boolean,
    onEvent: (UserProfileContract.Event) -> Unit
) {
    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = stringResource(Res.string.edit_profile),
                onNavigationClicked = { navigation.pop() },
                actions = {
                    Icon(
                        modifier = Modifier.padding(end = Dimens.spaceMedium),
                        imageVector = Icons.Rounded.Check,
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
            UserProfileForm(
                fullName = fullNameField,
                phoneNumber = phoneNumberField,
                onFullNameChanged = { onEvent(UserProfileContract.Event.OnFullNameChanged(it)) },
                onPhoneNumberChanged = { onEvent(UserProfileContract.Event.OnPhoneNumberChanged(it)) },
                enabled = !updating
            )
            Spacer(Dimens.spaceMedium)
            Napier.d(
                "UserProfileContent: uiState.canSave: ${uiState.canSave}, uiState.isDataChanged: ${uiState.isDataChanged}"
            )
            SaveButton(
                isEnabled = !updating && uiState.canSave && uiState.isDataChanged,
                onClick = { onEvent(UserProfileContract.Event.OnSaveProfile) }
            )
            Spacer(Dimens.spaceLarge)
            LogoutButton(
                isEnabled = !updating,
                onClick = { onEvent(UserProfileContract.Event.OnLogout) }
            )
        }

    }
}

@Composable
private fun UserProfileForm(
    fullName: FormField<String, StringResource>,
    phoneNumber: FormField<String, StringResource>,
    onFullNameChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    enabled: Boolean
) {
    val (fullNameValue, _) = fullName.data.collectAsMutableState()
    val fullNameError by fullName.error.collectAsState()

    val (phoneValue, _) = phoneNumber.data.collectAsMutableState()
    val phoneError by phoneNumber.error.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Rounded.Person, contentDescription = stringResource(Res.string.full_name))
            },
            value = fullNameValue,
            onValueChange = onFullNameChanged,
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
            onValueChange = onPhoneNumberChanged,
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
        Icon(Icons.Rounded.Check, contentDescription = stringResource(Res.string.save_changes))
        Spacer(Dimens.spaceSmall)
        Text(stringResource(Res.string.save_changes))
    }
}

@Composable
private fun LogoutButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedButton(
            onClick = onClick,
            enabled = isEnabled,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = stringResource(Res.string.logout))
            Spacer(Dimens.spaceSmall)
            Text(
                text = stringResource(Res.string.logout),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}