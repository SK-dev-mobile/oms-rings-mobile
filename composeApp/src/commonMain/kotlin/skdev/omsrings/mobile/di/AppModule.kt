package skdev.omsrings.mobile.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import skdev.omsrings.mobile.presentation.feature_main.MainScreenModel
import skdev.omsrings.mobile.utils.notification.NotificationManager

private val data = module {
    // Add there data DI defenitions

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
}

private val useCases = module {
    // Add there UseCases DI defenitions

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
