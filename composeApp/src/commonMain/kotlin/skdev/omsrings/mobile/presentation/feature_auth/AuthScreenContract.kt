package skdev.omsrings.mobile.presentation.feature_auth

import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole

object AuthScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
        object OnForgotPasswordClicked : Event
        object OnSignInClicked : Event
        data class OnSignUpClicked(val role: UserRole) : Event
        object OnResetPasswordClicked : Event
        object OnCreateAccountClicked : Event
        object OnBackClicked : Event
    }

    sealed interface Effect {
        object NavigateToMainScreen : Effect
        object NavigateBack : Effect

    }


    sealed interface State {
        object SignIn : State
        object SignUp : State
        object PasswordReset : State
    }
}
