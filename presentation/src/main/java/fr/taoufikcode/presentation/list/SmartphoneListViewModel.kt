package fr.taoufikcode.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.taoufikcode.common.isExpired
import fr.taoufikcode.domain.usecase.home.GetSmartphonesSummaryUseCase
import fr.taoufikcode.domain.usecase.home.GetSyncStatusUseCase
import fr.taoufikcode.domain.usecase.home.SaveSyncDateUseCase
import fr.taoufikcode.domain.usecase.home.SyncSmartphonesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SmartphoneListViewModel(
    private val getSmartphonesSummaryUseCase: GetSmartphonesSummaryUseCase,
    private val syncSmartphonesUseCase: SyncSmartphonesUseCase,
    private val getSyncStatusUseCase: GetSyncStatusUseCase,
    private val saveSyncDateUseCase: SaveSyncDateUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SmartphoneListState())
    val state = _state.asStateFlow()

    init {
        loadSmartphones()
        checkAndSync()
    }

    private val _events = Channel<SmartphoneListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ListActions) {
        when (action) {
            is ListActions.Refresh -> onRefresh()
            else -> Unit
        }
    }

    private fun onRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            syncSmartphonesUseCase()
                .onSuccess {
                    _state.update { it.copy(isRefreshing = false, error = null) }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = error.message ?: "Sync failed",
                        )
                    }
                    _events.trySend(SmartphoneListEvent.ShowError("Sync failed: ${error.message}"))
                }
        }
    }

    private fun loadSmartphones() {
        getSmartphonesSummaryUseCase()
            .onEach { smartphonesList ->
                _state.update {
                    it.copy(
                        smartphones = smartphonesList.map { smartphone -> smartphone.toUi() },
                        isLoading = false,
                        error = null,
                    )
                }
            }.catch { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load smartphones",
                    )
                }
                _events.trySend(
                    SmartphoneListEvent.ShowError("Failed to load data: ${error.message}"),
                )
            }.launchIn(viewModelScope)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun checkAndSync() {
        viewModelScope.launch {
            try {
                val syncStatus = getSyncStatusUseCase.invoke().first()
                if (syncStatus.isExpired(minutePassed = SYNC_INTERVAL_MINUTES)) {
                    syncSmartphonesUseCase()
                        .onSuccess {
                            saveSyncDateUseCase(System.currentTimeMillis())
                        }.onFailure { error ->
                            _events.trySend(
                                SmartphoneListEvent.ShowError("Sync failed: ${error.message}"),
                            )
                        }
                }
            } catch (error: Exception) {
                _events.trySend(
                    SmartphoneListEvent.ShowError("Failed to check sync status: ${error.message}"),
                )
            }
        }
    }

    companion object {
        private const val SYNC_INTERVAL_MINUTES = 5L
    }
}
