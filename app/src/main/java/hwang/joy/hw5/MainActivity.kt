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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val alertStore by viewModel.alertStore.collectAsState(initial = emptyList())
    Log.d("halp", "$alertStore")
    
    Log.d("mainActivity", "rendering UI")
    val scope = rememberCoroutineScope()

    val activeUfos by viewModel.activeUfos.collectAsState(initial = emptyList())
    val pointStoreMap by viewModel.pointStoreMap.collectAsState(initial = mapOf())
    val pointStoreList by viewModel.pointStoreList.collectAsState(initial = emptyList())

    Log.d("mainActivity", "$activeUfos | $pointStoreMap | $pointStoreList ")
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(LatLng(38.9835316367249, -77.12127685546875), 20f)
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }
    var mapLoaded by remember { mutableStateOf(false) }

    if (pointStoreList.isNotEmpty()) {
        var bounds: LatLngBounds? = null
        pointStoreList.forEach { point ->
            bounds = bounds?.including(point) ?: LatLngBounds(point, point)
        }
        bounds?.let { newBounds ->
            scope.launch {
                Log.d("mainActivity", "moving camera")
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(newBounds, 100), 1000)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(
            cameraPositionState = cameraPositionState,
            onMapLoaded = { mapLoaded = true },
            modifier = Modifier.fillMaxSize(),
            ufos = activeUfos,
            lines = pointStoreMap,
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
                Log.d("mainActivity", "---------- start alien reporting")
                viewModel.startAlienReporting()
            }

        }
    }
}

@Composable
fun GoogleMapView(
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    modifier: Modifier,
    ufos: List<UfoLatLng>,
    lines: Map<Int, ImmutableSet<Pair<Long, LatLng>>>,
) {
    Log.d("mainActivity", "rendering GoogleMapView")

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

    Log.d("boop > > active", "${ufos.size}")
    Log.d("boop * * states", "${ufoStates.size}")


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
        Log.d("mainActivity", "********* GoogleMap *************")

        ufoStates.forEach { ufoMarker ->
            Log.d("mainActivity", "marker!")
            MarkerInfoWindowContent(
                state = ufoMarker.state,
                icon = ufoIcon,
                anchor = Offset(0.5f, 0.5f),
                title = "Ship ${ufoMarker.ship}",
                draggable = false,
            )
        }

        lines.forEach { ufo ->
            if (ufo.value.size > 1) {
                val ufoLines = ufo.value.map { it.second }
                Polyline(
                    points = ufoLines,
                    color = MaterialTheme.colors.primaryVariant,
                    width = 10f,
                )
            }
        }
    }
}
