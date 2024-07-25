package skdev.omsrings.mobile.presentation.feature_profile

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.cant_be_blank
import omsringsmobile.composeapp.generated.resources.invalid_format
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.Constants
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.flowBlock
import skdev.omsrings.mobile.utils.fields.validators.ValidationResult
import skdev.omsrings.mobile.utils.fields.validators.matchRegex
import skdev.omsrings.mobile.utils.fields.validators.notBlank
import skdev.omsrings.mobile.utils.notification.NotificationManager

class UserProfileScreenModel(
    notificationManager: NotificationManager
) : BaseScreenModel<UserProfileContract.Event, UserProfileContract.Effect>(notificationManager) {
    private val _uiState = MutableStateFlow(
        UserProfileContract.UIState(
            fullName = FormField(
                scope = screenModelScope,
                initialValue = "",
                validation = flowBlock {
                    ValidationResult.of(it) {
                        notBlank(Res.string.cant_be_blank)
                    }
                }
            ),
            phoneNumber = FormField(
                scope = screenModelScope,
                initialValue = "",
                validation = flowBlock {
                    ValidationResult.of(it) {
                        notBlank(Res.string.cant_be_blank)
                        matchRegex(Res.string.invalid_format, Constants.PHONE_REGEX)
                    }
                }
            )
        )
    )
    val uiState: StateFlow<UserProfileContract.UIState> = _uiState.asStateFlow()


    override fun onEvent(event: UserProfileContract.Event) {
        TODO("Not yet implemented")
    }


}