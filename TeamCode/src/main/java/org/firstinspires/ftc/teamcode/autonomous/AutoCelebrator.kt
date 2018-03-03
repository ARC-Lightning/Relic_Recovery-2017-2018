package org.firstinspires.ftc.teamcode.autonomous

import org.firstinspires.ftc.teamcode.config.ConfigUser
import org.firstinspires.ftc.teamcode.io.Hardware

/**
 * Don't you think the robot enjoys having the belief that it finished all the enabled tasks?
 * Here's where its loud percussive noise can really make a mark.
 */
class AutoCelebrator {
    class Config : ConfigUser("AutoCelebrator.properties") {
        // Music to play
        val pourRhythm = file.getStringList("PourRhythm").map(String::toInt)
        val pourMovementTime = file.getInteger("PourMovementTime")

        // Music parameters
        val BPM = file.getInteger("BPM")

        // Servo positions
        val pourDown = file.getDouble("PourDown")
        val pourUp = file.getDouble("PourUp")
    }
    private val config = Config()
    private val pour = Hardware.glypher.bucketPour

    init {
        with (Hardware.telemetry) {
            write("I'm", "Done!")
            write("Time to", "Celebrate")
        }
    }

    // Blocks when setting, if applicable.
    private var pourRaised: Boolean = false
        set(raised) {
            pour.position = if (raised) config.pourUp else config.pourDown
            if (raised != field) {
                Thread.sleep(config.pourMovementTime.toLong())
                field = raised
            }
        }

    private fun playNote(time: Int) {
        if (time > 2 * config.pourMovementTime)
            Hardware.telemetry.warning("Can't keep up! Target time $time")

        val startTime = System.currentTimeMillis()
        pourRaised = false
        pourRaised = true
        while (System.currentTimeMillis() - startTime < time);
    }

    fun begin() {
        config.pourRhythm.forEach { beat ->
            playNote(60_000 / config.BPM * beat)
        }
    }
}