package hwang.joy.hw5
import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

@Immutable
data class ImmutableList<T>(val list: List<T>): List<T> by list

@Immutable
data class ImmutableSet<T>(val set: Set<T>): Set<T> by set

@Immutable
data class ImmutableMap<K, V>(val map: Map<K, V>): Map<K, V> by map

fun <T> emptyImmutableList() = ImmutableList<T>(emptyList())

fun <T> emptyImmutableSet() = ImmutableSet<T>(emptySet())

data class UfoPosition(
    val ship: Int,
    val lat: Double,
    val lon: Double,
)

data class AlienAlert(
    val ufos: List<UfoPosition> = emptyList()
)

data class UfoLatLng(
    val ship: Int,
    val latlng: LatLng,
)

class UfoMarker(
    val ship: Int,
    val state: MarkerState,
)

class UfoLines(ufos: List<UfoPosition> = emptyList()) {
    private val _lines = mutableMapOf<Int, MutableList<Pair<Double, Double>>>()
    val lines: HashMap<Int, List<Pair<Double, Double>>>
        get()  = HashMap(_lines)

    fun addLines(ufos: List<UfoPosition>) {
        ufos.forEach {
            if (_lines.containsKey(it.ship)) {
                _lines[it.ship]?.add(Pair(it.lat, it.lon))
            } else {
                _lines[it.ship] = mutableListOf(Pair(it.lat, it.lon))
            }

        }
    }
}

//    fun initializeLines(ufos: List<UfoPosition>): MutableMap<Int, Pair<Double, Double>> {
//        var linesMap = mutableMapOf<Int, Pair<Double, Double>>()
//        ufos.forEach {
//            linesMap[it.ship] = Pair(it.lat, it.lon)
//        }
//        return linesMap
//    }



//class UfoLines(ufos: List<UfoPosition> = emptyList()) {
//}


//class UfoLines private constructor(ufos: List<UfoPosition> = emptyList()) {
//
//    val lines
//
//    companion object {
//        fun initializeLines(ufos: List<UfoPosition>): MutableMap<Int, Pair<Double, Double>> {
//            var linesMap = mutableMapOf<Int, Pair<Double, Double>>()
//            ufos.forEach {
//                linesMap[it.ship] = Pair(it.lat, it.lon)
//            }
//            return linesMap
//        }
//
//        fun create(ufos: List<UfoPosition>): UfoLines {
//            val lines = initializeLines(ufos)
//            val instance = UfoLines(ufos)
//
//        }
//    }
//}