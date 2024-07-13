package skdev.omsrings.mobile.presentation.feature_auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.check_your_email
import omsringsmobile.composeapp.generated.resources.invalid_format
import omsringsmobile.composeapp.generated.resources.password_reseting_form_was_sended
import omsringsmobile.composeapp.generated.resources.password_too_short
import omsringsmobile.composeapp.generated.resources.passwords_not_match
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.usecase.feature_auth.SendResetPasswordEmailUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignInUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignUpUserUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole
import skdev.omsrings.mobile.utils.Constants
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validateAll
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.matchRegex
import skdev.omsrings.mobile.utils.fields.validators.minLength
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.fields.validators.sameAs
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.ToastType
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess

class AuthScreenModel(
    notificationManager: NotificationManager,
    private val signInUserUseCase: SignInUserUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val sendResetPasswordEmailUseCase: SendResetPasswordEmailUseCase,
) : BaseScreenModel<AuthScreenContract.Event, AuthScreenContract.Effect>(notificationManager) {

    private val _uiState =
        MutableStateFlow<AuthScreenContract.State>(AuthScreenContract.State.SignIn)
    val uiState = _uiState.asStateFlow()

    val emailField: FormField<String, StringResource> = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                matchRegex(Res.string.invalid_format, Constants.EMAIL_REGEX)
            }
        }
    )

    val passwordField: FormField<String, StringResource> = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                minLength(Res.string.password_too_short, minLength = Constants.PasswordLenght)
            }
        }
    )

    val passwordConfirmField: FormField<String, StringResource> = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                sameAs(Res.string.passwords_not_match, passwordField)
            }
        }
    )

    val phoneField: FormField<String, StringResource> = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                matchRegex(Res.string.invalid_format, Constants.PHONE_REGEX)
            }
        }
    )

    val fullNameField: FormField<String, StringResource> = FormField(
        scope = screenModelScope,
        initialValue = "",
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    override fun onEvent(event: AuthScreenContract.Event) {
        when (event) {
            AuthScreenContract.Event.OnDispose -> {}
            AuthScreenContract.Event.OnStart -> {}
            AuthScreenContract.Event.OnResetPasswordClicked -> onResetPasswordClicked()
            AuthScreenContract.Event.OnBackClicked -> onBackClicked()
            AuthScreenContract.Event.OnCreateAccountClicked -> onCreateAccountClicked()
            AuthScreenContract.Event.OnForgotPasswordClicked -> onForgotPasswordClicked()
            AuthScreenContract.Event.OnSignInClicked -> onSignInClicked()
            is AuthScreenContract.Event.OnSignUpClicked -> onSignUpClicked(event.role)
        }
    }

    private fun onSignUpClicked(role: UserRole) {
        screenModelScope.launch {
            onUpdateState()
            if (validateAll(
                    emailField,
                    passwordField,
                    passwordConfirmField,
                    phoneField,
                    fullNameField
                )
            ) {
                signUpUserUseCase(
                    email = emailField.value(),
                    password = passwordField.value(),
                    phone = phoneField.value(),
                    fullName = fullNameField.value(),
                    isEmployer = role == UserRole.EMPLOYER
                ).ifSuccess {
                    launchEffect(AuthScreenContract.Effect.NavigateToMainScreen)
                }
            }
            onUpdatedState()
        }
    }

    private fun onSignInClicked() {
        screenModelScope.launch {
            onUpdateState()
            if (validateAll(emailField, passwordField)) {
                signInUserUseCase(
                    emailField.value(),
                    passwordField.value()
                ).ifSuccess {
                    launchEffect(AuthScreenContract.Effect.NavigateToMainScreen)
                }.ifError {
                    emailField.setValue("")
                    passwordField.setValue("")
                }

            }
            onUpdatedState()
        }
    }

    private fun onForgotPasswordClicked() {
        screenModelScope.launch {
            clearFieldsErrors()
            _uiState.update { AuthScreenContract.State.PasswordReset }
        }
    }

    private fun onBackClicked() {
        screenModelScope.launch {
            clearFieldsErrors()
            when (uiState.value) {
                AuthScreenContract.State.PasswordReset -> {
                    _uiState.update { AuthScreenContract.State.SignIn }
                }

                is AuthScreenContract.State.SignUp -> {
                    _uiState.update { AuthScreenContract.State.SignIn }
                }

                AuthScreenContract.State.SignIn -> {
                    launchEffect(AuthScreenContract.Effect.NaivgateBack)
                }
            }
        }
    }

    private fun onResetPasswordClicked() {
        screenModelScope.launch {
            onUpdateState()
            if (validateAll(emailField)) {
                sendResetPasswordEmailUseCase(
                    email = emailField.value()
                ).ifSuccess {
                    showToast(
                        titleRes = Res.string.check_your_email,
                        messageRes = Res.string.password_reseting_form_was_sended,
                        icon = Icons.AutoMirrored.Rounded.Send,
                        type = ToastType.Success
                    )
                    _uiState.update { AuthScreenContract.State.SignIn }
                }
            }
            onUpdatedState()
        }
    }

    private fun onCreateAccountClicked() {
        screenModelScope.launch {
            clearFieldsErrors()
            _uiState.update { AuthScreenContract.State.SignUp }
        }
    }

    private suspend fun clearFieldsErrors() {
        emailField.resetValidation()
        passwordField.resetValidation()
        passwordConfirmField.resetValidation()
        phoneField.resetValidation()
        fullNameField.resetValidation()
    }
}
