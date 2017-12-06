package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.AcsNavigator
import org.firstinspires.ftc.teamcode.io.DynamicConfig
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

    lateinit var navigator: AcsNavigator
    lateinit var vuforia: IVuforia
    lateinit var decider: DecisionMaker

    var vuMark: RelicRecoveryVuMark? = null

    /**
     * Main procedure for Autonomous.
     *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        if (!initAll()) return

        with(Hardware) {
            // The glyph clamp will be preloaded with a glyph. Close the clamp to hold it.
            Hardware.glypher.bucketClamping = true
        }

        waitForStart()

        // Take tasks from the decider and execute them
        while (!decider.isDone && !isStopRequested) {
            val nextTask = decider.nextTask()!!

            Hardware.telemetry.write("Performing next task", nextTask)
            val result = decider.doTask(nextTask, this)

            Hardware.telemetry.data("Task $nextTask successful?",
                    result ?: "there was a problem, so no")
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
     * - Decision maker
     */
    private fun initAll(): Boolean {
        try {
            // PRE-INIT - must be above all others
            Hardware.init(this, motorPower)

            with(Hardware) {
                navigator = AcsNavigator(telemetry, drivetrain)
                vuforia = Vuforia(opMode)
                decider = DecisionMaker()

                // No need to hold telemetry data back in a LinearOpMode
                Hardware.telemetry.autoClear = false
                Hardware.telemetry.autoUpdate = true

                Hardware.telemetry.data("Tasks", decider.nextTasks)
            }
        } catch (exc: Exception) {
            telemetry.addData("FATAL", "ERROR")
            telemetry.addData("Initialization failed", exc.message ?: "for a reason unknown to humankind")
            telemetry.update()
            return false
        }
        return true
    }

    // Tasks
    // TODO("testing pending") Optimize reliability coefficients

    object Tasks {

        @Task(priority = 30.0 / 85.0, reliability = 0.75)
        fun knockJewel(opMode: AutonomousMain): Boolean {
            opMode.navigator.goToPosition("jewel-knock")
            with(Hardware.knocker) {
                lowerArm()

                // If no concrete conclusion arises from the data, fail this task
                val colorDetected = detect() ?: return false

                // Knock off the jewel of color opposite to the team we're on
                removeJewel(colorDetected != DynamicConfig.alliance)

                raiseArm()
                return true
            }
        }

        @Task(priority = 10.0 / 85.0, reliability = 0.9)
        fun parkInSafeZone(opMode: AutonomousMain): Boolean {
            opMode.navigator.goToPosition("safe-zone")
            opMode.sleep(1000)
            return true
        }

        @Task(priority = 30.0 / 85.0, reliability = 0.7)
        fun readVuMark(opMode: AutonomousMain): Boolean {
            with(opMode) {
                // The VuMark is placed close enough to the jewel knocking position that it should be
                // able to be recognized at that position
                navigator.goToPosition("jewel-knock")

                vuforia.startTracking()

                // Allow camera to focus
                opMode.sleep(2000)

                vuMark = vuforia.readVuMark()
                vuforia.stopTracking()

                // If its representation is known, it's successful
                return vuMark != RelicRecoveryVuMark.UNKNOWN
            }
        }

        @Task(priority = 15.0 / 85.0, reliability = 0.5)
        fun placeInCryptoBox(opMode: AutonomousMain): Boolean {
            opMode.navigator.goToPosition(when (opMode.vuMark) {
                RelicRecoveryVuMark.LEFT -> "load-column1"
                RelicRecoveryVuMark.CENTER -> "load-column2"
                RelicRecoveryVuMark.RIGHT -> "load-column3"
            // TODO("testing pending") Loading into which column is most reliable?
                RelicRecoveryVuMark.UNKNOWN, null -> "load-column1"
            })

            Hardware.glypher.bucketPouring = true
            Hardware.glypher.bucketClamping = false
            Hardware.glypher.bucketPouring = false

            return true
        }
    }
}