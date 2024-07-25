package skdev.omsrings.mobile.presentation.feature_profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private var initialUserInfo = UserInfo.DEFAULT

    private val _uiState = MutableStateFlow(UserProfileContract.UIState())
    val uiState = _uiState.asStateFlow()


    val fullNameField = FormField(
        scope = screenModelScope,
        initialValue = initialUserInfo.fullName,
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
            }
        }
    )

    val phoneNumberField = FormField(
        scope = screenModelScope,
        initialValue = initialUserInfo.phoneNumber,
        validation = flowBlock {
            ValidationResult.of(it) {
                notBlank(Res.string.cant_be_blank)
                matchRegex(Res.string.invalid_format, Constants.PHONE_REGEX)
            }
        }
    )

    init {
        loadUserProfile()
        observeFieldChanges()
    }

    private fun loadUserProfile() {
        screenModelScope.launch {
            onUpdateState()
            getUserProfileUseCase()
                .ifSuccess { userInfoResult ->
                    val userInfo = userInfoResult.data
                    updateFields(userInfo)
                    initialUserInfo = userInfo
                }.ifError { error ->
                    handleUserProfileError(error.error)
                }
            onUpdatedState()
        }
    }

    private fun observeFieldChanges() {
        screenModelScope.launch {
            combine(
                fullNameField.data,
                phoneNumberField.data
            ) { fullName, phoneNumber ->
                UserInfo(
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    isEmployer = _uiState.value.userInfo.isEmployer
                )
            }.collect { userInfo ->
                updateUIState(userInfo)
            }
        }
    }


    override fun onEvent(event: UserProfileContract.Event) {
        when (event) {
            is UserProfileContract.Event.OnFullNameChanged -> fullNameField.setValue(event.fullName)
            is UserProfileContract.Event.OnPhoneNumberChanged -> phoneNumberField.setValue(event.phoneNumber)
            is UserProfileContract.Event.OnSaveProfile -> saveProfile()
            is UserProfileContract.Event.OnLogout -> logout()
        }
    }


    private fun updateFields(userInfo: UserInfo) {
        fullNameField.setValue(userInfo.fullName)
        phoneNumberField.setValue(userInfo.phoneNumber)
    }

    private fun updateUIState(userInfo: UserInfo) {
        val isDataChanged = isUserInfoChanged(userInfo)
        val canSave = isDataChanged && fullNameField.validate() && phoneNumberField.validate()
        _uiState.update { it.copy(userInfo = userInfo, isDataChanged = isDataChanged, canSave = canSave) }
    }

    private fun isUserInfoChanged(currentUserInfo: UserInfo): Boolean {
        val trimmedCurrentFullName = currentUserInfo.fullName.trim().replace("\\s+".toRegex(), " ")
        val trimmedInitialFullName = initialUserInfo.fullName.trim().replace("\\s+".toRegex(), " ")

        return trimmedCurrentFullName != trimmedInitialFullName ||
                currentUserInfo.phoneNumber != initialUserInfo.phoneNumber
    }

    private fun saveProfile() {
        screenModelScope.launch {
            onUpdateState()
            val updatedUserInfo = _uiState.value.userInfo.copy(
                fullName = _uiState.value.userInfo.fullName.trim().replace("\\s+".toRegex(), " ")
            )
            updateUserProfileUseCase(updatedUserInfo)
                .ifSuccess {
                    handleProfileUpdateSuccess(updatedUserInfo)
                }
                .ifError { error ->
                    showErrorMessage(error.error)
                }
            onUpdatedState()
        }
    }

    private fun handleProfileUpdateSuccess(updatedUserInfo: UserInfo) {
        initialUserInfo = updatedUserInfo
        updateUIState(updatedUserInfo)
        showSuccessMessage(Res.string.profile_updated)
    }

    private fun handleUserProfileError(error: DataError) {
        if (error == DataError.Local.USER_NOT_LOGGED_IN) {
            screenModelScope.launch {
                launchEffect(UserProfileContract.Effect.LoggedOut)
            }
        }
        showErrorMessage(error)
    }


    private fun logout() {
        screenModelScope.launch {
            onUpdateState()
            logoutUseCase()
                .ifSuccess {
                    launchEffect(UserProfileContract.Effect.LoggedOut)
                }
                .ifError { error ->
                    showErrorMessage(error.error)
                }
            onUpdatedState()
        }
    }


    private fun showErrorMessage(error: DataError) {
        notificationManager.show(error.toNotificationModel())
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


}