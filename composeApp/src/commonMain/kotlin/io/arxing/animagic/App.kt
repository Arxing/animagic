package io.arxing.animagic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.arxing.animagic.component.LocalRootComponent
import io.arxing.animagic.screen.RootScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  MaterialTheme {
    RootScreen(
      modifier = Modifier.fillMaxSize(),
      component = LocalRootComponent.current,
    )
  }
}
