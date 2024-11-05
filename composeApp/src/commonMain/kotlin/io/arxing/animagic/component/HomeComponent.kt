package io.arxing.animagic.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class HomeComponent(
  val onClickItem: (String) -> Unit,
) {
  var texts: List<String> by mutableStateOf(emptyList())
}
