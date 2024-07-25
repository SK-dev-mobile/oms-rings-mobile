package skdev.omsrings.mobile.presentation.feature_profile

object UserProfileContract {

    data class UIState(
        val isDataChanged: Boolean = false,
        val canSave: Boolean = false,
        val fullName: String = "",
        val phoneNumber: String = "",
        val isEmployer: Boolean = false
    )


    sealed interface Event {
        data class OnFullNameChanged(val fullName: String) : Event
        data class OnPhoneNumberChanged(val phoneNumber: String) : Event
        data object OnSaveProfile : Event
        data object OnLogout : Event
    }

    sealed interface Effect {
        data object ProfileUpdated : Effect
        data object LoggedOut : Effect
    }
}