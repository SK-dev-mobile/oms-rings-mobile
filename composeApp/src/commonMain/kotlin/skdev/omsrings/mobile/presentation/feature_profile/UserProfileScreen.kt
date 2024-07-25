package skdev.omsrings.mobile.presentation.feature_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.fields.PhoneField
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

object UserProfileScreen : BaseScreen("user_profile_screen") {

    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<UserProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsState()


        val (fullNameValue, fullNameSetter) = uiState.fullName.data.collectAsMutableState()
        val fullNameError by uiState.fullName.error.collectAsState()

        val (phoneValue, phoneSetter) = uiState.phoneNumber.data.collectAsMutableState()
        val phoneError by uiState.phoneNumber.error.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spaceMedium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "User Profile",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = fullNameValue,
                onValueChange = fullNameSetter,
                label = {
                    Text("Full Name")
                },
                supportingText = SupportingText(fullNameError),
                isError = fullNameError != null,
                enabled = !uiState.updating,
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
                enabled = !uiState.updating,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(Dimens.spaceLarge)

            Button(
                enabled = !uiState.updating,
                onClick = { screenModel.onEvent(UserProfileContract.Event.OnSaveProfile) }
            ) {
                Text("Save Profile")
            }

            Spacer(Dimens.spaceSmall)

            Button(
                enabled = !uiState.updating,
                onClick = { screenModel.onEvent(UserProfileContract.Event.OnLogout) }
            ) {
                Text("Logout")
            }
        }

    }

}