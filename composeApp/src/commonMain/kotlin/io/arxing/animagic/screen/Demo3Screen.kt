package io.arxing.animagic.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
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
) {

  companion object {

    fun randomHorizontalSpeed(): Float {
      val direction = (Random.nextFloat() - 0.5f) * 2f // -1~1
      return direction * 50f // -50~50
    }

    fun randomVerticalSpeed(): Float {
      return Random.nextFloat() * 1000f + 20f // 20~70
    }

    fun randomNextChangeTime(): Float {
      return Random.nextFloat() * 3f + 1f // 1~10
    }

    fun randomSnowflake(screenWidth: Float): Snowflake {
      val size = Random.nextFloat() * 8f + 2f
      return Snowflake(
        x = Random.nextFloat() * screenWidth,
        y = -size,
        size = Random.nextFloat() * 8f + 2f,
        horizontalSpeed = randomHorizontalSpeed(),
        verticalSpeed = randomVerticalSpeed(),
        nextChangeTime = randomNextChangeTime(),
        elapsedTime = 0f,
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
) {
  val snowAccumulation by remember { mutableStateOf(mutableMapOf<Float, Float>()) }
  var screenSize by remember { mutableStateOf(IntSize.Zero) }
  val screenWidth = screenSize.width.toFloat()
  val screenHeight = screenSize.height.toFloat()
  val maxSnowflakeSize = 500
  val snowflakeSpawnInterval = 0.1f

  // 使用 mutableStateOf 來存儲雪花狀態
  var snowflakes by remember(screenSize) {
    mutableStateOf(
      List(0) { id -> Snowflake.randomSnowflake(screenWidth) }
    )
  }

  LaunchedEffect(screenSize) {
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
          val currentHeight = snowAccumulation[snowX] ?: 0f
          snowAccumulation[snowX] = (currentHeight + updatedSnowflake.size)
          null
        } else
          updatedSnowflake
      }

      spawnTimer += deltaTime
      if (spawnTimer >= snowflakeSpawnInterval && snowflakes.size < maxSnowflakeSize) {
        spawnTimer = 0f
        repeat(10) {
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
    snowAccumulation.forEach { (x, height) ->
      drawCircle(
        color = Color.Gray,
        radius = height,
        center = Offset(x, screenHeight - height)
      )
    }

    snowflakes.forEach { snowflake ->
      drawCircle(
        color = Color.Gray,
        radius = snowflake.size,
        center = Offset(snowflake.x, snowflake.y),
        alpha = 0.8f
      )
    }
  }

  Text("雪數量=${snowflakes.size}")
}
