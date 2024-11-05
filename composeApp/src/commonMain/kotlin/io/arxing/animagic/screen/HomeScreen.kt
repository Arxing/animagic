package io.arxing.animagic.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.arxing.animagic.component.HomeComponent
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  component: HomeComponent,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    stickyHeader {
      Button(
        onClick = {
          val i = Random.nextInt(1000)
          component.texts += "加了: $i"
        }
      ) {
        Text("點我點我~")
      }
    }
    items(items = component.texts) {
      Box(modifier = Modifier.size(300.dp, 100.dp).background(Color.Gray).clickable {
        component.onClickItem(it)
      }) {
        Text(modifier = Modifier.align(Alignment.Center), text = it)
      }
    }
  }
}