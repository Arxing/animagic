package io.arxing.animagic.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.arxing.animagic.component.DrawingComponent

@Composable
fun DrawingScreen(
  modifier: Modifier = Modifier,
  component: DrawingComponent,
) {
  Column(modifier = modifier.fillMaxSize()) {
    Button(
      onClick = {
        component.onBack()
      }
    ) {
      Text("back")
    }

    Button(
      onClick = {
        component.text += "OO"
      }
    ) {
      Text("change")
    }
    Text(text = component.text)
  }
}
