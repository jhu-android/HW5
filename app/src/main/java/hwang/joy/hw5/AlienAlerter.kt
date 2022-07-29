package hwang.joy.hw5

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class AlienAlerter(private val scope: CoroutineScope) {

    private val alienApiService = AlienApiService.create()

    private val _alerts = MutableStateFlow(AlienAlert())
    val alerts: Flow<AlienAlert>
        get() = _alerts

    var continuePolling = true

    suspend fun emitUfos(n: Int) {
        val response = alienApiService.getUfos(n.toString())
        if (response.code() == 404) { // more catching errors? TODO
            continuePolling = false
        } else {
//            Log.d("jhw", response.body().toString())
            response.body()?.let {
                Log.d("jhw $n", it.toString())
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
                delay(1000L)
            }
        }
    }



}