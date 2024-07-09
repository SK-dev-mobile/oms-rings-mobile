# Проект OMS ЖБИ Кольца

Мобильное приложение для учета заказов

Основной пример: https://github.com/izluchatel-dev/RingsPlus/

---

# Стурктура проекта
(Появится позже)

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

