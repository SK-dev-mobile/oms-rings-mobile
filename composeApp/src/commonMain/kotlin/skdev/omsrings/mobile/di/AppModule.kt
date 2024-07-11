package skdev.omsrings.mobile.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import skdev.omsrings.mobile.data.repository.AuthRepositoryImpl
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignInUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignUpUserUseCase
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreenModel
import skdev.omsrings.mobile.presentation.feature_main.MainScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager

private val data = module {
    // Add there data DI defenitions


    single<AuthRepository> {
        AuthRepositoryImpl(
            firebaseAuth = Firebase.auth
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
            signUpUserUseCase = get()
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
