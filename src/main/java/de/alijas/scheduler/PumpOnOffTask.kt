package de.alijas.scheduler

import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*


class PumpOnOffTask(private val config: PowerOnOffConfiguration) : TimerTask() {
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private var logger: Logger = LoggerFactory.getLogger(PumpOnOffTask::class.java)
    private val client: Client = ClientBuilder.newClient()

    override fun run() {
        startPump()
        Thread.sleep(config.runningTimeInSeconds * 1000)
        stopPump()
    }

    private fun startPump() {
        logger.info("Starting Pump (Next start will @ ${getNextPowerOnTime()})")
        executeGetCall(config.powerOnCommand)
    }

    private fun stopPump() {
        logger.info("Stopping Pump")
        executeGetCall(config.powerOffCommand)
    }

    private fun executeGetCall(uri: String) {
        val get = client
            .target(uri)
            .request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
            .get()

        logger.debug(get.readEntity(String::class.java))
    }

    private fun getNextPowerOnTime() : String {
        val timeInMillis: Long = Date().time
        val dateAfterAddingDelay = Date(timeInMillis + config.delayInMinutes * 60 * 1000)
        return timeFormatter.format(dateAfterAddingDelay)
    }

}