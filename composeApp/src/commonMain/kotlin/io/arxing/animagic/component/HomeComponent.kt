package io.arxing.animagic.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.router.stack.push
import io.arxing.animagic.component.RootComponent.Route.DrawingRoute

class HomeComponent {
  var texts: List<String> by mutableStateOf(emptyList())

  fun onClickItem() {
    navigation.push(DrawingRoute(it))
  }
}
