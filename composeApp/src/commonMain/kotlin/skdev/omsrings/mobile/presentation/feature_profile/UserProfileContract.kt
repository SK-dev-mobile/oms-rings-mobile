package skdev.omsrings.mobile.presentation.feature_profile

object UserProfileContract {
    
    data class UIState(
        val isOk: Boolean = false
    )


    sealed interface Event {

    }

    sealed interface Effect {

    }
}