package hwang.joy.hw5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.*

class AliensViewModel: ViewModel() {

    private val alienAlerter = AlienAlerter(viewModelScope)


    suspend fun startAlienReporting() {
        alienAlerter.startReporting()
    }

    val activeUfos = alienAlerter.alerts.map { ImmutableList(it.ufos) }

    // Create Point Store
    private val mutableStore = mutableMapOf<Int, MutableSet<LatLng>>()

    private val alertStore =
        alienAlerter.alerts.runningFold(mutableListOf<UfoPosition>()) { accumulator, alert ->
            alert.ufos.forEach {
                accumulator.add(it)
            }
            accumulator
        }


    private val mutableStoreFlow = alertStore.map { alerts ->
        alerts.forEach { alert ->
            if (mutableStore.containsKey(alert.ship)) {
                mutableStore[alert.ship]?.add(LatLng(alert.lat, alert.lon))
            } else {
                mutableStore[alert.ship] = mutableSetOf(LatLng(alert.lat, alert.lon))
            }
        }
        mutableStore
    }

    private val mutableStoreListFlow =
        mutableStoreFlow.map { flowValue -> flowValue.mapValues { coordinates -> ImmutableSet(coordinates.value) } }

    val pointStore = mutableStoreListFlow.map { ImmutableMap(it) }
}



//    private val _pointStore = MutableStateFlow(generatePointStore())
//    val pointStore: Flow<Map<Int, ImmutableList<LatLng>>>
//        get() = _pointStore
//
//    private fun generatePointStore(): Map<Int, ImmutableList<LatLng>> {
//        Log.d("viewModel", "generatePointStore called")
//        val mutableStore = mutableMapOf<Int, MutableList<LatLng>>()
//
//        alertStore.map { alerts ->
//            alerts.forEach { alert ->
//                if (mutableStore.containsKey(alert.ship)) {
//                    mutableStore[alert.ship]?.add(LatLng(alert.lat, alert.lon))
//                } else {
//                    mutableStore[alert.ship] = mutableListOf(LatLng(alert.lat, alert.lon))
//                }
//            }
//        }
//
//        val immutableCoordinates = mutableStore.mapValues { ImmutableList(it.value) }
//        return immutableCoordinates.toMap()
//    }
//}