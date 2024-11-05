package io.arxing.animagic

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.arxing.animagic.component.LocalRootComponent
import io.arxing.animagic.component.RootComponent
import kotlinx.browser.document
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  val lifecycle = LifecycleRegistry()
  val root = RootComponent(componentContext = DefaultComponentContext(lifecycle))
  lifecycle.resume()

  ComposeViewport(document.body!!) {
    var typography by remember { mutableStateOf<Typography?>(null) }
    // LaunchedEffect(Unit) {
    //   typography = Typography(defaultFontFamily = loadFonts())
    // }

    CompositionLocalProvider(LocalRootComponent provides root) {
      MaterialTheme {
        App()
      }
    }
  }
}

@OptIn(InternalResourceApi::class)
private suspend fun loadFonts(): FontFamily {
  val regular = readResourceBytes("font/consola.ttf")
  val bold = readResourceBytes("font/consolab.ttf")
  return FontFamily(
    Font(identity = "consolas-regular", data = regular, weight = FontWeight.Normal),
    Font(identity = "consolas-bold", data = bold, weight = FontWeight.Bold),
  )
}
