package hwang.joy.hw5
import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(val list: List<T>): List<T> by list

data class UfoPosition(
    val ship: Int,
    val lat: Double,
    val lon: Double,
)

class AlienAlert(val aliens: List<UfoPosition> = emptyList())


//class AlienAlert(val ufos: List<UfoPosition> = emptyList())