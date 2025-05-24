package skdev.omsrings.mobile.presentation.feature_main

import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.model.DayInfoModel
import skdev.omsrings.mobile.presentation.feature_main.components.YearMonth

object MainScreenContract {
    sealed interface Event {
        object OnStart : Event
        object OnDispose : Event
        data class OnLoadMonthInfo(val month: YearMonth) : Event
    }

    sealed interface Effect {
        object NavigateToDetail : Effect
    }

    data class State(
        val calendarDays: Map<LocalDate, DayInfoModel> = emptyMap(),
        val isEmployer: Boolean = false,
    )
}
