@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.moufee.purduemenus.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.moufee.purduemenus.R
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.ui.theme.StationHeaderColor
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.Resource
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * The main screen: dining court tabs, meal selector, the menu item pager and the date picker bar.
 */
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    snackbarHostState: SnackbarHostState,
    onSettingsClicked: () -> Unit,
    onFeedbackClicked: () -> Unit,
) {
    val context = LocalContext.current
    val dayMenu by viewModel.dayMenu.collectAsState()
    val sortedLocations by viewModel.sortedLocations.collectAsState()
    val favoriteSet by viewModel.favoriteSet.collectAsState()
    val appPreferences by viewModel.appPreferences.collectAsState()
    val selectedMeal by viewModel.selectedMeal.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

    LaunchedEffect(viewModel) {
        viewModel.dayMenu.collect { result ->
            if (result is Resource.Error) {
                snackbarHostState.showSnackbar(context.getString(R.string.network_error_message))
            }
        }
    }

    val pagerState = rememberPagerState(pageCount = { sortedLocations.size })

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { MenuTopAppBar(onSettingsClicked, onFeedbackClicked) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (sortedLocations.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage.coerceIn(0, sortedLocations.lastIndex),
                    backgroundColor = MaterialTheme.colors.primarySurface,
                ) {
                    sortedLocations.forEachIndexed { index, diningCourt ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(diningCourt.tabTitle(favoriteSet, appPreferences.showFavoriteCounts)) },
                        )
                    }
                }
            }
            MealButtonRow(
                selectedMeal = selectedMeal,
                enabled = dayMenu.isSuccess,
                showLateLunch = dayMenu.asSuccess()?.data?.hasLateLunch == true,
                onMealSelected = viewModel::setSelectedMeal,
            )
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (dayMenu) {
                    is Resource.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is Resource.Success -> HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        key = { sortedLocations[it].diningCourtName },
                    ) { page ->
                        MenuItemListPage(viewModel, sortedLocations[page].diningCourtName)
                    }
                    is Resource.Error -> {}
                }
            }
            DatePickerBar(
                dateText = remember(currentDate) {
                    DateTimeHelper.getFriendlyDateFormat(currentDate, Locale.getDefault(), context)
                },
                onPreviousDay = viewModel::previousDay,
                onNextDay = viewModel::nextDay,
                onDateClicked = viewModel::currentDay,
            )
        }
    }
}

private fun DiningCourtMeal.tabTitle(favoriteItemIds: Set<String>, showFavoriteCount: Boolean): String {
    if (!showFavoriteCount || favoriteItemIds.isEmpty()) return diningCourtName
    val favoriteCount = stations
        .flatMap { station -> station.items.filter { it.id in favoriteItemIds } }
        .toSet()
        .size
    return "$diningCourtName ($favoriteCount)"
}

@Composable
private fun MenuTopAppBar(onSettingsClicked: () -> Unit, onFeedbackClicked: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onSettingsClicked()
                }) {
                    Text(stringResource(R.string.action_settings))
                }
                DropdownMenuItem(onClick = {
                    menuExpanded = false
                    onFeedbackClicked()
                }) {
                    Text(stringResource(R.string.action_send_feedback))
                }
            }
        },
    )
}

@Composable
private fun MealButtonRow(
    selectedMeal: String?,
    enabled: Boolean,
    showLateLunch: Boolean,
    onMealSelected: (String) -> Unit,
) {
    Surface(color = MaterialTheme.colors.background, elevation = 2.dp, modifier = Modifier.zIndex(1f)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        ) {
            MealButton(stringResource(R.string.breakfast), "Breakfast", selectedMeal, enabled,
                R.drawable.ic_coffee_cup, R.drawable.ic_coffee_outline, Modifier.weight(1f), onMealSelected)
            MealButton(stringResource(R.string.lunch), "Lunch", selectedMeal, enabled,
                R.drawable.ic_local_pizza_black_24px, R.drawable.ic_local_pizza_black_inverse_24px, Modifier.weight(1f), onMealSelected)
            if (showLateLunch) {
                MealButton(stringResource(R.string.late_lunch), "Late Lunch", selectedMeal, enabled,
                    R.drawable.ic_food_apple, R.drawable.ic_food_apple_inverse, Modifier.weight(1f), onMealSelected)
            }
            MealButton(stringResource(R.string.dinner), "Dinner", selectedMeal, enabled,
                R.drawable.ic_hamburger, R.drawable.ic_hamburger_inverse, Modifier.weight(1f), onMealSelected)
        }
    }
}

