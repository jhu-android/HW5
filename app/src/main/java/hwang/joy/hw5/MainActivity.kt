package hwang.joy.hw5

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import hwang.joy.hw5.ui.theme.HW5Theme
import kotlinx.coroutines.delay

private val googleHQ = LatLng(37.42423291057923, -122.08811454627153)
private val defaultCameraPosition = CameraPosition.fromLatLngZoom(googleHQ, 11f)

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AliensViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW5Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Ui(viewModel)

//                    var mapLoaded by remember { mutableStateOf(false) }
//
//                    val cameraPositionState = rememberCameraPositionState {
//                        position = defaultCameraPosition
//                    }
//
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        GoogleMapView(
//                            cameraPositionState = cameraPositionState,
//                            onMapLoaded = { mapLoaded = true },
//                            modifier = Modifier.fillMaxSize(),
//                            place = googleHQ,
//                        )
//                        if (!mapLoaded) {
//                            AnimatedVisibility(
//                                visible = !mapLoaded,
//                                modifier = Modifier.fillMaxSize(),
//                                enter = EnterTransition.None,
//                                exit = fadeOut(),
//                            ) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier
//                                        .background(MaterialTheme.colors.background)
//                                        .wrapContentSize()
//                                )
//                            }
//                        }
//                    }
                }
            }
        }
    }
}

@Composable
fun Ui(
    viewModel: AliensViewModel,
) {
    val activeUfos by viewModel.activeUfos.collectAsState(initial = emptyList())
    val pointStore by viewModel.pointStore.collectAsState(initial = mapOf())

    Log.d("cyrano activeUfos", "$activeUfos")
    Log.d("cyrano pointStore", "$pointStore")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text="ALIENS!!!")

        LaunchedEffect(Unit) {
            Log.d("jhw", "launched effect")
            viewModel.startAlienReporting()
        }

        activeUfos.forEach {
            Text(text="$it")
        }

    }
}

@Composable
fun GoogleMapView(
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    place: LatLng,
    modifier: Modifier,
) {

    val placeState = rememberMarkerState(position = place)

    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = true,
            rotationGesturesEnabled = true,
        )
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    LaunchedEffect(Unit) {
        delay(3000L)
        onMapLoaded()
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties,
        onMapLoaded = onMapLoaded,
        modifier = Modifier,
    ) {
        MarkerInfoWindowContent(
            state = placeState,
            title = "MARKER yo",
            draggable = true,
        )
        Polyline(
            points = listOf(LatLng(37.384374, -122.068712), LatLng(37.42423291057923, -122.08811454627153)),
            color = Color.Red,
            width = 8f,
        )
        Polyline(
            points = listOf(LatLng(37.384584, -122.132241), LatLng(37.42423291057923, -122.08811454627153)),
            color = Color.Blue,
            width = 8f,
        )




    }
    

}
