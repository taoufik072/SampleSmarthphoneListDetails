package fr.taoufikcode.common

import java.time.Duration
import java.time.Instant

import java.time.Clock

fun Long.isExpired(minutePassed: Long, clock: Clock = Clock.systemDefaultZone()): Boolean {
    val currentTime = Instant.now(clock).toEpochMilli()
    val minutesInMillis = Duration.ofMinutes(minutePassed).toMillis()
    return currentTime - this > minutesInMillis
}
