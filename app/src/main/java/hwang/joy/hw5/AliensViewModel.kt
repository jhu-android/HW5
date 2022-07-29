package hwang.joy.hw5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map

class AliensViewModel(): ViewModel() {

private val alienAlerter = AlienAlerter(viewModelScope)

suspend fun startAlienReporting() {
    alienAlerter.startReporting()
}

val alertState = alienAlerter.alerts.map { ImmutableList(it.aliens) }

}