@Composable
private fun MealButton(
    label: String,
    mealName: String,
    selectedMeal: String?,
    enabled: Boolean,
    selectedIconRes: Int,
    unselectedIconRes: Int,
    modifier: Modifier = Modifier,
    onMealSelected: (String) -> Unit,
) {
    val selected = selectedMeal == mealName
    Column(
        modifier
            .clickable(enabled = enabled) { onMealSelected(mealName) }
            .alpha(if (enabled) 1f else 0.38f)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(if (selected) selectedIconRes else unselectedIconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(label, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun DatePickerBar(
    dateText: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateClicked: () -> Unit,
) {
    Surface(color = MaterialTheme.colors.primarySurface) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.width(50.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_keyboard_arrow_left_black_24dp),
                    contentDescription = stringResource(R.string.previous_day_button_description),
                )
            }
            Text(
                text = dateText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDateClicked)
                    .padding(vertical = 16.dp),
            )
            IconButton(onClick = onNextDay, modifier = Modifier.width(50.dp)) {
                Image(
                    painter = painterResource(R.drawable.ic_keyboard_arrow_right_black_24dp),
                    contentDescription = stringResource(R.string.next_day_button_description),
                )
            }
        }
    }
}

/**
 * The list of menu items for one meal at one dining court.
 */
@Composable
fun MenuItemListPage(viewModel: MenuViewModel, diningCourtName: String) {
    val uiState by remember(viewModel, diningCourtName) { viewModel.getMenuDetailsUiState(diningCourtName) }
        .collectAsState(initial = MenuViewModel.MealDetailUiState(emptyList(), "", null))
    val appPreferences by viewModel.appPreferences.collectAsState()

    Column(Modifier.fillMaxSize()) {
        if (appPreferences.showServingTimes) {
            Surface(color = MaterialTheme.colors.background, elevation = 1.dp) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(uiState.servingTimeText, textAlign = TextAlign.Center)
                }
            }
        }
        if (uiState.items.isNotEmpty()) {
            val columns = if (LocalConfiguration.current.screenWidthDp > 500) 2 else 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = uiState.items,
                    key = {
                        when (it) {
                            is HeaderItemViewObject -> "header-${it.stationName}"
                            is MenuItemViewObject -> it.id
                        }
                    },
                    span = {
                        if (it is HeaderItemViewObject) GridItemSpan(maxLineSpan) else GridItemSpan(1)
                    },
                ) { item ->
                    when (item) {
                        is HeaderItemViewObject -> StationHeader(item.stationName)
                        is MenuItemViewObject -> MenuItemRow(item) { viewModel.toggleFavorite(it.menuItem) }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val status = uiState.status
                Text(if (status != null && status != "Open") status else stringResource(R.string.no_data))
            }
        }
    }
}

@Composable
private fun StationHeader(stationName: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Divider()
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                text = stationName,
                color = StationHeaderColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}

@Composable
private fun MenuItemRow(item: MenuItemViewObject, onLongPressed: (MenuItemViewObject) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .combinedClickable(onClick = {}, onLongClick = { onLongPressed(item) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f, fill = false),
        )
        if (item.isVegetarian) {
            Image(
                painter = painterResource(R.drawable.ic_vegetarian_mark),
                contentDescription = stringResource(R.string.description_vegetarian_icon),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(14.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        if (item.isFavorite) {
            Image(
                painter = painterResource(R.drawable.ic_favorite_24dp),
                contentDescription = stringResource(R.string.description_favorite_button),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(14.dp),
            )
        }
    }
}
