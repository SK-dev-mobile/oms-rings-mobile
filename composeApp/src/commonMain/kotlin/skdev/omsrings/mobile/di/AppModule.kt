package skdev.omsrings.mobile.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import skdev.omsrings.mobile.data.repository.AuthRepositoryImpl
import skdev.omsrings.mobile.data.repository.FirebaseUserSettingsRepository
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.domain.usecase.feature_auth.SendResetPasswordEmailUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignInUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignUpUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.ClearOldOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.GetUserSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateNotificationSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateShowClearedOrdersSettingsUseCase
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreenModel
import skdev.omsrings.mobile.presentation.feature_main.MainScreenModel
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsModel
import skdev.omsrings.mobile.utils.notification.NotificationManager


private val data = module {
    // Add there data DI defenitions


    single<AuthRepository> {
        AuthRepositoryImpl(
            firebaseAuth = Firebase.auth,
            firestore = Firebase.firestore,
        )
    }
    // TODO: Replace userId with real user id
    single<UserSettingsRepository> {
        FirebaseUserSettingsRepository(
            userId = "1",
            firestore = Firebase.firestore
        )
    }

}

private val utils = module {
    // Add there utils defenitions

    single<NotificationManager> {
        NotificationManager()
    }
}

private val viewModels = module {
    // Add there ViewModels DI defenitions

    factory<MainScreenModel> {
        MainScreenModel(
            notificationManager = get()
        )
    }

    factory<AuthScreenModel> {
        AuthScreenModel(
            notificationManager = get(),
            signUpUserUseCase = get(),
            signInUserUseCase = get(),
            sendResetPasswordEmailUseCase = get(),
        )
    }

    factory<UserSettingsModel> {
        UserSettingsModel(
            notificationManager = get(),
            getUserSettingsUseCase = get(),
            updateNotificationSettingsUseCase = get(),
            updateShowClearedOrdersSettingsUseCase = get(),
            clearOldOrdersUseCase = get()
        )
    }

}

private val useCases = module {
    // Add there UseCases DI defenitions

    factory<SignInUserUseCase> {
        SignInUserUseCase(
            authRepository = get(),
            notificationManager = get()
        )
    }

    factory<SignUpUserUseCase> {
        SignUpUserUseCase(
            authRepository = get(),
            notificationManager = get()
        )
    }

    factory<SendResetPasswordEmailUseCase> {
        SendResetPasswordEmailUseCase(
            authRepository = get(),
            notificationManager = get()
        )
    }

    // Feature User Settings
    factory<GetUserSettingsUseCase> { GetUserSettingsUseCase(repository = get()) }
    factory<UpdateNotificationSettingsUseCase> { UpdateNotificationSettingsUseCase(repository = get()) }
    factory<UpdateShowClearedOrdersSettingsUseCase> { UpdateShowClearedOrdersSettingsUseCase(repository = get()) }
    factory<ClearOldOrdersUseCase> { ClearOldOrdersUseCase(repository = get()) }
}

private fun commonModule() = listOf(data, utils, viewModels, useCases)

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        appDeclaration()
        modules(
            commonModule() + platformModule()
        )
    }
}

// Platform specific modules
expect fun platformModule(): List<Module>
