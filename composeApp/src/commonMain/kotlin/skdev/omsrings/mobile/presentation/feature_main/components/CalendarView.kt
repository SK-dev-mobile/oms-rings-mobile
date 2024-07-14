package skdev.omsrings.mobile.presentation.feature_main.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.day_of_week_friday
import omsringsmobile.composeapp.generated.resources.day_of_week_monday
import omsringsmobile.composeapp.generated.resources.day_of_week_saturday
import omsringsmobile.composeapp.generated.resources.day_of_week_sunday
import omsringsmobile.composeapp.generated.resources.day_of_week_thursday
import omsringsmobile.composeapp.generated.resources.day_of_week_tuesday
import omsringsmobile.composeapp.generated.resources.day_of_week_wednesday
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_main.components.CalendarState.Companion.MAX_MONTH_BUFFER
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.CustomTheme
import skdev.omsrings.mobile.ui.theme.values.AnimationSpec
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.ui.theme.values.IconSize
import skdev.omsrings.mobile.utils.datetime.DateTimePattern
import skdev.omsrings.mobile.utils.datetime.format

@Composable
fun rememberCalendarState(
    initialDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    initialStartDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    onMonthChanged: (YearMonth) -> Unit = {}
): CalendarState = remember {
    CalendarState(initialDate, initialStartDayOfWeek, onMonthChanged)
}

@Immutable
data class YearMonth(val month: Month, val year: Int) {
    private var localDate: LocalDate = LocalDate(year, month, 1)

    fun format(pattern: DateTimePattern = DateTimePattern.CALENDAR_MONTH_YEAR): String {
        require(pattern == DateTimePattern.CALENDAR_MONTH_YEAR)
        return localDate.format(pattern)
    }

    val firstDayOfMonth: LocalDate
        get() = localDate

    fun plus(value: Int, unit: DateTimeUnit.DateBased): YearMonth {
        return localDate.plus(value, unit).yearMonth
    }

    fun minus(value: Int, unit: DateTimeUnit.DateBased): YearMonth {
        return localDate.minus(value, unit).yearMonth
    }
}

val LocalDate.yearMonth: YearMonth
    get() = YearMonth(month, year)

fun DayOfWeek.getStringResource(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.day_of_week_monday
    DayOfWeek.TUESDAY -> Res.string.day_of_week_tuesday
    DayOfWeek.WEDNESDAY -> Res.string.day_of_week_wednesday
    DayOfWeek.THURSDAY -> Res.string.day_of_week_thursday
    DayOfWeek.FRIDAY -> Res.string.day_of_week_friday
    DayOfWeek.SATURDAY -> Res.string.day_of_week_saturday
    DayOfWeek.SUNDAY -> Res.string.day_of_week_sunday
    else -> Res.string.day_of_week_sunday
}


@Immutable
data class DateAdditionalInfo(
    val edited: Boolean,
    val locked: Boolean,
)


