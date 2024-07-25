package skdev.omsrings.mobile.presentation.feature_profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.invalid_format
import omsringsmobile.composeapp.generated.resources.profile_updated
import omsringsmobile.composeapp.generated.resources.success
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.domain.model.UserInfo
import skdev.omsrings.mobile.domain.usecase.feature_user_profile.GetUserProfileUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_profile.LogoutUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_profile.UpdateUserProfileUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.Constants
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.error.toNotificationModel
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.matchRegex
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.notification.NotificationModel
import skdev.omsrings.mobile.utils.notification.ToastType
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess

class UserProfileScreenModel(
    private val notificationManager: NotificationManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseScreenModel<UserProfileContract.Event, UserProfileContract.Effect>(notificationManager) {
    private var initialFullName = ""
    private var initialPhoneNumber = ""

    private val _uiState = MutableStateFlow(UserProfileContract.UIState())
    val uiState = _uiState.asStateFlow()


    val fullNameField = FormField(
        scope = screenModelScope,
        initialValue = initialFullName,
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    val phoneNumberField = FormField(
        scope = screenModelScope,
        initialValue = initialPhoneNumber,
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                matchRegex(Res.string.invalid_format, Constants.PHONE_REGEX)
            }
        }
    )

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            getUserProfileUseCase()
                .ifSuccess { userInfoResult ->
                    val userInfo = userInfoResult.data
                    initialFullName = userInfo.fullName
                    initialPhoneNumber = userInfo.phoneNumber
                    _uiState.update { uiState ->
                        uiState.copy(
                            fullName = userInfo.fullName,
                            phoneNumber = userInfo.phoneNumber,
                            isEmployer = userInfo.isEmployer
                        )
                    }
                    fullNameField.setValue(userInfo.fullName)
                    phoneNumberField.setValue(userInfo.phoneNumber)
                }.ifError { error ->
                    showErrorMessage(error.error)
                }
        }
    }

    override fun onEvent(event: UserProfileContract.Event) {
        when (event) {
            is UserProfileContract.Event.OnFullNameChanged -> updateFullName(event.fullName)
            is UserProfileContract.Event.OnPhoneNumberChanged -> updatePhoneNumber(event.phoneNumber)
            is UserProfileContract.Event.OnSaveProfile -> saveProfile()
            is UserProfileContract.Event.OnLogout -> logout()
        }
    }


    private fun showErrorMessage(error: DataError) {
        Napier.e("Error: $error")
        notificationManager.show(error.toNotificationModel())
    }

    private fun updateFullName(fullName: String) {
        val formattedFullName = fullName.trim()
        _uiState.update { it.copy(fullName = formattedFullName) }
        fullNameField.setValue(fullName)
        fullNameField.validate()
        updateUIState()
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
        phoneNumberField.setValue(phoneNumber)
        phoneNumberField.validate()
        updateUIState()
    }

    private fun updateUIState() {
        val isDataChanged = _uiState.value.fullName != initialFullName ||
                _uiState.value.phoneNumber != initialPhoneNumber
        val canSave = isDataChanged &&
                fullNameField.error.value == null &&
                phoneNumberField.error.value == null
        _uiState.update { it.copy(isDataChanged = isDataChanged, canSave = canSave) }
    }

    private fun saveProfile() {
        screenModelScope.launch {
            val updatedUserInfo = UserInfo(
                fullName = fullNameField.data.value,
                phoneNumber = phoneNumberField.data.value,
                isEmployer = uiState.value.isEmployer
            )
            updateUserProfileUseCase(updatedUserInfo)
                .ifSuccess {
                    initialFullName = updatedUserInfo.fullName
                    initialPhoneNumber = updatedUserInfo.phoneNumber
                    _uiState.update { uiState ->
                        uiState.copy(
                            fullName = updatedUserInfo.fullName,
                            phoneNumber = updatedUserInfo.phoneNumber
                        )
                    }
                    updateUIState()
                    showSuccessMessage(Res.string.profile_updated)
                }
                .ifError { error ->
                    showErrorMessage(error.error)
                }
        }

    }

    private fun showSuccessMessage(message: StringResource) {
        notificationManager.show(
            NotificationModel.Toast(
                titleRes = Res.string.success,
                messageRes = message,
                icon = Icons.Rounded.CheckCircle,
                type = ToastType.Success
            )
        )
    }

    private fun logout() {
        screenModelScope.launch {
            logoutUseCase()
                .ifSuccess {
                    launchEffect(UserProfileContract.Effect.LoggedOut)
                }
                .ifError { error ->
                    showErrorMessage(error.error)
                }
        }
    }


}