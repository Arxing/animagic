package io.arxing.animagic.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun Demo2Screen() {
  InertialBouncingBall()
}

@Composable
fun InertialBouncingBall() {
  val ballRadius = 50f // 球半徑
  val friction = 0.98f // 慣性摩擦力
  val reboundFactor = -0.8f // 反彈系數

  // 記錄球的狀態
  var ballPosition by remember { mutableStateOf(Offset(300f, 300f)) }
  var velocity by remember { mutableStateOf(Offset.Zero) }
  var isDragging by remember { mutableStateOf(false) }

  // 畫布大小
  var canvasSize by remember { mutableStateOf(Offset.Zero) }

  // 慣性邏輯
  LaunchedEffect(isDragging) {
    while (!isDragging && velocity != Offset.Zero) {
      withFrameMillis {
        // 模擬摩擦力減速
        velocity = Offset(velocity.x * friction, velocity.y * friction)

        if (abs(velocity.x) < 1 && abs(velocity.y) < 1) {
          velocity = Offset.Zero // 停止移動
        }

        // 更新球的位置
        ballPosition = Offset(
          (ballPosition.x + velocity.x).coerceIn(ballRadius, canvasSize.x - ballRadius),
          (ballPosition.y + velocity.y).coerceIn(ballRadius, canvasSize.y - ballRadius)
        )

        // 邊界反彈邏輯
        if (ballPosition.x <= ballRadius || ballPosition.x >= canvasSize.x - ballRadius) {
          velocity = velocity.copy(x = velocity.x * reboundFactor)
        }
        if (ballPosition.y <= ballRadius || ballPosition.y >= canvasSize.y - ballRadius) {
          velocity = velocity.copy(y = velocity.y * reboundFactor)
        }
      }
    }
  }
  Box(
    modifier = Modifier.fillMaxSize(),
  ) {
    Canvas(
      modifier = Modifier
        .pointerInput(Unit) {
          return@pointerInput awaitPointerEventScope {
            while (true) {
              // 等待按下事件
              val event = awaitPointerEvent()

              // 找到第一個按下的 Pointer
              val pointer = event.changes.firstOrNull { it.pressed }
              if (pointer != null) {
                // 開始拖曳，記錄起始位置
                var previousPosition = pointer.position

                // 持續偵測拖曳
                while (true) {
                  val dragEvent = awaitPointerEvent()
                  val dragChange = dragEvent.changes.find { it.id == pointer.id }

                  if (dragChange == null || !dragChange.pressed) {
                    // Pointer 被釋放或拖曳結束，立即觸發 onDragEnd
                    println("Drag ended!")
                    break
                  }

                  // 計算拖曳量
                  val currentPosition = dragChange.position
                  val dragAmount = currentPosition - previousPosition
                  println("Dragging: $dragAmount")

                  // 更新上一個位置
                  previousPosition = currentPosition

                  // 消耗事件，避免手勢衝突
                  dragChange.consume()
                }
              }
            }
          }
          detectDragGestures(
            onDragStart = {
              println("## onDragStart()")
              isDragging = true
            },
            onDrag = { change, dragAmount ->
              println("## onChange(), pos=${change.position}")
              change.consume()
              ballPosition = Offset(
                (ballPosition.x + dragAmount.x).coerceIn(ballRadius, canvasSize.x - ballRadius),
                (ballPosition.y + dragAmount.y).coerceIn(ballRadius, canvasSize.y - ballRadius)
              )
              // 更新速度：用拖曳距離模擬瞬時速度
              velocity = dragAmount
            },
            onDragEnd = {
              println("## onDragEnd()")
              isDragging = false
              // 立即更新一幀以消除延遲
              ballPosition = Offset(
                (ballPosition.x + velocity.x).coerceIn(ballRadius, canvasSize.x - ballRadius),
                (ballPosition.y + velocity.y).coerceIn(ballRadius, canvasSize.y - ballRadius)
              )
            },
            onDragCancel = {
              println("## onDragCancel()")
              isDragging = false
            },
          )
        }
        .fillMaxSize()
    ) {
      if (canvasSize == Offset.Zero) {
        canvasSize = Offset(size.width, size.height) // 初始化畫布大小
      }

      // 繪製球
      drawCircle(
        color = Color.Red,
        radius = ballRadius,
        center = ballPosition
      )
    }

    Text("isDragging=$isDragging")
  }
}