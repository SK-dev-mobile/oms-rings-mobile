package skdev.omsrings.mobile.presentation.feature_auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.password_reset
import omsringsmobile.composeapp.generated.resources.sign_in
import omsringsmobile.composeapp.generated.resources.sign_up
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_auth.content.PasswordResetContent
import skdev.omsrings.mobile.presentation.feature_auth.content.SignInContent
import skdev.omsrings.mobile.presentation.feature_auth.content.SignUpContent
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.theme.values.Dimens

internal typealias OnAction = (AuthScreenContract.Event) -> Unit

object AuthScreen : BaseScreen("auth_screen") {

    @OptIn(InternalVoyagerApi::class, ExperimentalAnimationApi::class)
    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<AuthScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        val updating by screenModel.updating.collectAsState()

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = when (uiState) {
                        AuthScreenContract.State.SignIn -> stringResource(Res.string.sign_in)
                        is AuthScreenContract.State.SignUp -> stringResource(Res.string.sign_up)
                        AuthScreenContract.State.PasswordReset -> stringResource(Res.string.password_reset)
                    },
                    enabledNavigation = (uiState != AuthScreenContract.State.SignIn),
                    onNavigationClicked = {
                        screenModel.onEvent(AuthScreenContract.Event.OnBackClicked)
                    }
                )
            },
            contentWindowInsets = WindowInsets.safeContent,
        ) { paddingValues ->
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                transitionSpec = {
                    when {
                        AuthScreenContract.State.SignIn isTransitioningTo AuthScreenContract.State.SignUp -> {
                            slideInHorizontally(initialOffsetX = { width -> width }) togetherWith fadeOut()
                        }

                        AuthScreenContract.State.SignUp isTransitioningTo AuthScreenContract.State.SignIn -> {
                            slideInHorizontally(initialOffsetX = { width -> -width }) togetherWith fadeOut()
                        }

                        else -> fadeIn() togetherWith fadeOut()
                    }
                },
                targetState = uiState
            ) {
                when (it) {
                    AuthScreenContract.State.SignIn -> SignInContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.spaceMedium),
                        onAction = screenModel::onEvent,
                        updating = updating,
                        emailField = screenModel.emailField,
                        passwordField = screenModel.passwordField
                    )

                    is AuthScreenContract.State.SignUp -> SignUpContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.spaceMedium),
                        onAction = screenModel::onEvent,
                        updating = updating,
                        emailField = screenModel.emailField,
                        passwordField = screenModel.passwordField,
                        passwordConfirmField = screenModel.passwordConfirmField,
                        phoneField = screenModel.phoneField,
                        nameField = screenModel.fullNameField,
                    )

                    AuthScreenContract.State.PasswordReset -> PasswordResetContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.spaceMedium),
                        onAction = screenModel::onEvent,
                        updating = updating,
                        emailField = screenModel.emailField,
                    )
                }
            }
        }

        BackHandler(uiState != AuthScreenContract.State.SignIn) {
            screenModel.onEvent(AuthScreenContract.Event.OnBackClicked)
        }
    }
}



