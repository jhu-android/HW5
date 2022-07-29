package hwang.joy.hw5

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AliensViewModel(viewModelScope: CoroutineScope): ViewModel() {

    private val alienAlerter = AlienAlerter(viewModelScope)
    val alertsFlow: Flow<AlienAlert> = flow { alienAlerter.alerts }

    suspend fun startAlienReporting() {
        alienAlerter.startReporting()
    }

}