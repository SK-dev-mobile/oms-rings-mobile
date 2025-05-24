package skdev.omsrings.mobile.presentation.feature_daily_cart

import androidx.compose.runtime.Immutable
import skdev.omsrings.mobile.domain.model.FolderWithItems
import skdev.omsrings.mobile.domain.model.ItemWithQuantity

class DailyCartContract {
    @Immutable
    data class State(
        val isLoading: Boolean = false,
        val selectedDate: String = "",
        val folders: List<FolderWithItems> = emptyList(),
        val totalAmount: Int = 0
    )

    sealed interface Event {
        data object OnBackClicked : Event
        data object OnRefresh : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
} 