package io.arxing.animagic.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import io.arxing.animagic.component.RootComponent
import io.arxing.animagic.component.RootComponent.RouteChild.DrawingChild
import io.arxing.animagic.component.RootComponent.RouteChild.HomeChild

@Composable
fun RootScreen(
  modifier: Modifier = Modifier,
  component: RootComponent,
) {
  Children(
    modifier = modifier,
    stack = component.stack,
    animation = stackAnimation(fade()),
  ) {
    when (val child = it.instance) {
      is HomeChild -> {
        HomeScreen(component = child.component)
      }

      is DrawingChild -> {
        DrawingScreen(component = child.component)
      }
    }
  }
}
