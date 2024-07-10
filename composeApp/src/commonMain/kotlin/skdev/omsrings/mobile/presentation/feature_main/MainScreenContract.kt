package skdev.omsrings.mobile.presentation.feature_main

object MainScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
    }

    sealed interface Effect {
        object NavigateToDetail : Effect
    }
}
