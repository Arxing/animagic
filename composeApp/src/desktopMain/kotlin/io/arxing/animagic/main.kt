package io.arxing.animagic

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.arxing.animagic.component.LocalRootComponent
import io.arxing.animagic.component.RootComponent

fun main() = application {
  val lifecycle = rememberSaveable { LifecycleRegistry() }
  val root = rememberSaveable { RootComponent(DefaultComponentContext(lifecycle)) }

  Window(
    onCloseRequest = ::exitApplication,
    title = "Compose-Animagic",
  ) {
    CompositionLocalProvider(LocalRootComponent provides root) {
      App()
    }
  }
}
