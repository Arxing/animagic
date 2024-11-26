package io.arxing.animagic.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DrawingScreen(
  modifier: Modifier = Modifier,
  text: String,
) {
  Column(modifier = modifier.fillMaxSize()) {
    Button(
      onClick = {
      }
    ) {
      Text("back")
    }

    Button(
      onClick = {
      }
    ) {
      Text("change")
    }
    Text(text = text)
  }
}
