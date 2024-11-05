package io.arxing.animagic.component

import androidx.compose.runtime.compositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import io.arxing.animagic.component.RootComponent.Route.DrawingRoute
import io.arxing.animagic.component.RootComponent.Route.HomeRoute
import io.arxing.animagic.component.RootComponent.RouteChild.DrawingChild
import io.arxing.animagic.component.RootComponent.RouteChild.HomeChild
import kotlinx.serialization.Serializable

val LocalRootComponent = compositionLocalOf<RootComponent> { throw NotImplementedError() }

class RootComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
  private val navigation = StackNavigation<Route>()

  val stack: Value<ChildStack<*, RouteChild>> = childStack(
    source = navigation,
    serializer = Route.serializer(),
    initialConfiguration = HomeRoute,
    handleBackButton = true,
    childFactory = { route, _ ->
      createChild(route)
    },
  )

  private fun onBackPressed() {
    navigation.pop()
  }

  private fun createChild(route: Route): RouteChild =
    when (route) {
      is HomeRoute -> {
        HomeChild(
          HomeComponent(
            onClickItem = {
              navigation.push(DrawingRoute(it))
            },
          )
        )
      }

      is DrawingRoute -> {
        DrawingChild(
          DrawingComponent(
            text = route.text,
            onBack = {
              onBackPressed()
            },
          )
        )
      }
    }

  sealed class RouteChild {
    class HomeChild(val component: HomeComponent) : RouteChild()
    data class DrawingChild(val component: DrawingComponent) : RouteChild()
  }

  @Serializable
  private sealed interface Route {
    @Serializable
    data object HomeRoute : Route

    @Serializable
    data class DrawingRoute(val text: String) : Route
  }
}
