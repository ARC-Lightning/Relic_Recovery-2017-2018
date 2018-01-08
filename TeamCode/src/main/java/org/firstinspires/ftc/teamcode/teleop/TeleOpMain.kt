package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.io.DynamicConfig
import org.firstinspires.ftc.teamcode.io.Hardware
import org.locationtech.jts.math.Vector2D

/**
 * Main procedure for TeleOp during Relic Recovery.
 * As a quick reference, the following actions score points for our team during TeleOp/end game.
 * - Glyph stored in cryptobox
 * - Completed row of 3 or column of 4
 * - Completed cipher
 * - Robot balanced on balancing stone
 * - (end game) Relic in Zone 1 thru 3
 * - (end game) Relic upright bonus
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "TeleOp Main", group = "Pragmaticos")
class TeleOpMain : OpMode() {

    // Configuration values
    object Config {
        val motorPower = 0.9
        val turnSpeed = 0.95
        val glyphCollectorPower = 0.9
        val bucketLiftPower = 0.3
        val stickAxisToBinaryThreshold = 0.3
        val bucketPourSensitivity = 0.015
    }

    object InputColumns {
        lateinit var collectorIn: ChangeBasedInputColumn<Boolean>
        lateinit var collectorOut: ChangeBasedInputColumn<Boolean>
        lateinit var collectorHug: ChangeBasedInputColumn<Boolean>
    }

    lateinit private var padListener: GamepadListener

    override fun init() {
        // Initialize systems
        Hardware.init(this, Config.motorPower)
        padListener = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)

        // Collector flywheel mechanism will be folded - release it when initializing
        Hardware.glypher.unfoldCollector()

        // Initialize toggle input surfaces, which currently includes collectorIn use (A, gamepad 2)
        InputColumns.collectorIn = ChangeBasedInputColumn { gamepad2.a }
        InputColumns.collectorOut = ChangeBasedInputColumn { gamepad2.b }
        InputColumns.collectorHug = ChangeBasedInputColumn { gamepad2.y }

        // Lock the jewel arm
        //Hardware.knocker.raiseArm()
    }

    override fun init_loop() {
        padListener.update()
    }

    override fun loop() {
        // Gamepad mappings
        with(Hardware) {
            fun Boolean.int() = if (this) 1.0 else 0.0
            fun stickAxisToBinary(axisValue: Double, previous: Boolean): Boolean = when {
                axisValue > Config.stickAxisToBinaryThreshold -> true
                axisValue < -Config.stickAxisToBinaryThreshold -> false
                else -> previous
            }

            with(gamepad1) {
                // Drivetrain movement
                val moveVec = Vector2D(
                        left_stick_x.toDouble(),
                        -left_stick_y.toDouble())
                drivetrain.startMove(moveVec, moveVec.length() / Math.sqrt(2.0))
                telemetry.write("Move vector", moveVec.toString())

                //jkuDrivetrain turning
                val turningPower = Config.motorPower * Config.turnSpeed
                // The x axis of a stick on the gamepad is positive when it is to the right.
                //   Since positive power in startTurn turns the robot counter-clockwise,
                //   it may be more intuitive to invert the x value.
                // Do not turn the robot when the right stick is within a deadzone -- it may confuse the motors and twitter.
                if (right_stick_x < -0.1 || right_stick_x > 0.1) {
                    val turningValue = -right_stick_x * turningPower
                    drivetrain.startTurn(turningValue)
                    telemetry.write("Turn power", turningValue.toString())
                }
            }

            with(gamepad2) {

                // Collector unfolds when INIT is pressed
                fun openCloseBind(close: Boolean, open: Boolean, current: Boolean) =
                        current != (close != open && current != open)

                // Bucket lift -> up / down
                glypher.liftPower = dpad_up.int() * Config.bucketLiftPower -
                        dpad_down.int() * Config.bucketLiftPower

                // Bucket pour -> Right stick y, forward = vertical, backward = laid down
                glypher.bucketPourPos = Range.clip(
                        glypher.bucketPourPos - right_stick_y.toDouble() *
                                Config.bucketPourSensitivity, 0.0, 1.0)

                // Bucket clamping -> right bumper open, left bumper close
                glypher.bucketClamping =
                        openCloseBind(left_bumper, right_bumper, glypher.bucketClamping)

                // Collector toggle between idle & pulling in -> A button
                // Push out -> B button
                InputColumns.collectorIn.onChange { _, new ->
                    glypher.collectorPower = new.int() * Config.glyphCollectorPower
                }
                InputColumns.collectorOut.onChange { _, new ->
                    glypher.collectorPower = new.int() * -Config.glyphCollectorPower
                }
                // Temporary solution: TODO

                /* X button raises knocker arm (at request, Dec 17 at Gann)
                if (x)
                    Hardware.knocker.raiseArm()*/

                // Y button toggles collector hugger
                InputColumns.collectorHug.onChange { _, new ->
                    if (new)
                        glypher.collectorHugging = !glypher.collectorHugging
                }

            }

        }

        // Messages only pertain to one loop
        Hardware.telemetry.flush()
    }

    // A button toggle collectorIn
    // Up/down lift
    // right stick up / down: bucket eject
    // right bumper open, left bumper close clamp

    /**
     * A class that retains input state and performs a given callback when the state changes.
     */
    class ChangeBasedInputColumn<out T>(private val input: () -> T) {
        private var previousState = input()

        /**
         * Calls the given lambda if the input value has changed. Should be called only once during
         * each loop.
         */
        fun onChange(todo: (T, T) -> Unit): T {
            val newState = input()

            if (newState != previousState) {
                todo(previousState, newState)
                previousState = newState
            }

            return newState
        }
    }
}