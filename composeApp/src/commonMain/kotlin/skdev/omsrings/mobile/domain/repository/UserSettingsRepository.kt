package skdev.omsrings.mobile.domain.repository

import skdev.omsrings.mobile.domain.model.UserSettings

interface UserSettingsRepository {
    /**
     * Получает текущие настройки пользователя.
     * @return объект UserSettings
     */
    suspend fun getUserSettings(): UserSettings

    /**
     * Обновляет настройку получения уведомлений.
     * @param enabled true, если уведомления включены, false в противном случае
     */
    suspend fun updateNotificationSettings(enabled: Boolean)

    /**
     * Обновляет настройку отображения списанных заказов.
     * @param show true, если нужно показывать списанные заказы, false в противном случае
     */
    suspend fun updateShowClearedOrdersSettings(show: Boolean)

    /**
     * Очищает данные о заказах старше 3 месяцев.
     * @return количество удаленных заказов
     */
    suspend fun clearOldOrders(): Int

    /**
     * Сбрасывает все настройки пользователя к значениям по умолчанию.
     */
    suspend fun resetToDefault()

}