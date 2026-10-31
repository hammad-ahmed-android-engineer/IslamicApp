package com.ne.muslimprayers.qiblafinder.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerAlarmsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.TogglePrayerAlarmUseCase
import com.ne.muslimprayers.qiblafinder.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrayerAlarmUiState(
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class PrayerAlarmViewModel @Inject constructor(
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayerAlarmUiState())
    val uiState: StateFlow<PrayerAlarmUiState> = _uiState.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPrayerAlarmsUseCase().collect { list ->
                _uiState.update {
                    it.copy(
                        alarms = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleAlarm(alarmItem: PrayerAlarmItem) {
        viewModelScope.launch {
            val newState = !alarmItem.isAlarmActive
            togglePrayerAlarmUseCase(alarmItem, newState)

            if (newState) {
                alarmScheduler.scheduleAlarm(alarmItem.copy(isAlarmActive = true))
            } else {
                alarmScheduler.cancelAlarm(alarmItem.id)
            }
        }
    }
}
