package io.arxing.animagic.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.PauseCircle
import compose.icons.feathericons.PlayCircle
import compose.icons.feathericons.StopCircle
import io.arxing.animagic.internal.calcCurrent
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Demo1Screen() {
  val progress = remember { Animatable(0f) }
  var isPlaying by remember { mutableStateOf(true) }
  val numPointsRatio = remember { mutableFloatStateOf(0.3f) }
  val numTurnsRatio = remember { mutableFloatStateOf(0.2f) }
  val speedRatio = remember { mutableFloatStateOf(0.5f) }
  val scope = rememberCoroutineScope()

  val numPoints = calcCurrent(numPointsRatio.value, 100f, 3000f).toInt()
  val numTurns = calcCurrent(numTurnsRatio.value, 1f, 30f).toInt()
  val speed = calcCurrent(speedRatio.value, 60_000f, 500f).toInt()

  LaunchedEffect(isPlaying, speed) {
    if (isPlaying) {
      val remainingTime = speed.times(1f - progress.value).roundToInt()
      progress.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(easing = LinearEasing, durationMillis = remainingTime),
          repeatMode = RepeatMode.Restart,
        ),
      )
    } else {
      progress.stop()
    }
  }

  Box(
    modifier = Modifier.fillMaxSize(),
  ) {
    AnimationContent(
      progress = progress.value,
      numTurns = numTurns,
      numPoints = numPoints,
    )
    ControllBar(
      modifier = Modifier.align(Alignment.BottomCenter),
      progress = progress.value,
      isPlaying = isPlaying,
      numPointsRatioState = numPointsRatio,
      numTurnsRatioState = numTurnsRatio,
      speedRatioState = speedRatio,
      onProgressChange = {
        scope.launch {
          isPlaying = false
          progress.snapTo(it)
        }
      },
      onTogglePlay = {
        isPlaying = !isPlaying
      },
      onStop = {
        scope.launch {
          isPlaying = false
          progress.snapTo(0f)
        }
      },
    )
  }
}

@Composable
private fun AnimationContent(
  progress: Float,
  numPoints: Int,
  numTurns: Int
) {
  // 控制螺旋視角的變數
  var angleOffsetX by remember { mutableStateOf(0f) } // 控制 X 軸視角變化速率
  var angleOffsetY by remember { mutableStateOf(0f) } // 控制 Y 軸視角變化速率

  Canvas(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectDragGestures { _, dragAmount ->
          // 計算拖曳的偏移量
          val diffX = -dragAmount.x
          val diffY = -dragAmount.y

          // 根據拖曳的偏移量來更新視角
          angleOffsetX += diffX / size.width * 2f  // 控制 X 軸視角變化
          angleOffsetY += diffY / size.height * 2f // 控制 Y 軸視角變化
        }
      },
  ) {
    val radius = min(size.width, size.height) / 5f // 螺旋半徑
    val height = min(size.width, size.height) / 2f // 螺旋的高度
    val centerX = size.width / 2
    val centerY = size.height / 2

    // 螺旋的中心點
    val spiralCenterX = 0f
    val spiralCenterY = 0f
    val spiralCenterZ = 0f

    for (i in 0 until numPoints) {
      // 計算基本角度，控制旋轉圈數
      val angle = 2 * kotlin.math.PI * numTurns * i / numPoints

      // 用 progress 控制螺旋自轉的角度偏移
      val rotationAngle = progress * 2 * kotlin.math.PI.toFloat()

      // 螺旋的基本 X, Y, Z 位置
      val z = height * i / numPoints // 控制高度，z 軸
      val x = radius * cos(angle + rotationAngle) // x 軸位置加上自轉角度
      val y = radius * sin(angle + rotationAngle) // y 軸位置加上自轉角度

      // 計算每個點相對於螺旋中心的偏移量
      val relativeX = x - spiralCenterX
      val relativeY = y - spiralCenterY
      val relativeZ = z - spiralCenterZ

      // 3D 視角調整（旋轉矩陣）
      val cosX = cos(angleOffsetX)
      val sinX = sin(angleOffsetX)
      val cosY = cos(angleOffsetY)
      val sinY = sin(angleOffsetY)

      // 以螺旋的 3D 中心作為旋轉基準點，旋轉前先進行偏移
      // X 軸的旋轉（將 Y 和 Z 軸進行旋轉）
      val rotatedX = relativeX * cosX - relativeZ * sinX
      val rotatedZ = relativeX * sinX + relativeZ * cosX

      // Y 軸的旋轉（將 X 和 Z 軸進行旋轉）
      val rotatedY = relativeY * cosY - rotatedZ * sinY
      val rotatedZFinal = relativeY * sinY + rotatedZ * cosY

      // 旋轉後，將點的位置還原回畫布坐標系中
      val offsetX = centerX + rotatedX // 圍繞螺旋中心旋轉
      val offsetY = centerY + rotatedY - rotatedZFinal // 使用旋轉後的 Z 來控制 Y 的偏移

      val normalizedZ = i / numPoints.toFloat()
      val h = normalizedZ * 360f
      val s = 0.4f
      val l = 0.7f
      val color = Color.hsl(h, s, l)

      drawCircle(
        color = color,
        radius = 5f, // 圓點半徑
        center = androidx.compose.ui.geometry.Offset(offsetX.toFloat(), offsetY.toFloat())
      )
    }
  }
}

@Composable
private fun ControllBar(
  modifier: Modifier = Modifier,
  progress: Float,
  numPointsRatioState: MutableState<Float>,
  numTurnsRatioState: MutableState<Float>,
  speedRatioState: MutableState<Float>,
  isPlaying: Boolean,
  onProgressChange: (Float) -> Unit,
  onTogglePlay: () -> Unit,
  onStop: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 15.dp)
      .padding(bottom = 10.dp)
      .then(modifier),
  ) {
    Text(
      modifier = Modifier.align(Alignment.CenterHorizontally),
      text = "${progress.times(100).roundToInt()}%",
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(5.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        modifier = Modifier.size(30.dp).clip(CircleShape).clickable { onTogglePlay() },
        imageVector = if (isPlaying) FeatherIcons.PauseCircle else FeatherIcons.PlayCircle,
        contentDescription = null,
      )
      Icon(
        modifier = Modifier.size(30.dp).clip(CircleShape).clickable { onStop() },
        imageVector = FeatherIcons.StopCircle,
        contentDescription = null,
      )
      Slider(
        value = progress,
        onValueChange = onProgressChange,
        valueRange = 0f..1f,
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = "密集度")
        Slider(
          value = numPointsRatioState.value,
          onValueChange = { numPointsRatioState.value = it },
        )
      }

      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = "螺旋圈數")
        Slider(
          value = numTurnsRatioState.value,
          onValueChange = { numTurnsRatioState.value = it },
        )
      }

      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(text = "速率")
        Slider(
          value = speedRatioState.value,
          onValueChange = { speedRatioState.value = it },
        )
      }
    }
  }
}
