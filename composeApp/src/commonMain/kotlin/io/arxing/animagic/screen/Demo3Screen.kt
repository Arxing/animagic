package io.arxing.animagic.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import io.arxing.animagic.internal.calcCurrent
import io.arxing.animagic.internal.calcProgress
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun Demo3Screen() {
  SnowfallEffect()
}

data class Snowflake(
  var x: Float,
  var y: Float,
  val size: Float,
  val verticalSpeed: Float,
  var horizontalSpeed: Float,
  var nextChangeTime: Float,
  var elapsedTime: Float,
  var rotationAngle: Float,
) {

  companion object {

    fun randomHorizontalSpeed(): Float {
      val direction = (Random.nextFloat() - 0.5f) * 2f // -1~1
      return direction * 50f // -50~50
    }

    fun randomVerticalSpeed(min: Float = 20f, max: Float = 50f): Float {
      return Random.nextFloat() * (max - min) + min
    }

    fun randomNextChangeTime(min: Float = 1f, max: Float = 10f): Float {
      return Random.nextFloat() * (max - min) + min
    }

    fun randomSnowflake(screenWidth: Float, minSize: Float = 1f, maxSize: Float = 6f): Snowflake {
      val size = Random.nextFloat() * (maxSize - minSize) + minSize
      return Snowflake(
        x = Random.nextFloat() * screenWidth,
        y = -size,
        size = size,
        horizontalSpeed = randomHorizontalSpeed(),
        verticalSpeed = randomVerticalSpeed(),
        nextChangeTime = randomNextChangeTime(),
        elapsedTime = 0f,
        rotationAngle = Random.nextFloat() * 360f,
      )
    }
  }

  private fun updateX(deltaTime: Float): Float = x + horizontalSpeed * deltaTime

  private fun updateY(deltaTime: Float): Float = y + verticalSpeed * deltaTime

  fun newInstanceWithUpdate(deltaTime: Float, screenWidth: Float, screenHeight: Float): Snowflake {
    val left = 0f - size
    val right = screenWidth + size
    val top = 0f - size
    val bottom = screenHeight + size

    elapsedTime += deltaTime
    if (elapsedTime >= nextChangeTime) {
      horizontalSpeed = randomHorizontalSpeed()
      nextChangeTime = elapsedTime + randomNextChangeTime()
      elapsedTime = 0f
    }

    return if (x in left..right && y in top..bottom) {
      copy(
        x = updateX(deltaTime),
        y = updateY(deltaTime),
      )
    } else {
      randomSnowflake(screenWidth)
    }
  }
}

