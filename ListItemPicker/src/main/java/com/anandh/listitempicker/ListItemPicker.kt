package com.anandh.listitempicker

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest


@Composable
fun <T> ListItemPicker(
    modifier: Modifier,
    itemFormatter: ((T) -> String)? = null,
    selectedItem: T,
    onSelectionChange: (T) -> Unit,
    separatorColor: Color = Color.Gray,
    items: List<T>,
    itemTextStyle: TextStyle,
    itemVerticalPadding: Dp = 8.dp,
    separatorThickness: Dp = 1.dp,
    enableSound: Boolean = true,
    enableHaptic: Boolean = true
) {
    val labelOrDefault = itemFormatter ?: { it: T -> it.toString() }
    val textStyleOrDefault = itemTextStyle ?: TextStyle.Default
    ListPicker(
        modifier = modifier,
        initialSelectedItem = selectedItem,
        itemList = items,
        label = labelOrDefault,
        onItemChange = onSelectionChange,
        textStyle = textStyleOrDefault,
        dividersColor = separatorColor,
        verticalPadding = itemVerticalPadding,
        dividerThickness = separatorThickness,
        enableSound = enableSound,
        enableHaptic = enableHaptic
    )
}

fun calculateIntervals(coercedOutOfBoundsPageCount: Int, listSize: Int): List<Int> {
    return listOf(
        0,
        coercedOutOfBoundsPageCount,
        coercedOutOfBoundsPageCount + 1 * listSize,
        coercedOutOfBoundsPageCount + 1 * listSize + coercedOutOfBoundsPageCount,
    )
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun <T> ListPicker(
    modifier: Modifier,
    initialSelectedItem: T,
    itemList: List<T>,
    label: T.() -> String = { toString() },
    onItemChange: (T) -> Unit,
    outOfBoundsPageCount: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current,
    verticalPadding: Dp = 8.dp,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    dividerThickness: Dp = 1.dp,
    enableSound: Boolean,
    enableHaptic: Boolean
) {

    val listSize = itemList.size
    val coercedOutOfBoundsPageCount = outOfBoundsPageCount.coerceIn(0..listSize / 2)
    val visibleItemsCount = 1 + coercedOutOfBoundsPageCount * 2

    // Define intervals for items
    val intervals = calculateIntervals(coercedOutOfBoundsPageCount, listSize)

    // Using lazy list state to manage scrolling
    val initialIndex = itemList.indexOf(initialSelectedItem).coerceIn(0, itemList.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // Add these near the start of the function
    val context = LocalContext.current
    val view = LocalView.current
    val mediaPlayer = remember { createMediaPlayer(context) }

    // Cleanup media player when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    // Side effect to manage initial scroll position
    LaunchedEffect(initialIndex) {
        listState.scrollToItem(initialIndex, 0)
    }
    // Observe the first visible item index and trigger onValueChange
    LaunchedEffect(key1 = itemList) {
        var previousIndex = listState.firstVisibleItemIndex
        snapshotFlow { listState.firstVisibleItemIndex }.collectLatest { currentIndex ->
            // Play tick sound when scroll settles
            if (previousIndex != currentIndex) {
                if (enableSound) {
                    mediaPlayer?.apply {
                        seekTo(0)
                        start()
                    }
                }
                if (enableHaptic) {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
                onItemChange(itemList[currentIndex % listSize])
                previousIndex = currentIndex
            }
        }
    }

    // Calculate heights with consideration for lineHeight units
    val itemHeight = if (textStyle.lineHeight.isSp) {
        textStyle.lineHeight.toDp
    } else {
        20.dp//handle this case accordingly
    } + verticalPadding * 2
    ComposeScope {
        AnimatedContent(
            modifier = modifier.fillMaxWidth(),
            targetState = remember { derivedStateOf { listState.firstVisibleItemIndex } },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "AnimatedContent",
        ) {
            LazyColumn(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(
                    lazyListState = listState, snapPosition = SnapPosition.Center
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(vertical = itemHeight / 4),
                modifier = modifier
                    .height(itemHeight * visibleItemsCount)
                    .fadingEdge(
                        brush = remember {
                            Brush.verticalGradient(
                                0F to Color.Transparent,
                                0.5F to textStyle.color,
                                1F to Color.Transparent
                            )
                        },
                    ),
            ) {
                items(count = intervals.last(), key = { it }) { index ->
                    val textModifier = Modifier
                        .padding(horizontal = 16.dp, vertical = verticalPadding)
                        .fillMaxWidth()

                    when (index) {
                        in intervals[0]..<intervals[1], in intervals[2]..<intervals[3] -> {
                            Text(
                                text = "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle,
                                modifier = textModifier,
                                textAlign = TextAlign.Center
                            )
                        }

                        in intervals[1]..<intervals[2] -> {
                            Text(
                                text = itemList[(index - coercedOutOfBoundsPageCount) % listSize].label(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle,
                                modifier = textModifier,  // Use same modifier as empty text
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Use transparent color if dividersColor is white
            val effectiveDividerColor =
                if (dividersColor == Color.White) Color.Transparent else dividersColor
            // Add separators
            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(
                        y = (itemHeight * coercedOutOfBoundsPageCount) + (verticalPadding / 2) - (dividerThickness / 2)
                    ), thickness = dividerThickness, color = effectiveDividerColor
            )

            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(
                        y = (itemHeight * (coercedOutOfBoundsPageCount + 1)) + (verticalPadding / 2) - (dividerThickness / 2)
                    ), thickness = dividerThickness, color = effectiveDividerColor
            )
        }
    }
}

private fun createMediaPlayer(context: Context): MediaPlayer? {
    return try {
        MediaPlayer.create(context, R.raw.scroll_sound)?.apply {
            setVolume(0.5f, 0.5f)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ComposeScope(content: @Composable () -> Unit) {
    content()
}

val TextUnit.toDp: Dp @Composable get() = LocalDensity.current.toDp(this)
fun Density.toDp(sp: TextUnit): Dp = sp.toDp()

@Stable
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }