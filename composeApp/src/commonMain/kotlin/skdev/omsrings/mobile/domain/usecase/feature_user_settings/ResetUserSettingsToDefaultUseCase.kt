package skdev.omsrings.mobile.domain.usecase.feature_user_settings

import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class ResetUserSettingsToDefaultUseCase(private val repository: UserSettingsRepository) {
    suspend operator fun invoke(): DataResult<Unit, DataError> =
        repository.resetToDefault()
}