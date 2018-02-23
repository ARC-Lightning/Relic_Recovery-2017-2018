package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.config.ConfigFile

/**
 * Tests for the test.properties configuration file and puts all key-value entries to telemetry.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Autonomous(name = "ConfigurationTest", group = "Pragmaticos")
class ConfigurationTest : LinearOpMode() {

    lateinit var config: ConfigFile

    override fun runOpMode() {
        config = ConfigFile("test.properties")

        waitForStart()

        // Print internal storage directory
        telemetry.addData("File path", ConfigFile.CONFIG_PATH)

        for ((key, value) in config.properties.entries) {
            telemetry.addData(key.toString(), value)
        }
        telemetry.update()

        while (opModeIsActive());
    }
}