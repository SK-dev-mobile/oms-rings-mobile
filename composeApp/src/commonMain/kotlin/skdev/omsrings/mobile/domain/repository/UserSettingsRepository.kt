package skdev.omsrings.mobile.domain.repository

import skdev.omsrings.mobile.domain.model.UserSettings
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

interface UserSettingsRepository {
    /**
     * Получает текущие настройки пользователя.
     * @return объект UserSettings
     */
    suspend fun getUserSettings(): DataResult<UserSettings, DataError>

    /**
     * Обновляет настройку получения уведомлений.
     * @param enabled true, если уведомления включены, false в противном случае
     */
    suspend fun updateNotificationSettings(enabled: Boolean): DataResult<Unit, DataError>

    /**
     * Обновляет настройку отображения списанных заказов.
     * @param show true, если нужно показывать списанные заказы, false в противном случае
     */
    suspend fun updateShowClearedOrdersSettings(show: Boolean): DataResult<Unit, DataError>

    /**
     * Очищает данные о заказах старше 3 месяцев.
     * @return количество удаленных заказов
     */
    suspend fun clearOldOrders(): DataResult<Int, DataError>

    /**
     * Сбрасывает все настройки пользователя к значениям по умолчанию.
     */
    suspend fun resetToDefault(): DataResult<Unit, DataError>


}