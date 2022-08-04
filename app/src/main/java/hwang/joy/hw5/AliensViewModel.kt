package hwang.joy.hw5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*

class AliensViewModel: ViewModel() {
    private val startTime =  System.currentTimeMillis()
    private val elapsedTime: () -> Long = {  System.currentTimeMillis() - startTime }
    private val alienAlerter = AlienAlerter(viewModelScope)
    private val alertStore =
        alienAlerter.alerts.runningFold(mutableListOf<UfoPositionTimestamped>()) { accumulator, alert ->
            alert.ufos.forEach {
                accumulator.add(UfoPositionTimestamped(it.ship, it.lat, it.lon, elapsedTime()))
            }
            accumulator
        }

    val activeUfos = alienAlerter.alerts.map { alert ->
        val mutableList = mutableListOf<UfoPositionLatlng>()
        alert.ufos.forEach { ufoPosition ->
            mutableList.add(UfoPositionLatlng(ufoPosition.ship, LatLng(ufoPosition.lat, ufoPosition.lon)))
        }
        mutableList.toList()
    }
    val historicalSightings = getHistoricalSightingsFlow()

    suspend fun startAlienReporting() {
        alienAlerter.startReporting()
    }

    private fun getHistoricalSightingsFlow(): Flow<ImmutableMap<Int, ImmutableSet<UfoSighting>>> {
        val mutableStore = mutableMapOf<Int, MutableSet<UfoSighting>>()
        val mutableStoreFlow =
            alertStore.map { alerts ->
                alerts.forEach { alert ->
                    if (mutableStore.containsKey(alert.ship)) {
                        mutableStore[alert.ship]?.add(UfoSighting(alert.time, LatLng(alert.lat, alert.lon)))
                    } else {
                        mutableStore[alert.ship] = mutableSetOf(UfoSighting(alert.time, LatLng(alert.lat, alert.lon)))
                    }
                }
                mutableStore
            }
        val mutableStoreSetFlow =
            mutableStoreFlow.map { flowValue ->
                flowValue.mapValues { coordinates -> ImmutableSet(coordinates.value) }
            }
        return mutableStoreSetFlow.map { ImmutableMap(it) }
    }
}
