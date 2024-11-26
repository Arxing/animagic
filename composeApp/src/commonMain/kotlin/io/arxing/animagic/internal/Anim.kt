package io.arxing.animagic.internal

fun calcCurrent(progress: Float, from: Float, into: Float): Float = from + progress * (into - from)

fun calcProgress(current: Float, from: Float, into: Float): Float = (current - from) / (into - from)
