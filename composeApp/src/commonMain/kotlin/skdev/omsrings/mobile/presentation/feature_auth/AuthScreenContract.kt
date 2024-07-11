package skdev.omsrings.mobile.presentation.feature_auth

object AuthScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
        object OnSignUpClicked : Event
    }

    sealed interface Effect {
        object NavigateToDetail : Effect
    }
}
