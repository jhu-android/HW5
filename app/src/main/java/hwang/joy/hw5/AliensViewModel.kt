package hwang.joy.hw5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*

class AliensViewModel: ViewModel() {

    private val alienAlerter = AlienAlerter(viewModelScope)
    val activeUfos = alienAlerter.alerts.map { ImmutableList(it.ufos) }
    val pointStore = pointStoreFlow()

    suspend fun startAlienReporting() {
        alienAlerter.startReporting()
    }

    private fun pointStoreFlow(): Flow<ImmutableMap<Int, ImmutableSet<LatLng>>> {
        val mutableStore = mutableMapOf<Int, MutableSet<LatLng>>()

        val alertStore =
            alienAlerter.alerts.runningFold(mutableListOf<UfoPosition>()) { accumulator, alert ->
                alert.ufos.forEach {
                    accumulator.add(it)
                }
                accumulator
            }

        val mutableStoreFlow =
            alertStore.map { alerts ->
                alerts.forEach { alert ->
                    if (mutableStore.containsKey(alert.ship)) {
                        mutableStore[alert.ship]?.add(LatLng(alert.lat, alert.lon))
                    } else {
                        mutableStore[alert.ship] = mutableSetOf(LatLng(alert.lat, alert.lon))
                    }
                }
                mutableStore
            }

        val mutableStoreListFlow =
            mutableStoreFlow.map { flowValue ->
                flowValue.mapValues { coordinates -> ImmutableSet(coordinates.value) }
            }

        return mutableStoreListFlow.map { ImmutableMap(it) }
    }
}

