package skdev.omsrings.mobile.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import skdev.omsrings.mobile.data.repository.FirebaseAuthRepository
import skdev.omsrings.mobile.data.repository.FirebaseInventoryRepository
import skdev.omsrings.mobile.data.repository.FirebaseOrderRepository
import skdev.omsrings.mobile.data.repository.FirebaseUserSettingsRepository
import skdev.omsrings.mobile.domain.repository.AuthRepository
import skdev.omsrings.mobile.domain.repository.InventoryRepository
import skdev.omsrings.mobile.domain.repository.OrderRepository
import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.domain.usecase.feature_auth.GetUserInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.IsAuthorizedUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.LogOutUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SendResetPasswordEmailUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignInUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_auth.SignUpUserUseCase
import skdev.omsrings.mobile.domain.usecase.feature_daily_cart.GetDailyCartItemsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.GetDayOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.SetDayLockedStatusUseCase
import skdev.omsrings.mobile.domain.usecase.feature_day_orders.UpdateOrderStatusUseCase
import skdev.omsrings.mobile.domain.usecase.feature_main.GetDaysInfoUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.CreateOrderUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.GetFoldersAndItemsInventory
import skdev.omsrings.mobile.domain.usecase.feature_order.GetInventoryItemsByIdsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.GetOrderByIdUseCase
import skdev.omsrings.mobile.domain.usecase.feature_order.UpdateOrderUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.ClearOldOrdersUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.GetUserSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateNotificationSettingsUseCase
import skdev.omsrings.mobile.domain.usecase.feature_user_settings.UpdateShowClearedOrdersSettingsUseCase
import skdev.omsrings.mobile.presentation.feature_auth.AuthScreenModel
import skdev.omsrings.mobile.presentation.feature_daily_cart.DailyCartScreenModel
import skdev.omsrings.mobile.presentation.feature_day_orders.DayOrdersScreenModel
import skdev.omsrings.mobile.presentation.feature_inventory_management.InventoryManagementScreenModel
import skdev.omsrings.mobile.presentation.feature_main.MainScreenModel
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormScreenModel
import skdev.omsrings.mobile.presentation.feature_user_settings.UserSettingsModel
import skdev.omsrings.mobile.utils.notification.NotificationManager


private val data = module {
    single<FirebaseFirestore> { Firebase.firestore }

    single<AuthRepository> {
        FirebaseAuthRepository(
            firebaseAuth = Firebase.auth,
            firestore = get()
        )
    }


    single<UserSettingsRepository> {
        FirebaseUserSettingsRepository(
            firebaseAuth = Firebase.auth,
            firestore = get()
        )
    }

    single<InventoryRepository> {
        FirebaseInventoryRepository(
            firestore = get()
        )
    }

    single<OrderRepository> {
        FirebaseOrderRepository(firestore = get())
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
            notificationManager = get(),
            getDaysInfoUseCase = get()
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

    // Feature Inventory Management
    factory<InventoryManagementScreenModel> {
        InventoryManagementScreenModel(
            inventoryRepository = get(),
            notificationManager = get()
        )
    }

    // Feature User Settings
    factory<UserSettingsModel> {
        UserSettingsModel(
            notificationManager = get(),
            getUserSettingsUseCase = get(),
            updateNotificationSettingsUseCase = get(),
            updateShowClearedOrdersSettingsUseCase = get(),
            clearOldOrdersUseCase = get()
        )
    }

    // OrderForm
    factory<OrderFormScreenModel> { parameters ->
        OrderFormScreenModel(
            notificationManager = get(),
            createOrderUseCase = get(),
            updateOrderUseCase = get(),
            getFoldersAndItemsInventory = get(),
            getOrderByIdUseCase = get(),
            getInventoryItemsByIdsUseCase = get(),
            getUserInfoUseCase = get(),
            selectedDate = parameters.get(),
            orderId = parameters.getOrNull()
        )
    }

    // Day Orders
    factory<DayOrdersScreenModel> { parameters ->
        DayOrdersScreenModel(
            notificationManager = get(),
            getDayOrdersUseCase = get(),
            updateOrderStatusUseCase = get(),
            selectedDate = parameters.get(),
            getDayInfoUseCase = get(),
            setDayLockedStatusUseCase = get(),
        )
    }


    factory<DailyCartScreenModel> { parameters ->
        DailyCartScreenModel(
            notificationManager = get(),
            getDailyCartItemsUseCase = get(),
            selectedDate = parameters.get()
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
    factory<UpdateShowClearedOrdersSettingsUseCase> {
        UpdateShowClearedOrdersSettingsUseCase(
            repository = get()
        )
    }
    factory<ClearOldOrdersUseCase> { ClearOldOrdersUseCase(repository = get()) }

    // Feature Create Order
    factory<CreateOrderUseCase> {
        CreateOrderUseCase(
            repository = get(),
            notificationManager = get()
        )
    }
    factory<GetFoldersAndItemsInventory> { GetFoldersAndItemsInventory(repository = get()) }
    factory<GetOrderByIdUseCase> { GetOrderByIdUseCase(repository = get()) }
    factory<UpdateOrderUseCase> { UpdateOrderUseCase(repository = get()) }
    factory<GetInventoryItemsByIdsUseCase> { GetInventoryItemsByIdsUseCase(repository = get()) }

    // Feature Main
    factory<GetDaysInfoUseCase> {
        GetDaysInfoUseCase(
            repository = get(),
            notificationManager = get()
        )
    }

    factory<IsAuthorizedUseCase> {
        IsAuthorizedUseCase(
            authRepository = get()
        )
    }

    // Feature Day Orders
    factory<GetDayOrdersUseCase> {
        GetDayOrdersUseCase(
            orderRepository = get(),
            inventoryRepository = get(),
            notificationManager = get()
        )
    }

    factory<UpdateOrderStatusUseCase> {
        UpdateOrderStatusUseCase(
            orderRepository = get(),
            authRepository = get(),
            notificationManager = get()
        )
    }

    factory<SetDayLockedStatusUseCase> {
        SetDayLockedStatusUseCase(
            orderRepository = get()
        )
    }

    factory<GetDayInfoUseCase> {
        GetDayInfoUseCase(
            orderRepository = get()
        )
    }

    factory<LogOutUseCase> {
        LogOutUseCase(
            authRepository = get(),
        )
    }

    factory { GetUserInfoUseCase(authRepository = get()) }

    factory {
        GetDailyCartItemsUseCase(
            orderRepository = get(),
            inventoryRepository = get(),
            notificationManager = get()
        )
    }
}

private fun commonModule() = listOf(data, utils, viewModels, useCases)

fun initKoin(
//    appDeclaration: KoinAppDeclaration = {},
) {
    println("Koin initialization started")
    startKoin {
//        appDeclaration()
        modules(
            commonModule() + platformModule()
        )
    }
}

// Platform specific modules
expect fun platformModule(): List<Module>
