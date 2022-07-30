package hwang.joy.hw5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*

class AliensViewModel: ViewModel() {

    private val alienAlerter = AlienAlerter(viewModelScope)
    private val alertStore =
        alienAlerter.alerts.runningFold(mutableListOf<UfoPosition>()) { accumulator, alert ->
            alert.ufos.forEach {
                accumulator.add(it)
            }
            accumulator
        }

    val activeUfos = alienAlerter.alerts.map { alert ->
        val mutableList = mutableListOf<UfoLatLng>()
        alert.ufos.forEach { ufoPosition ->
            mutableList.add(UfoLatLng(ufoPosition.ship, LatLng(ufoPosition.lat, ufoPosition.lon)))
        }
        mutableList.toList()
    }

    val pointStoreMap = pointStoreMapFlow()
    val pointStoreList = pointStoreListFlow()

    suspend fun startAlienReporting() {
        alienAlerter.startReporting()
    }

    private fun activeUfoFlow(): Flow<List<UfoLatLng>> {
        val mutableListFlow = alienAlerter.alerts.map { alert ->
            val mutableList = mutableListOf<UfoLatLng>()
            alert.ufos.forEach { ufoPosition ->
                mutableList.add(UfoLatLng(ufoPosition.ship, LatLng(ufoPosition.lat, ufoPosition.lon)))
            }
            mutableList.toList()
        }
        return mutableListFlow
    }

    private fun pointStoreListFlow(): Flow<List<LatLng>> {
        val mutableList = mutableListOf<LatLng>()
        val mutableListFlow = alertStore.map { ufoPositions ->
            ufoPositions.forEach { ufoPosition ->
                mutableList.add(LatLng(ufoPosition.lat, ufoPosition.lon))
            }
            mutableList.toList()
        }
        return mutableListFlow
    }

    private fun pointStoreMapFlow(): Flow<ImmutableMap<Int, ImmutableSet<LatLng>>> {
        val mutableStore = mutableMapOf<Int, MutableSet<LatLng>>()
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
        val mutableStoreSetFlow =
            mutableStoreFlow.map { flowValue ->
                flowValue.mapValues { coordinates -> ImmutableSet(coordinates.value) }
            }
        return mutableStoreSetFlow.map { ImmutableMap(it) }
    }
}
