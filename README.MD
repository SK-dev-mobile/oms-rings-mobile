# Проект OMS ЖБИ Кольца

Мобильное приложение для учета заказов железобетонных изделий. Приложение разработано с использованием многоплатформенной архитектуры Kotlin и Jetpack Compose для Android и iOS платформ. Основной функционал приложения включает:

- **Учет заказов**: Возможность добавления, редактирования и удаления заказов.
- **Интеграция с Firebase**: Аутентификация пользователей, хранение данных и отправка уведомлений.
- **Валидация форм**: Поля ввода с асинхронной валидацией данных.
- **Поддержка нескольких платформ**: Общий код для Android и iOS с использованием Kotlin Multiplatform.
- **Систему уведомлений**: Администраторы получают уведомления об изменениях в заказах.

![Demo](assets/oms-rings-demostration.png)

Основной пример: https://github.com/izluchatel-dev/RingsPlus/

---

# Стурктура проекта

## Общая
`composeApp/`: Директория, содержащая код приложения, написанного с использованием Jetpack Compose. (Также Android платформа)
`iosApp/`: Директория, содержащая код iOS-версии приложения.

## composeApp

Проект composeApp организован в соответствии с многоплатформенной архитектурой Kotlin. Он разделен на несколько основных слоев:

**Общий слой (`commonMain`)**: Этот слой содержит код, который будет использоваться на всех платформах. Обычно здесь размещаются модели данных, бизнес-логика и некоторые интерфейсы для взаимодействия с платформенно-специфичными реализациями. В проекте используется Compose Multiplatform, поэтоу здесь же располагаются привычные пакеты `presentation`, `ui`.

**Android-слой (`androidMain`)**: Этот слой содержит код, который будет использоваться только на Android. Здесь специфичные для Android классы и ресурсы, такие как Activities, Services, и т.д. `App.android.kt` является частью этого слоя.

**iOS-слой (`iosMain`)**: Этот слой содержит код, который будет использоваться только на iOS. Здесь специфичные для iOS классы и ресурсы. `main.kt` является частью этого слоя.

# Использовние утилит

## Строковые ресурсы

Для добавления указать значение в `composeApp/src/commonMain/composeResources/values/strings.xml`
Строковые ресурсы можно использовать в коде Kotlin следующим образом:
```kotlin
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.Res.new_string

val myStringRes: StringResource = Res.string.new_string
```

В `Composable` можно получить `String` из `StringResource` с помощью функции `stringResource()`


## FormFiled – Поле ввода

Каждый экземпляр `FormField` включает в себя следующие ключевые элементы:

Данные поля (`data`): Хранит текущее значение поля ввода. Это значение может быть любого типа, определенного при создании экземпляра FormField.
Валидация (`validation`): Функция валидации, которая принимает поток данных поля и возвращает поток с результатами валидации. Это позволяет асинхронно проверять данные на соответствие заданным критериям.
Состояние ошибки (`error`): Поток, который содержит информацию об ошибке валидации. Если данные проходят валидацию, состояние ошибки будет null.
Флаг валидности (`isValid`): Поток, который указывает, прошли ли данные успешно валидацию. Это позволяет легко управлять состоянием UI в зависимости от валидности данных.

**ScreenModel**
```kotlin
// Создание
val commentField: FormField<String, StringResource> = FormField(
    scope = screenModelScope,
    initialValue = "",
    validation = flowBlock {
        ValidationResult.of(it) {
            notBlank(Res.string.cant_be_blank) // Не пустое
        }
    }
)

// <...>

private suspend fun doSmth() {
    if (validateAll(commentField)) {
        // Действие на успешную проверку
    }
}
```

**Screen**
```kotlin
import skdev.omsrings.mobile.ui.components.fields.TextField   // Custom TextField (для телефона PhoneField)

// Получение
val (commentValue, commentSetter) = screenModel.commentField.data.collectAsMutableState()
val commentError: StringResource? by screenModel.commentField.error.collectAsState()
val updating by screenModel.updating.collectAsState()

// Использование
TextField(
    value = commentValue,
    enabled = !updating,
    onValueChange = commentSetter,
    isError = commentError != null,
    supportingText = SupportingText(commentError?.let { stringResource(it) }),
)
```

---

# Best practies

## Передача intents

Вместо большого количества onClick событий лучше передавать обработчик событий.

**🚫 Пример как не нужно:**
```kotlin
Menu(
    onMenuItemClick = { screenModel.onEvent(MenuItemEvent) },
    onErrorAction = { screenModel.onEvent(ErrorEvent) },
    /* еще 100 onClick */
)

@Composable
private fun Menu(
    onMenuItemClick: () -> Unit,
    onErrorAction: () -> Unit,
    /* еще 100 onClick */
)
```

**✅ Пример как нужно:** 
```kotlin
typealias OnAction = (Event) -> Unit

Menu(
    onAction = { screenModel.onEvent(it) },
)

@Composable
private fun Menu(
    onAction: OnAction,
) {
    Button(onClick = { onAction(MenuItemEvent) }) {
        Text("Click me")
    }
}
```

> Важно! Актуально только для экранов и функций внутри одного feature. Если функцию можно переиспользовать, то оставить нужно старые callback события.


## UseCase

`UseCase` выполняет только одну конкретную задачу, которую можно переиспользовать, например, для разных платформ. Внутри `UseCase` стоит при необходимости делать обработку результата и выпод ошибки в Notification Center. 

**Например:**
```kotlin
class SignInWithLoginUseCase(
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
) {
    operator suspend fun invoke(login: String, password: String): DataResult<Unit, DataError> {
        return authRepository.signIn(login, password).also {
            it.ifError {
                notificationManager.show(it.error.toNotificationModel())
            }
        }
    }
    
    companion object {
        const val TAG = "SignInWithLoginUseCase"
    }
}
```

## Error managment

Для удобного управления ошибками используется собственный `Error` и `DataResult`. Он позволяет показывать ошибки в удобном виде, а также управлять ими.

Если в каком то месте приложения может возникнуть ошибка, то стоит обернуть возвращаемое функцией значение в `DataResult` с указанием типа возвращаемого значения и типа возможной ошибки. Группы ошибок можно добавлять, наследуясь от `Error`. Использовать только enum для ошибок.
Для ошибок, которые подлежат оповещению пользователя стоит создать строковый ресурс и указать в `Error.toNotificationModel()`


# Compose Multiplatform Application – Настройка проекта

## Перед запуском
 - проверить систему с [KDoctor](https://github.com/Kotlin/kdoctor)
 - установить JDK 17 или выше
 - добавить `local.properties` файл в корень проекта и указать путь к Android SDK там
    Пример: `sdk.dir=/your-path/sdk` 

Сборка application bundle:
 - запустить `./gradlew :composeApp:assembleDebug`
 - найти `.apk` файл в `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### iOS
Чтобы запустить на iPhone device/simulator:
 - Открыть `iosApp/iosApp.xcproject` в Xcode и запустить станадартную конфигурацию
 - Или использовать [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) для Android Studio

