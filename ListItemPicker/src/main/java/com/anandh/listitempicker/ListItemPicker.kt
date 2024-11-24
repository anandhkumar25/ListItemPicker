package com.anandh.listitempicker

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest


@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    ListPicker(
        modifier = modifier.fillMaxWidth(),
        initialValue = value,
        itemList = list,
        label = label,
        onValueChange = onValueChange,
        textStyle = textStyle,
        dividersColor = dividersColor
    )
}


@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun <T> ListPicker(
    initialValue: T,
    itemList: List<T>,
    modifier: Modifier,
    label: T.() -> String = { toString() },
    onValueChange: (T) -> Unit,
    outOfBoundsPageCount: Int = 1,
    textStyle: TextStyle = LocalTextStyle.current,
    verticalPadding: Dp = 8.dp,
    dividersColor: Color = MaterialTheme.colorScheme.primary,
    dividerThickness: Dp = 1.dp
) {

    val listSize = itemList.size
    val coercedOutOfBoundsPageCount = outOfBoundsPageCount.coerceIn(0..listSize / 2)
    val visibleItemsCount = 1 + coercedOutOfBoundsPageCount * 2

    // Define intervals for items
    val intervals =
        remember(key1 = coercedOutOfBoundsPageCount, key2 = 1, key3 = listSize) {
            listOf(
                0,
                coercedOutOfBoundsPageCount,
                coercedOutOfBoundsPageCount + 1 * listSize,
                coercedOutOfBoundsPageCount + 1 * listSize + coercedOutOfBoundsPageCount,
            )
        }

    // Using lazy list state to manage scrolling
    val initialIndex = itemList.indexOf(initialValue).coerceIn(0, itemList.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // Side effect to manage initial scroll position
    LaunchedEffect(initialIndex) {
        listState.scrollToItem(initialIndex, 0)
    }
    // Observe the first visible item index and trigger onValueChange
    LaunchedEffect(key1 = itemList) {
        snapshotFlow { listState.firstVisibleItemIndex }.collectLatest {
            onValueChange(itemList[it % listSize])
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
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .height(itemHeight * visibleItemsCount)
                    .fadingEdge(
                        brush = remember {
                            Brush.verticalGradient(
                                0F to Color.Transparent,
                                0.5F to Color.Black,
                                1F to Color.Transparent
                            )
                        },
                    ),
            ) {
                items(
                    count = intervals.last(),
                    key = { it },
                ) { index ->
                    val textModifier = Modifier.padding(vertical = verticalPadding)
                    when (index) {
                        in intervals[0]..<intervals[1] -> {
                            Text(
                                text = "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle,
                                modifier = textModifier,
                            )
                        }

                        in intervals[1]..<intervals[2] -> {
                            Text(
                                text = itemList[(index - coercedOutOfBoundsPageCount) % listSize].label(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = verticalPadding
                                ),
                            )
                        }

                        in intervals[2]..<intervals[3] -> {
                            Text(
                                text = "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = textStyle,
                                modifier = textModifier,
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
                    .offset(y = itemHeight * coercedOutOfBoundsPageCount - dividerThickness / 2),
                thickness = dividerThickness,
                color = effectiveDividerColor
            )

            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .offset(y = itemHeight * (coercedOutOfBoundsPageCount + 1) - dividerThickness / 2),
                thickness = dividerThickness,
                color = effectiveDividerColor
            )
        }
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
