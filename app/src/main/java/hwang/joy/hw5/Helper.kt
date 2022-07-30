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

data class UfoPosition(
    val ship: Int,
    val lat: Double,
    val lon: Double,
)

data class UfoPositionTime(
    val ship: Int,
    val lat: Double,
    val lon: Double,
    val time:  Long,
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