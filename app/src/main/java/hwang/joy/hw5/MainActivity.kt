package hwang.joy.hw5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import hwang.joy.hw5.ui.theme.HW5Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val startingCameraPosition = LatLng(38.9835316367249, -77.12127685546875)

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
                }
            }
        }
    }
}

@Composable
fun Ui(
    viewModel: AliensViewModel,
) {
    val scope = rememberCoroutineScope()
    val activeUfos by viewModel.activeUfos.collectAsState(initial = emptyList())
    val historicalSightings by viewModel.historicalSightings.collectAsState(initial = mapOf())
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(startingCameraPosition, 20f)
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }
    var mapLoaded by remember { mutableStateOf(false) }

    if (historicalSightings.isNotEmpty()) {
        var bounds: LatLngBounds? = null
        val sightingsList = getPointsAsList(historicalSightings)
        sightingsList.forEach { point ->
            bounds = bounds?.including(point) ?: LatLngBounds(point, point)
        }
        bounds?.let { newBounds ->
            scope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(newBounds, 100), 1000)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(
            cameraPositionState = cameraPositionState,
            onMapLoaded = { mapLoaded = true },
            ufos = activeUfos,
            sightings = historicalSightings,
        )
        if (!mapLoaded) {
            AnimatedVisibility(
                visible = !mapLoaded,
                modifier = Modifier.fillMaxSize(),
                enter = EnterTransition.None,
                exit = fadeOut(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .wrapContentSize()
                )
            }
        } else {
            LaunchedEffect(Unit) {
                viewModel.startAlienReporting()
            }

        }
    }
}

@Composable
fun GoogleMapView(
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    ufos: List<UfoPositionLatlng>,
    sightings: Map<Int, ImmutableSet<UfoSighting>>,
) {
    Modifier.fillMaxWidth()

    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = true,
            rotationGesturesEnabled = true,
        )
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    val context = LocalContext.current
    var ufoStates by remember { mutableStateOf(emptyList<UfoMarker>()) }
    var ufoIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    LaunchedEffect(true) {
        withContext(Dispatchers.IO) {
            ufoIcon = context.loadBitmapDescriptor(R.drawable.ic_ufo_flying)
        }
    }

    LaunchedEffect(Unit) {
        delay(3000L)
        onMapLoaded()
    }

    LaunchedEffect(ufos) {
        ufoStates = emptyList()
        ufos.forEach { ufo ->
            ufoStates += UfoMarker(ufo.ship, MarkerState(ufo.latlng))
        }
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties,
        onMapLoaded = onMapLoaded,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ufoStates.forEach { ufoMarker ->
            MarkerInfoWindowContent(
                state = ufoMarker.state,
                icon = ufoIcon,
                anchor = Offset(0.5f, 0.5f),
                title = stringResource(R.string.ship_title, "${ufoMarker.ship}"),
                draggable = false,
            )
        }

        sightings.forEach { ufoSightings ->
            if (ufoSightings.value.size > 1) {
                val ufoLines = ufoSightings.value.map { it.place }
                Polyline(
                    points = ufoLines,
                    color = MaterialTheme.colors.primaryVariant,
                    width = 10f,
                )
            }
        }
    }
}
