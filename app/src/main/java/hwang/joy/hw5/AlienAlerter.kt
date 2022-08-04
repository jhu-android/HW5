package hwang.joy.hw5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlienAlerter(private val scope: CoroutineScope) {

    private val alienApiService = AlienApiService.create()

    private val _alerts = MutableStateFlow(AlienAlert())
    val alerts: Flow<AlienAlert>
        get() = _alerts

    private var continuePolling = true

    private suspend fun emitUfos(n: Int) {
        val response = alienApiService.getUfos(n.toString())
        if (response.code() == 404) {
            continuePolling = false
        } else {
            response.body()?.let {
                _alerts.emit(AlienAlert(it))
            }
        }
    }

    suspend fun startReporting() {
        scope.launch(Dispatchers.IO) {
            var n = 1
            while (continuePolling) {
                emitUfos(n)
                n += 1
                delay(2000L)
            }
        }
    }



}