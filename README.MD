# Проект OMS ЖБИ Кольца

Мобильное приложение для учета заказов

Основной пример: https://github.com/izluchatel-dev/RingsPlus/

---

# Стурктура проекта
(Появится позже)


# Использовние утилит

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

