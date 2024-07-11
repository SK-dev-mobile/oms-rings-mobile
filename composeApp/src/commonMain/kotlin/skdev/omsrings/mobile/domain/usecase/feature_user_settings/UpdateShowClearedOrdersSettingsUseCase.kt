package skdev.omsrings.mobile.domain.usecase.feature_user_settings

import skdev.omsrings.mobile.domain.repository.UserSettingsRepository
import skdev.omsrings.mobile.utils.error.DataError
import skdev.omsrings.mobile.utils.result.DataResult

class UpdateShowClearedOrdersSettingsUseCase(private val repository: UserSettingsRepository) {
    suspend operator fun invoke(show: Boolean): DataResult<Unit, DataError> =
        repository.updateShowClearedOrdersSettings(show)
}