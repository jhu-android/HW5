package hwang.joy.hw5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class AlienAlerter(private val scope: CoroutineScope) {
//    val alerts = MutableStateFlow(emptyList<UfoPosition>() as AlienAlert)
    val alerts: Flow<AlienAlert> = flow {
        for (i in 0..3) {

        }
    }

    private val alienApiService = AlienApiService.create()

    suspend fun startReporting() {
        scope.launch(Dispatchers.IO) {
            alienApiService.getPosition(n = "1").body()
//            val ships = alienApiService.getPosition(n = "1").body()
//            if (ships != null) {
//                alerts.emit(ships)
//            }
        }
    }

    suspend fun
}