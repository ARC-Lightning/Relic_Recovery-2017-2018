package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.config.ConfigUser
import org.firstinspires.ftc.teamcode.io.Hardware
import org.locationtech.jts.math.Vector2D
import java.util.*

/**
 * The base LinearOpMode procedure in which autonomous operation is performed.
 * Four actions that score points for us:
 * - Putting pre-loaded glyph into column
 * - The right column according to the VuMark
 * - Knocking off the right jewel
 * - Parking in the safe zone
 *
 * To use this base class, extend it while specifying its constructor parameters and put
 *   the annotation on that class.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
open class AutonomousBase(val allianceColor: AllianceColor,
                          val isStartingLeft: Boolean) : LinearOpMode() {

    companion object {
        // Statically available properties
        lateinit var alliance: AllianceColor
        var startingLeft: Boolean = true
    }

    class Config : ConfigUser("AutonomousBase/config.properties") {

        val taskSequence = file.getStringList("TaskSequence")
        val motorPower = file.getDouble("MotorPower")

        val useDecisionMaker = file.getBoolean("UseDecisionMaker")
        val useAutoCelebrator = file.getBoolean("UseAutoCelebrator")

        // Task definition decisions
        val jewelSenseAttempts = file.getInteger("JewelSenseAttempts")
        val jewelDisplacementMax = file.getDouble("JewelDisplacementMax")
        // For how much should it wait for Vuforia to recognize the VuMark? (in ms)
        val vuMarkTimeout = file.getInteger("VuMarkTimeout")
        val flywheelPower = file.getDouble("FlywheelPower")
    }

    lateinit var config: Config

    lateinit var navigator: IAutoNav
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

        waitForStart()

        if (config.useDecisionMaker) {
            Hardware.telemetry.write("Task Decision Model", "Arbitrary")
            while (!decider.isDone && !isStopRequested) {
                runTask(decider.nextTask()!!)
            }
        } else {
            Hardware.telemetry.write("Task Decision Model", "Predefined")
            var useFailsafe = false
            val seqIt = config.taskSequence.iterator()

            while (seqIt.hasNext() && !isStopRequested) {
                if (runTask(seqIt.next()) != true) {
                    Hardware.telemetry.error("Predefined task failure, fail-safe")
                    useFailsafe = true
                    break
                }
            }
            if (useFailsafe) {
                while (!decider.isDone && !isStopRequested) {
                    val task = decider.nextTask()
                    if (task != null) {
                        runTask(task)
                    } else {
                        Hardware.telemetry.warning("Next task returned null while decider not done")
                    }
                }
            }
            // We're finished, celebrate if enabled
            if (config.useAutoCelebrator)
                AutoCelebrator().begin()
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
            config = Config()
            Hardware.init(this, config.motorPower)

            with(Hardware) {
                navigator = AutoNav()
                vuforia = Vuforia(opMode)
                decider = DecisionMaker()

                // No need to hold telemetry data back in a LinearOpMode
                Hardware.telemetry.autoClear = false
                Hardware.telemetry.autoUpdate = true

                // Assign properties to companion object
                alliance = allianceColor
                startingLeft = isStartingLeft

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
        fun knockJewel(opMode: AutonomousBase): Boolean {

            /** Moves the arm for a random amount in either direction, bounded by a configured parameter.*/
            fun armDisplacement() =
                    (Random().nextDouble() - 0.5) * 2 * opMode.config.jewelDisplacementMax

            with(Hardware.knocker) {
                opMode.navigator.beginJewelKnock()

                lowerArm()

                run attempt@ {
                    repeat(opMode.config.jewelSenseAttempts) { i ->
                        Hardware.telemetry.write("Knock jewel attempt", (i + 1).toString())
                        opMode.sleep(1000)

                        val colorDetected = detect()

                        // If no concrete conclusion arises from the data, fail this task
                        if (colorDetected != null) {
                            // Knock off the jewel of color opposite to the team we're on
                            Hardware.telemetry.write("Color detected", colorDetected.toString())
                            removeJewel(colorDetected != alliance)
                            return@attempt
                        }
                        arm.position += armDisplacement()
                    }
                }

                opMode.navigator.endJewelKnock()

                // Precisely restoring the start position is hopeless. If jewel not removed, give up
                return true
            }
        }

        @Task(priority = 10.0 / 85.0, reliability = 0.9)
        fun parkInSafeZone(opMode: AutonomousBase): Boolean {
            opMode.navigator.goToCryptoBox(RelicRecoveryVuMark.CENTER)
            opMode.sleep(2000)
//            opMode.navigator.returnFromCryptoBox(RelicRecoveryVuMark.CENTER)
            return true
        }

        @Task(priority = 30.0 / 85.0, reliability = 0.7)
        fun readVuMark(opMode: AutonomousBase): Boolean {
            with(opMode) {
                navigator.beginReadingVuMark()
                vuforia.startTracking()

                // Allow camera to focus
                opMode.sleep(3000)

                // Repeat until timeout or recognition
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < opMode.config.vuMarkTimeout) {
                    vuMark = vuforia.readVuMark()

                    if (vuMark != null)
                        break

                }
                vuforia.stopTracking()

                Hardware.telemetry.write("Read VuMark", vuMark?.name ?: "Failed")
                navigator.endReadingVuMark()

                // If its representation is known, it's successful
                return vuMark != RelicRecoveryVuMark.UNKNOWN
            }
        }

        @Task(priority = 15.0 / 85.0, reliability = 0.7)
        fun placeInCryptoBox(opMode: AutonomousBase): Boolean {
            opMode.navigator.goToCryptoBox(opMode.vuMark ?: RelicRecoveryVuMark.CENTER)

            with (Hardware) {
                glypher.collectorPower = opMode.config.flywheelPower
                glypher.bucketPourPos = 1.0
                opMode.sleep(1000)

                // Shove it just a bit
                drivetrain.move(Vector2D(0.0, 0.5))

                glypher.bucketPourPos = 0.0
                glypher.collectorPower = 0.0

                // Remove contact with glyph
                drivetrain.move(Vector2D(0.0, -1.0))
            }

            return true
        }
    }
}

@Autonomous(name = "Auto Red Left", group = "Pragmaticos")
class RedLeftAuto : AutonomousBase(AllianceColor.RED, true)

@Autonomous(name = "Auto Red Right", group = "Pragmaticos")
class RedRightAuto : AutonomousBase(AllianceColor.RED, false)

@Autonomous(name = "Auto Blue Left", group = "Pragmaticos")
class BlueLeftAuto : AutonomousBase(AllianceColor.BLUE, true)

@Autonomous(name = "Auto Blue Right", group = "Pragmaticos")
class BlueRightAuto : AutonomousBase(AllianceColor.BLUE, false)
