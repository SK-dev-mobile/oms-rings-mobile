package skdev.omsrings.mobile.presentation.feature_auth

import cafe.adriel.voyager.core.model.screenModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignUpUserUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.ifError
import skdev.omsrings.mobile.utils.result.ifSuccess

class AuthScreenModel(
    notificationManager: NotificationManager,
    private val signUpUserUseCase: SignUpUserUseCase,
) : BaseScreenModel<AuthScreenContract.Event, AuthScreenContract.Effect>(notificationManager) {
    override fun onEvent(event: AuthScreenContract.Event) {
        when(event) {
            AuthScreenContract.Event.OnDispose -> {

            }
            AuthScreenContract.Event.OnSignUpClicked -> {
                screenModelScope.launch {
                    signUpUserUseCase("stakancheck@gmail.com", "qwerty123")
                        .ifSuccess {
                            Napier.d(tag = "AuthScreenModel") { "Success, uid -> ${it.data}" }
                        }
                }
            }
            AuthScreenContract.Event.OnStart -> {

            }
        }
    }
}
