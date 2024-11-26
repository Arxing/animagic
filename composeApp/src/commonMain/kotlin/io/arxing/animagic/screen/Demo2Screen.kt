package io.arxing.animagic.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun Demo2Screen() {
  DraggableBoxWithInertiaKMP()
}

@Composable
fun DraggableBoxWithInertiaKMP() {
  val boxOffsetX = remember { Animatable(0f) }
  val boxOffsetY = remember { Animatable(0f) }
  var velocityX by remember { mutableStateOf(0f) }
  var velocityY by remember { mutableStateOf(0f) }

  val boxSize = 100.dp
  var parentWidth by remember { mutableStateOf(0f) }
  var parentHeight by remember { mutableStateOf(0f) }
  val density = LocalDensity.current

  Box(
    Modifier
      .fillMaxSize()
      .background(Color.LightGray)
      .onGloballyPositioned { coordinates ->
        // 獲取父容器的寬高
        val size = coordinates.size
        parentWidth = size.width.toFloat()
        parentHeight = size.height.toFloat()
      }
  ) {
    Box(
      Modifier
        .offset {
          IntOffset(
            boxOffsetX.value.roundToInt(),
            boxOffsetY.value.roundToInt()
          )
        }
        .size(boxSize)
        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = {
              // 初始化速度
              velocityX = 0f
              velocityY = 0f
            },
            onDragEnd = {
              // 進行慣性與邊界回彈處理
              CoroutineScope(Dispatchers.Main).launch {
                applyInertiaAndBounce(
                  boxOffsetX,
                  0f,
                  parentWidth - with(density) { boxSize.toPx() },
                  initialVelocity = velocityX
                )
                applyInertiaAndBounce(
                  boxOffsetY,
                  0f,
                  parentHeight - with(density) { boxSize.toPx() },
                  initialVelocity = velocityY
                )
              }
            },
            onDrag = { change, dragAmount ->
              change.consume()
              // 計算速度
              velocityX = dragAmount.x * 0.2f
              velocityY = dragAmount.y * 0.2f

              CoroutineScope(Dispatchers.Main).launch {
                boxOffsetX.snapTo(boxOffsetX.value + dragAmount.x)
                boxOffsetY.snapTo(boxOffsetY.value + dragAmount.y)
              }
            }
          )
        }
    )
  }
}

private suspend fun applyInertiaAndBounce(
  offset: Animatable<Float, AnimationVector1D>,
  minBound: Float,
  maxBound: Float,
  initialVelocity: Float,
  velocityDecayFactor: Float = 0.98f, // 慣性衰減速度
  reboundFactor: Float = 0.5f // 回彈速度衰減
) {
  var velocity = initialVelocity
  while (velocity.absoluteValue > 0.5f) {
    velocity *= velocityDecayFactor
    val newOffset = offset.value + velocity

    when {
      newOffset < minBound -> {
        // 左邊界碰撞
        velocity = -velocity * reboundFactor
        offset.snapTo(minBound)
      }
      newOffset > maxBound -> {
        // 右邊界碰撞
        velocity = -velocity * reboundFactor
        offset.snapTo(maxBound)
      }
      else -> {
        // 慣性滑動
        offset.snapTo(newOffset)
      }
    }
    delay(16) // 每幀更新
  }
}
