package io.arxing.animagic

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform