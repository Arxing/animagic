package io.arxing.animagic.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DrawingComponent(
  text: String,
  val onBack: () -> Unit,
) {
  var text: String by mutableStateOf(text)
}
