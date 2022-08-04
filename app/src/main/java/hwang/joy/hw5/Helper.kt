package hwang.joy.hw5
import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

// Attribution Scott Stanchfield, Android Summer 2022
// https://gitlab.com/605-686/android-summer-2022/-/raw/main/Movies4/app/src/main/java/com/javadude/movies4/Common.kt
@Immutable
data class ImmutableList<T>(val list: List<T>): List<T> by list

@Immutable
data class ImmutableSet<T>(val set: Set<T>): Set<T> by set

@Immutable
data class ImmutableMap<K, V>(val map: Map<K, V>): Map<K, V> by map

fun <T> emptyImmutableList() = ImmutableList<T>(emptyList())
// End Attribution

data class UfoPosition(
    val ship: Int,
    val lat: Double,
    val lon: Double,
)

data class AlienAlert(
    val ufos: List<UfoPosition> = emptyList()
)

data class UfoPositionTimestamped(
    val ship: Int,
    val lat: Double,
    val lon: Double,
    val time:  Long,
)

data class UfoPositionLatlng(
    val ship: Int,
    val latlng: LatLng,
)

data class UfoSighting(
    val time: Long,
    val place: LatLng,
)

data class UfoMarker(
    val ship: Int,
    val state: MarkerState,
)

fun getPointsAsList(pointMap: Map<Int, ImmutableSet<UfoSighting>>): List<LatLng> {
    val mutableList = mutableListOf<LatLng>()
    pointMap.values.forEach { ufoSightings ->
        ufoSightings.forEach { sighting ->
            mutableList.add(sighting.place)
        }
    }
    return mutableList.toList()
}
