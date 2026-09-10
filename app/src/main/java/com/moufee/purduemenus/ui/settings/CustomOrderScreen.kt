package com.moufee.purduemenus.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.data.menus.Location

private val LocationRowHeight = 56.dp

/**
 * Lets the user reorder dining courts via long-press drag and toggle their visibility.
 */
@Composable
fun CustomOrderScreen(viewModel: LocationSettingsViewModel) {
    val liveLocations by viewModel.locations.observeAsState()
    LaunchedEffect(liveLocations) {
        liveLocations?.let { viewModel.initializeOnce(it) }
    }

    val locations = viewModel.orderedLocations
    var draggedLocationId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val rowHeightPx = with(LocalDensity.current) { LocationRowHeight.toPx() }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.title_activity_custom_order)) }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            locations.forEach { location ->
                key(location.LocationId) {
                    val isDragged = location.LocationId == draggedLocationId
                    LocationRow(
                        location = location,
                        onToggleVisibility = { viewModel.toggleVisibility(location) },
                        modifier = Modifier
                            .zIndex(if (isDragged) 1f else 0f)
                            .graphicsLayer {
                                if (isDragged) {
                                    translationY = dragOffset
                                    shadowElevation = 10f
                                }
                            }
                            .pointerInput(location.LocationId) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedLocationId = location.LocationId
                                        dragOffset = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount.y
                                        val fromIndex = locations.indexOfFirst { it.LocationId == draggedLocationId }
                                        if (fromIndex < 0) return@detectDragGesturesAfterLongPress
                                        if (dragOffset > rowHeightPx / 2 && fromIndex < locations.lastIndex) {
                                            viewModel.moveLocation(fromIndex, fromIndex + 1)
                                            dragOffset -= rowHeightPx
                                        } else if (dragOffset < -rowHeightPx / 2 && fromIndex > 0) {
                                            viewModel.moveLocation(fromIndex, fromIndex - 1)
                                            dragOffset += rowHeightPx
                                        }
                                    },
                                    onDragEnd = {
                                        draggedLocationId = null
                                        dragOffset = 0f
                                        viewModel.saveOrder()
                                    },
                                    onDragCancel = {
                                        draggedLocationId = null
                                        dragOffset = 0f
                                        viewModel.saveOrder()
                                    },
                                )
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationRow(
    location: Location,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(LocationRowHeight)
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = location.FormalName,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .weight(1f),
        )
        IconButton(onClick = onToggleVisibility, modifier = Modifier.padding(end = 16.dp)) {
            Image(
                painter = painterResource(if (location.isHidden) R.drawable.ic_not_visible_24dp else R.drawable.ic_visible_24dp),
                contentDescription = stringResource(R.string.desc_toggle_visibility),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}
