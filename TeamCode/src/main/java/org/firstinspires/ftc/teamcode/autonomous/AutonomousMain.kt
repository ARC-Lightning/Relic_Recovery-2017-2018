package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.AcsNavigator
import org.firstinspires.ftc.teamcode.io.Hardware

/**
 * The main LinearOpMode procedure in which autonomous operation is performed.
 * Four actions that score points for us:
 * - Putting pre-loaded glyph into column
 * - The right column according to the VuMark
 * - Knocking off the right jewel
 * - Parking in the safe zone
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Autonomous(name = "Autonomous Main", group = "Pragmaticos")
class AutonomousMain : LinearOpMode() {

    // CONFIGURATIONS
    companion object {
        val motorPower = 0.9
    }

    lateinit var hardware: Hardware
    lateinit var navigator: AcsNavigator
    lateinit var vuforia: IVuforia

    /**
     * Main procedure for Autonomous.
     *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        if (!initAll()) return

        with(hardware) {

            // The glyph clamp will be preloaded with a glyph. Close the clamp to hold it.
            clamp.leftArm = true
            clamp.rightArm = true

            waitForStart()
            vuforia.startTracking()

            // TODO(waiting) hardware unfinished, particularly encoders
        }
    }

    /**
     * Initializes all necessary systems for Autonomous operation.
     * Includes the following systems:
     * - Telemetry   ╮
     * - Drivetrain  ├─ Hardware
     * - Manipulators│
     * - Sensors     ╯
     * - Navigator (GameMap)
     * - VuMark Reading (Vuforia)
     */
    private fun initAll(): Boolean {
        try {

            hardware = Hardware(this, motorPower)
            navigator = AcsNavigator(hardware.telemetry, hardware.drivetrain)
            vuforia = Vuforia(this)

        } catch (exc: Exception) {
            telemetry.addData("FATAL", "ERROR")
            telemetry.addData("Initialization failed", exc.message ?: "for a reason unknown to humankind")
            return false
        }
        return true
    }
}
