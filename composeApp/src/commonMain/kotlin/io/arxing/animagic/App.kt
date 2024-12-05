package io.arxing.animagic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronLeft
import io.arxing.animagic.screen.Demo1Screen
import io.arxing.animagic.screen.Demo2Screen
import io.arxing.animagic.screen.Demo3Screen
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  MaterialTheme {
    PreComposeApp {
      val navigator = rememberNavigator()
      Row(
        modifier = Modifier.fillMaxSize(),
      ) {
        SideNavi(navigator)
        NavHost(
          modifier = Modifier.fillMaxSize(),
          navigator = navigator,
          initialRoute = "demo3",
        ) {
          scene("demo1") {
            Demo1Screen()
          }
          scene("demo2") {
            Demo2Screen()
          }
          scene("demo3") {
            Demo3Screen()
          }
        }
      }
    }
  }
}

@Composable
private fun SideNavi(navigator: Navigator) {
  LazyColumn(
    modifier = Modifier.width(100.dp).fillMaxHeight().background(Color.LightGray),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    item {
      IconButton(
        onClick = {
          navigator.goBack()
        }
      ) {
        Icon(imageVector = FeatherIcons.ChevronLeft, contentDescription = null)
      }
    }
    item {
      CommonButton("Demo1") {
        navigator.navigate("demo1")
      }
    }
    item {
      CommonButton("Demo2") {
        navigator.navigate("demo2")
      }
    }
    item {
      CommonButton("Demo3") {
        navigator.navigate("demo3")
      }
    }
  }
}

@Composable
private fun CommonButton(text: String, onClick: () -> Unit) {
  Button(onClick = onClick) {
    Text(text = text)
  }
}

