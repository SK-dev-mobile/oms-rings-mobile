package skdev.omsrings.mobile.presentation.feature_profile

import skdev.omsrings.mobile.domain.model.UserInfo

object UserProfileContract {

    data class UIState(
        val userInfo: UserInfo = UserInfo.DEFAULT,
        val isDataChanged: Boolean = false,
        val canSave: Boolean = false,
    )


    sealed interface Event {
        data class OnFullNameChanged(val fullName: String) : Event
        data class OnPhoneNumberChanged(val phoneNumber: String) : Event
        data object OnSaveProfile : Event
        data object OnLogout : Event
    }

    sealed interface Effect {
        data object LoggedOut : Effect
    }
}