@Composable
fun SnowfallEffect(
  modifier: Modifier = Modifier,
  screenWidthChunkSizeRatio: Float = 0.2f,
  maxSnowflakeSize: Int = 100,
  snowflakeSpawnInterval: Float = 0.1f
) {
  // 繪製畫面的尺寸
  var screenSize by remember { mutableStateOf(IntSize.Zero) }
  val screenWidth = screenSize.width.toFloat()
  val screenHeight = screenSize.height.toFloat()
  // 螢幕寬度切成 N 等份，每一份的寬度
  val screenWidthChunkSize = (screenWidth * screenWidthChunkSizeRatio).toInt()
  // 積雪資料，key 是 chunk，value 是 height
  val snowAccumulation = remember(screenSize, screenWidthChunkSize) { mutableMapOf<Int, Float>() }
  // 雪花資料
  var snowflakes by remember(screenSize) { mutableStateOf<List<Snowflake>>(emptyList()) }

  val xToChunk: (Float) -> Int = remember(screenSize, screenWidthChunkSize) {
    { x -> (x * screenWidthChunkSize / screenWidth).roundToInt().coerceIn(0 until screenWidthChunkSize) }
  }

  val chunkToX: (Int) -> Float = remember(screenSize, screenWidthChunkSize) {
    { chunk -> chunk * screenWidth / screenWidthChunkSize }
  }

  LaunchedEffect(screenSize, screenWidthChunkSize, maxSnowflakeSize, snowflakeSpawnInterval) {
    var lastFrameTime = withFrameMillis { it }
    var spawnTimer = 0f

    while (true) {
      val currentTime = withFrameMillis { it }
      val deltaTime = (currentTime - lastFrameTime) / 1000f
      lastFrameTime = currentTime

      // 創建新的雪花列表以觸發重組
      snowflakes = snowflakes.mapNotNull { snowflake ->
        val updatedSnowflake = snowflake.newInstanceWithUpdate(deltaTime, screenWidth, screenHeight)
        if (updatedSnowflake.y >= screenHeight) {
          val snowX = updatedSnowflake.x
          val chunk = xToChunk(snowX)

          val currentHeight = snowAccumulation[chunk] ?: 0f

          // 取得相鄰區域的高度
          val leftHeight = snowAccumulation.getOrElse(chunk - 1) { 0f }
          val rightHeight = snowAccumulation.getOrElse(chunk + 1) { 0f }

          // 計算相鄰區域的平均高度
          val avgHeight = (leftHeight + rightHeight) / 2f

          // 計算高度差異
          val deltaHeight = abs(currentHeight - avgHeight)

          // 動態調整增量：根據差異大小來減少過大的變動
          val addHeight = if (deltaHeight > 10f) {
            // 如果差異過大，減少增量，讓高度過渡更平滑
            (avgHeight - currentHeight) * 0.5f  // 減少高度變化的幅度
          } else {
            // 如果差異不大，正常加上一定高度
            (avgHeight + 1f) - currentHeight
          }.coerceAtLeast(1f)

          // 如果當前高度小於相鄰區域的最大高度，則進行增長
          if (currentHeight <= max(leftHeight, rightHeight)) {
            snowAccumulation[chunk] = currentHeight + addHeight
          }
          null // 如果雪花已經超出螢幕邊界，則雪花消失
        } else
          updatedSnowflake
      }

      spawnTimer += deltaTime
      if (spawnTimer >= snowflakeSpawnInterval && snowflakes.size < maxSnowflakeSize) {
        spawnTimer = 0f
        repeat(15) {
        snowflakes += Snowflake.randomSnowflake(screenWidth)
        }
      }
    }
  }

  Canvas(
    modifier = Modifier
      .fillMaxSize()
      .onSizeChanged { screenSize = it },
  ) {
    val smoothedPath = Path().apply {
      moveTo(0f, screenHeight)

      val sortedSnowAcc = snowAccumulation.entries.sortedBy { it.key }
      // 用貝茲曲線來插值每兩個高度點之間
      for (i in 1 until sortedSnowAcc.size) {
        val (chunk1, height1) = sortedSnowAcc.elementAt(i - 1)
        val (chunk2, height2) = sortedSnowAcc.elementAt(i)

        val x1 = chunkToX(chunk1)
        val x2 = chunkToX(chunk2)

        // 計算插值點
        val controlX = (x1 + x2) / 2f
        val controlY = (height1 + height2) / 2f

        // 使用三次貝茲曲線的形式來平滑過渡
        cubicTo(
          x1 = controlX, y1 = screenHeight - controlY,
          x2 = controlX, y2 = screenHeight - controlY,
          x3 = x2,
          y3 = screenHeight - height2,
        )
      }

      // 對最後一個點進行平滑處理
      if (sortedSnowAcc.size > 1) {
        val (lastChunk, lastHeight) = sortedSnowAcc.last() // 最後一個積雪堆
        val (secondLastChunk, secondLastHeight) = sortedSnowAcc[sortedSnowAcc.size - 2] // 倒數第二個積雪堆

        val x2 = chunkToX(lastChunk)
        val x1 = chunkToX(secondLastChunk)

        // 計算最後一段控制點
        val controlX = (x1 + x2) / 2f
        val controlY = (lastHeight + secondLastHeight) / 2f

        cubicTo(
          x1 = controlX, y1 = screenHeight - controlY,
          x2 = controlX, y2 = screenHeight - controlY,
          x3 = x2,
          y3 = screenHeight - lastHeight,
        )
      }

      // 最後一個點
      lineTo(screenWidth, screenHeight)
      close()
    }


    drawPath(smoothedPath, color = Color.Gray)

    snowflakes.forEach { snowflake ->
      val snowflakePath = Path().apply {
        val x = snowflake.x
        val y = snowflake.y
        val size = snowflake.size
        moveTo(x, y)

        fun Float.toRadians(): Float = (this * PI / 180).toFloat()

        val numBranches = 6
        val angleIncrement = 360f / numBranches
        val branchLength = size * 0.5f

        // 繪製六個主要分支
        for (i in 0 until numBranches) {
          val angle = i * angleIncrement + snowflake.rotationAngle
          val radian = angle.toRadians()

          // 計算主分支的末端
          val endX = x + size * cos(radian)
          val endY = y + size * sin(radian)

          // 繪製主分支
          moveTo(x, y)
          lineTo(endX, endY)

          // 在每條分支上繪製冰晶的中空結構
          val numSubBranches = 3
          for (j in 1..numSubBranches) {
            val subBranchLength = branchLength * (1 - j * 0.3f) // 分支逐漸縮短
            val subBranchAngle = angle + 30f * (if (j % 2 == 0) 1 else -1) // 分支的角度變化
            val subBranchRadian = subBranchAngle.toRadians()

            // 計算分支末端
            val subBranchEndX = endX + subBranchLength * cos(subBranchRadian)
            val subBranchEndY = endY + subBranchLength * sin(subBranchRadian)

            // 繪製分支
            lineTo(subBranchEndX, subBranchEndY)

            // 返回到主分支的末端
            moveTo(endX, endY)
          }
        }

        close()
      }

      drawPath(
        path = snowflakePath,
        color = Color.Gray,
      )
    }
  }

  Text("雪數量=${snowflakes.size}")

  Slider(
    value = calcProgress(speed, 0f, 2000f),
    onValueChange = {
      speed = calcCurrent(it, 0f, 2000f)
    }
  )
}

var speed: Float by mutableStateOf(1000f)