@Stable
class CalendarState(
    initialDate: LocalDate,
    initialStartDayOfWeek: DayOfWeek,
    private val onMonthChanged: (YearMonth) -> Unit
) {
    private val _currentMonth = mutableStateOf(YearMonth(initialDate.month, initialDate.year))
    val currentMonth: State<YearMonth> = _currentMonth

    private val _selectedDate = mutableStateOf<LocalDate?>(null)
    val selectedDate: State<LocalDate?> = _selectedDate

    private val _startDayOfWeek = mutableStateOf(initialStartDayOfWeek)
    val startDayOfWeek: State<DayOfWeek> = _startDayOfWeek

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        updateCurrentMonth(date.yearMonth)
    }

    fun updateCurrentMonth(yearMonth: YearMonth) {
        if (_currentMonth.value != yearMonth) {
            _currentMonth.value = yearMonth
            onMonthChanged(yearMonth)
        }
    }

    fun updateStartDayOfWeek(dayOfWeek: DayOfWeek) {
        _startDayOfWeek.value = dayOfWeek
    }

    companion object {
        const val MAX_MONTH_BUFFER = 500
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    state: CalendarState,
    showWeekBar: Boolean = true,
    showMonthBar: Boolean = true,
    updating: Boolean = false,
    dateAddionalInfo: ((LocalDate) -> DateAdditionalInfo)? = null,
) {
    val initialPage = CalendarState.MAX_MONTH_BUFFER
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { MAX_MONTH_BUFFER * 2 + 1 }
    )

    val currentDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.settledPage) {
        scope.launch {
            state.updateCurrentMonth(
                currentDate.yearMonth.plus(pagerState.settledPage - initialPage, DateTimeUnit.MONTH)
            )
        }
    }

    Column {
        if (showMonthBar) {
            MonthBarView(
                modifier = Modifier.fillMaxWidth(),
                month = state.currentMonth.value,
                onNextClicked = {
                    scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    }
                },
                onPreviousClicked ={
                    scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage - 1)
                    }
                },
            )
        }

        if (updating) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline,
            )
        }

        Column(
            modifier = modifier
        ) {
            if (showWeekBar) {
                WeekBarView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.spaceMedium),
                    startDayOfWeek = state.startDayOfWeek.value
                )
                HorizontalDivider()
            }

            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
                key = { it },
                beyondBoundsPageCount = 2,
            ) { page ->
                val month = remember(page) {
                    currentDate.yearMonth.plus(page - initialPage, DateTimeUnit.MONTH)
                }

                val startDay = remember(month, state.startDayOfWeek.value) {
                    val firstDayOfMonth = month.firstDayOfMonth
                    val offset = (firstDayOfMonth.dayOfWeek.ordinal - state.startDayOfWeek.value.ordinal + 7) % 7
                    firstDayOfMonth.minus(offset, DateTimeUnit.DAY)
                }

                CalendarMonthView(
                    modifier = Modifier.fillMaxWidth(),
                    month = month,
                    startDay = startDay,
                    currentDate = currentDate,
                    selectedDate = state.selectedDate.value,
                    onDateClick = state::selectDate,
                    dateAddionalInfo = dateAddionalInfo,
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthView(
    modifier: Modifier = Modifier,
    month: YearMonth,
    startDay: LocalDate,
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    dateAddionalInfo: ((LocalDate) -> DateAdditionalInfo)?,
) {

    val itemModifier: Modifier = remember {
        Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceExtraSmall)
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        contentPadding = PaddingValues(Dimens.spaceMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceExtraSmall),
    ) {
        items(35) { dayOffset ->
            val date = startDay.plus(dayOffset, DateTimeUnit.DAY)
            val addionalInfo = remember(date) { if (dateAddionalInfo != null) dateAddionalInfo(date) else DateAdditionalInfo(false, false) }
            DayView(
                modifier = Modifier.fillMaxWidth(),
                itemModifier = itemModifier,
                date = date,
                enabled = date.month == month.month,
                selected = date == selectedDate,
                currentDate = date == currentDate,
                locked = addionalInfo.locked,
                edited = addionalInfo.edited,
                onClick = { onDateClick(date) },
            )
        }
    }
}

@Composable
private fun WeekBarView(
    modifier: Modifier = Modifier,
    startDayOfWeek: DayOfWeek
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceExtraSmall)
    ) {
        for (i in 0..6) {
            val dayOfWeek = remember(startDayOfWeek, i) {
                val ordinal = (startDayOfWeek.ordinal + i) % 7
                DayOfWeek.values().get(ordinal)
            }
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = stringResource(dayOfWeek.getStringResource()),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun MonthBarView(
    modifier: Modifier = Modifier,
    month: YearMonth,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
) {
    val formattedMonth: String by remember(month) {
        derivedStateOf {
            month.format()
        }
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.spaceMedium, vertical = Dimens.spaceExtraSmall)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousClicked
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = null
                )
            }

            AnimatedContent(formattedMonth) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
            }


            IconButton(
                onClick = onNextClicked
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun DayView(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = remember {
        Modifier
            .fillMaxWidth()
            .padding(Dimens.spaceExtraSmall)
    },
    date: LocalDate,
    enabled: Boolean,
    selected: Boolean,
    currentDate: Boolean,
    locked: Boolean,
    edited: Boolean,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (currentDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.outline
        ),
        border = if (selected) BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary
        ) else null,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = itemModifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (currentDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            Spacer(Dimens.spaceExtraSmall)

            Row(
                modifier = Modifier.height(IconSize.ExtraSmall),
            ) {
                if (locked) {
                    Icon(
                        modifier = Modifier.size(IconSize.ExtraSmall),
                        imageVector = Icons.Rounded.Lock,
                        tint = CustomTheme.colors.error,
                        contentDescription = null
                    )
                }

                Spacer(Dimens.spaceExtraSmall)

                if (edited) {
                    Icon(
                        modifier = Modifier.size(IconSize.ExtraSmall),
                        imageVector = Icons.Rounded.Edit,
                        tint = CustomTheme.colors.warning,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
