package skdev.omsrings.mobile.presentation.feature_profile

import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.utils.fields.FormField

object UserProfileContract {

    data class UIState(
        val fullName: FormField<String, StringResource>,
        val phoneNumber: FormField<String, StringResource>,
        val updating: Boolean = false
    )


    sealed interface Event {
        data class OnFullNameChanged(val newFullName: String) : Event
        data class OnPhoneNumberChanged(val newPhoneNumber: String) : Event
        data object OnSaveProfile : Event
        data object OnLogout : Event
    }

    sealed interface Effect {
        data object ProfileUpdated : Effect
        data object LoggedOut : Effect
    }
}