package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.io.DynamicConfig
import org.firstinspires.ftc.teamcode.io.Hardware
import org.firstinspires.ftc.teamcode.teleop.GamepadListener

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
        val taskSequence = listOf(
                "knockJewel",
                "readVuMark",
                "placeInCryptoBox",
                "parkInSafeZone"
        )
        val runTasksArbitrarily = false
    }

    lateinit var navigator: IAutoNav
    lateinit var vuforia: IVuforia
    lateinit var decider: DecisionMaker
    lateinit var configurator: GamepadListener

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

            with(DynamicConfig) {
                // Read Gamepad values for starting position.
                // Press X for blue alliance, otherwise red;
                //   Press dpad left for left start, otherwise right
                alliance = if (gamepad1.x) AllianceColor.BLUE else AllianceColor.RED
                isStartingLeft = gamepad1.dpad_left
                Hardware.telemetry.data("DynConf Input",
                        "AllianceColor=$alliance StartOnLeft=$isStartingLeft")
            }
        }
        waitForStart()

        if (runTasksArbitrarily) {
            Hardware.telemetry.write("Task Decision Model", "Arbitrary")
            while (!decider.isDone && !isStopRequested) {
                runTask(decider.nextTask()!!)
            }
        } else {
            Hardware.telemetry.write("Task Decision Model", "Predefined")
            val seqIt = taskSequence.iterator()
            while (seqIt.hasNext() && !isStopRequested) {
                runTask(seqIt.next())
            }
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
                navigator = AutoNav()
                vuforia = Vuforia(opMode)
                decider = DecisionMaker()
                configurator = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)

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

    /**
     * Executes the task with the given name verbosely.
     * @returns The result returned by the task
     */
    private fun runTask(taskName: String): Boolean? {
        Hardware.telemetry.write("Performing next task", taskName)
        val result = decider.doTask(taskName, this)

        Hardware.telemetry.data("Task $taskName successful?",
                result ?: "there was a problem, so no")

        return result
    }

    // Tasks
    // TODO("testing pending") Optimize reliability coefficients

    object Tasks {

        @Task(priority = 30.0 / 85.0, reliability = 0.75)
        fun knockJewel(opMode: AutonomousMain): Boolean {
            with(Hardware.knocker) {
                opMode.navigator.beginJewelKnock()

                lowerArm()

                val colorDetected = detect()

                // If no concrete conclusion arises from the data, fail this task
                if (colorDetected != null) {
                    // Knock off the jewel of color opposite to the team we're on
                    removeJewel(colorDetected != DynamicConfig.alliance)
                }

                raiseArm()

                opMode.navigator.endJewelKnock()
                return colorDetected != null
            }
        }

        @Task(priority = 10.0 / 85.0, reliability = 0.9)
        fun parkInSafeZone(opMode: AutonomousMain): Boolean {
            opMode.navigator.goToCryptoBox(RelicRecoveryVuMark.CENTER)
            opMode.sleep(2000)
            opMode.navigator.returnFromCryptoBox(RelicRecoveryVuMark.CENTER)
            return true
        }

        @Task(priority = 30.0 / 85.0, reliability = 0.7)
        fun readVuMark(opMode: AutonomousMain): Boolean {
            with(opMode) {
                vuforia.startTracking()

                // Allow camera to focus
                opMode.sleep(4000)

                vuMark = vuforia.readVuMark()
                vuforia.stopTracking()

                Hardware.telemetry.write("Read VuMark", vuMark?.name ?: "Failed")

                // If its representation is known, it's successful
                return vuMark != RelicRecoveryVuMark.UNKNOWN
            }
        }

        @Task(priority = 15.0 / 85.0, reliability = 0.7)
        fun placeInCryptoBox(opMode: AutonomousMain): Boolean {
            if (opMode.vuMark == null) {
                return false
            }
            opMode.navigator.goToCryptoBox(opMode.vuMark!!)

            Hardware.glypher.bucketPouring = true
            Hardware.glypher.bucketClamping = false
            Hardware.glypher.bucketPouring = false

            return true
        }
    }
}