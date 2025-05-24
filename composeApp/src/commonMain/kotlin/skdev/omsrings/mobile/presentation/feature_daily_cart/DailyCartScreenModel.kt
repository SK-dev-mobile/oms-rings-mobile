package skdev.omsrings.mobile.presentation.feature_daily_cart

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import skdev.omsrings.mobile.domain.usecase.feature_daily_cart.GetDailyCartItemsUseCase
import skdev.omsrings.mobile.presentation.base.BaseScreenModel
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format
import skdev.omsrings.mobile.utils.notification.NotificationManager
import skdev.omsrings.mobile.utils.result.ifSuccess

class DailyCartScreenModel(
    private val notificationManager: NotificationManager,
    private val getDailyCartItemsUseCase: GetDailyCartItemsUseCase,
    private val selectedDate: LocalDate
) : BaseScreenModel<DailyCartContract.Event, DailyCartContract.Effect>(notificationManager) {

    private val _uiState = MutableStateFlow(createInitialState(selectedDate))
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun createInitialState(selectedDate: LocalDate) = DailyCartContract.State(
        selectedDate = selectedDate.format(DateTimePattern.SIMPLE_DATE)
    )

    override fun onEvent(event: DailyCartContract.Event) {
        when (event) {
            DailyCartContract.Event.OnBackClicked -> navigateBack()
            DailyCartContract.Event.OnRefresh -> loadData()
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getDailyCartItemsUseCase(selectedDate).ifSuccess { result ->
                val foldersWithItems = result.data
                val totalAmount = foldersWithItems.sumOf { folder ->
                    folder.items.sumOf { it.quantity }
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        folders = foldersWithItems,
                        totalAmount = totalAmount
                    )
                }
            }
        }
    }

    private fun navigateBack() {
        screenModelScope.launch {
            launchEffect(DailyCartContract.Effect.NavigateBack)
        }
    }
} 
