package de.alijas.scheduler

data class PowerOnOffConfiguration(
    val delayInMinutes: Long,
    val runningTimeInSeconds: Long,
    val powerOnCommand: String,
    val powerOffCommand: String,
)
