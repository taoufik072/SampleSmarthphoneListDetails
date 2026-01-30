package fr.taoufikcode.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.taoufikcode.domain.usecase.smartphone.GetSmartphoneDetailsByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartphoneDetailsViewModel @Inject constructor(
    private val getSmartphoneDetailsByIdUseCase: GetSmartphoneDetailsByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val smartphoneId: String = checkNotNull(savedStateHandle["smartphoneId"])
    private val _state = MutableStateFlow(SmartphoneDetailsState())
    val state: StateFlow<SmartphoneDetailsState> = _state.asStateFlow()

    init {
        loadSmartphone(smartphoneId)
    }

    private fun loadSmartphone(id: String = smartphoneId) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getSmartphoneDetailsByIdUseCase(id)
                .onSuccess { smartphone ->
                    _state.update {
                        it.copy(
                            smartphone = smartphone.toUi(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load smartphone"
                        )
                    }
                }
        }
    }
}
