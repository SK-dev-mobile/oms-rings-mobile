package skdev.omsrings.mobile.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import skdev.omsrings.mobile.ui.components.error.ErrorPresenter
import skdev.omsrings.mobile.ui.components.error.ToastErrorPresenter
import skdev.omsrings.mobile.utils.message.MessageCollector
import skdev.omsrings.mobile.utils.message.ErrorObserver

private val data = module {
    // Add there data DI defenitions

    single<ErrorPresenter> {
        ToastErrorPresenter()
    }

    single<MessageCollector> {
        object : MessageCollector {
            override val observers: List<ErrorObserver>
                get() = listOf(get<ErrorPresenter>())
        }
    }
}

private val viewModels = module {
    // Add there ViewModels DI defenitions

}

private val useCases = module {
    // Add there UseCases DI defenitions

}

private fun commonModule() = listOf(data, viewModels, useCases)

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
