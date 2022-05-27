package de.alijas.cmd

import de.alijas.scheduler.PowerOnOffConfiguration
import de.alijas.scheduler.PumpOnOffTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.jar.Attributes
import java.util.jar.Manifest
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val logger: Logger = LoggerFactory.getLogger(PumpOnOffTask::class.java)

    if (args.size != 1) {
        logger.error("Usage: de.alijas.cmd.ExecutorKt <path to config file>")
        exitProcess(1)
    }


    Executor(args).setupAndRun()
}

class Executor(args: Array<String>) {
    private val logger: Logger = LoggerFactory.getLogger(PumpOnOffTask::class.java)
    private val config: PowerOnOffConfiguration

    init {

        val file = File(args[0])

        val prop = Properties()
        FileInputStream(file).use { prop.load(it) }

        // Print all properties
        prop.stringPropertyNames()
            .associateWith {prop.getProperty(it)}
            .forEach { println(it) }

        config = PowerOnOffConfiguration(
            delayInMinutes = prop.getProperty("delay.in.minutes").toLong(),
            runningTimeInSeconds = prop.getProperty("running.time.in.seconds").toLong(),
            powerOnCommand = prop.getProperty("power.on.command"),
            powerOffCommand = prop.getProperty("power.off.command")
        )

        logger.info("################################################### ")
        logger.info("#### Starting pumpcontrol Executor v.${getImplementationVersion()} ####")
        logger.info("# Delay in Minutes.......: ${config.delayInMinutes}")
        logger.info("# Running time in seconds: ${config.runningTimeInSeconds}")
        logger.info("# Power ON Command.......: ***")
        logger.info("# Power OFF Command......: ***")
        logger.info("################################################### ")

    }

    fun setupAndRun() {

        val timer = Timer("Pump-On-Off-Timer")
        timer.schedule(PumpOnOffTask(config), 0, TimeUnit.MINUTES.toMillis(config.delayInMinutes))
    }

    private fun getImplementationVersion(): String {
        try {
            val resources = javaClass.classLoader
                .getResources("META-INF/MANIFEST.MF")
            while (resources.hasMoreElements()) {
                val manifest = Manifest(resources.nextElement().openStream())
                val mainAttributes: Attributes = manifest.mainAttributes
                return mainAttributes.getValue("Implementation-Version")
            }
        } catch (e: IOException) {
            logger.error("Error reading manifest", e)
        }
        return "WITHIN-IDE"
    }

}