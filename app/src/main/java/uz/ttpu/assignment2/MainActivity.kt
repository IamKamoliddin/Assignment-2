package uz.ttpu.assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.ttpu.assignment2.ui.theme.Assignment2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StackDemo(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun Modifier.logPhases(name: String): Modifier = this
    .then(
        object : LayoutModifier {
            override fun MeasureScope.measure(
                measurable: Measurable,
                constraints: Constraints
            ): MeasureResult {
                Log.d("Phases", "$name: layout/measure")
                val placeable = measurable.measure(constraints)
                return layout(placeable.width, placeable.height) {
                    Log.d("Phases", "$name: layout/place")
                    placeable.placeRelative(0, 0)
                }
            }
        }
    )
    .drawBehind {
        Log.d("Phases", "$name: draw")
    }

@Composable
fun PaddingDemo(modifier: Modifier = Modifier) {
    var padding by remember { mutableStateOf(8.dp) }
    SideEffect { Log.d("Phases", "PaddingDemo: composition") }

    Column(modifier = modifier) {
        Button(onClick = { padding = if (padding == 8.dp) 32.dp else 8.dp }) {
            Text("Toggle padding")
        }
        Text(
            text = "Padding demo",
            modifier = Modifier
                .padding(padding)
                .logPhases("padding demo"),
        )
    }
}

@Composable
fun OffsetDemo(modifier: Modifier = Modifier) {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemo: composition") }
    val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }

    Column(modifier = modifier) {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetPx, 0) }
                .logPhases("offset demo"),
        )
    }
}

@Composable
fun ColorDemo(modifier: Modifier = Modifier) {
    var color by remember { mutableStateOf(Color.Red) }
    SideEffect { Log.d("Phases", "ColorDemo: composition") }

    Column(modifier = modifier) {
        Button(onClick = { color = if (color == Color.Red) Color.Blue else Color.Red }) {
            Text("Change color")
        }
        Canvas(modifier = Modifier.size(120.dp).logPhases("color demo")) {
            drawRect(color)
        }
    }
}

@Composable
fun ParallaxSlow(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Parallax", "slow: composition #${++compositions[0]}") }

    Box(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .offset(
                    y = with(LocalDensity.current) {
                        (listState.firstVisibleItemScrollOffset / 2).toDp()
                    }
                )
        )
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(100) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

@Composable
fun ParallaxFast(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Parallax", "fast: composition #${++compositions[0]}") }

    Box(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .offset {
                    IntOffset(x = 0, y = listState.firstVisibleItemScrollOffset / 2)
                }
        )
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(100) { index ->
                Text("Item $index", Modifier.fillMaxWidth().padding(16.dp))
            }
        }
    }
}

@Composable
fun CyclicDemo(modifier: Modifier = Modifier) {
    var imageHeightPx by remember { mutableIntStateOf(0) }
    val heightInThisComposition = imageHeightPx
    val compositions = remember { intArrayOf(0) }
    SideEffect {
        Log.d("Cycle", "composition #${++compositions[0]}: " +
            "imageHeightPx=$heightInThisComposition")
    }

    Box(modifier = modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFF6650A4))
                .onSizeChanged { size ->
                    Log.d("Cycle", "onSizeChanged: height=${size.height}")
                    imageHeightPx = size.height
                }
        )
        Text(
            text = "I'm below the image",
            modifier = Modifier.padding(
                top = with(LocalDensity.current) { heightInThisComposition.toDp() }
            ),
        )
    }
}

@Composable
fun CyclicFixed(modifier: Modifier = Modifier) {
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Cycle", "fixed: composition #${++compositions[0]}") }

    Column(modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}

@Composable
fun StackBelow(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->

        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            Log.d("StackBelow", "measure child")
            measurable.measure(loose)
        }

        val spacingPx = spacing.roundToPx()
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height } + if (placeables.isNotEmpty()) (placeables.size - 1) * spacingPx else 0

        layout(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
            Log.d("StackBelow", "place children")
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height + spacingPx
            }
        }
    }
}
@Composable
fun StackDemo(modifier: Modifier = Modifier) {
    val compositions = remember { intArrayOf(0) }
    SideEffect { Log.d("Cycle", "stack: composition #${++compositions[0]}") }

    StackBelow(modifier.fillMaxWidth(), spacing = 8.dp) {
        Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)))
        Text("I'm below the image")
    }
}

@Preview(showBackground = true)
@Composable
fun StackDemoPreview() {
    Assignment2Theme {
        StackDemo()
    }
}







