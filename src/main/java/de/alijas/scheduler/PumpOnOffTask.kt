package de.alijas.scheduler

import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


class PumpOnOffTask(private val config: PowerOnOffConfiguration) : TimerTask() {
    var logger: Logger = LoggerFactory.getLogger(PumpOnOffTask::class.java)
    val client: Client = ClientBuilder.newClient()

    override fun run() {
        startPump()
        Thread.sleep(config.runningTimeInSeconds * 1000)
        stopPump()
    }

    private fun startPump() {
        logger.info("Starting Pump")
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

}