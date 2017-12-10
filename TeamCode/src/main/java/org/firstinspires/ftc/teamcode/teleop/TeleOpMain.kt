package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
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
        val motorPower = 0.8
        val turnSpeed = 0.6
        val glyphCollectorPower = 0.3
        val bucketLiftPower = 0.2
        val stickAxisToBinaryThreshold = 0.3
    }

    object InputColumns {
        lateinit var collector: ToggleInputColumn
    }

    lateinit private var padListener: GamepadListener

    override fun init() {
        // Initialize systems
        Hardware.init(this, Config.motorPower)
        padListener = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)

        // Collector flywheel mechanism will be folded - release it when initializing
        Hardware.glypher.unfoldCollector()
    }

    override fun init_loop() {
        padListener.update()

        // Initialize toggle input surfaces, which currently includes collector use (A, gamepad 2)
        InputColumns.collector = ToggleInputColumn { gamepad2.a }

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

                // Drivetrain turning
                val turningPower = Config.motorPower * Config.turnSpeed
                // The x axis of a stick on the gamepad is positive when it is to the right.
                //   Since positive power in startTurn turns the robot counter-clockwise,
                //   it may be more intuitive to invert the x value.
                val turningValue = -right_stick_x * turningPower
                drivetrain.startTurn(turningValue)
                telemetry.write("Turn power", turningValue.toString())
            }

            with(gamepad2) {

                // Collector unfolds when INIT is pressed
                fun openCloseBind(close: Boolean, open: Boolean, current: Boolean) =
                        current != (close != open && current != open)

                // Bucket lift -> up / down
                glypher.liftPower = dpad_up.int() * Config.bucketLiftPower -
                        dpad_down.int() * Config.bucketLiftPower

                // Bucket pour -> Right stick y, forward = vertical, backward = laid down
                glypher.bucketPouring =
                        stickAxisToBinary(-right_stick_y.toDouble(), glypher.bucketPouring)

                // Bucket clamping -> right bumper open, left bumper close
                glypher.bucketClamping =
                        openCloseBind(left_bumper, right_bumper, glypher.bucketClamping)

                // Collector toggle between idle & pulling in -> A button
                InputColumns.collector.onChange { _, new ->
                    glypher.collectorPower = new.int() * Config.glyphCollectorPower
                }

            }

        }

        // Messages only pertain to one loop
        Hardware.telemetry.flush()
    }

    // A button toggle collector
    // Up/down lift
    // right stick up / down: bucket eject
    // right bumper open, left bumper close clamp

    class ToggleInputColumn(val input: () -> Boolean) {
        var previousState = input()

        fun onChange(todo: (Boolean, Boolean) -> Unit): Boolean {
            val newState = input()

            if (newState != previousState) {
                todo(previousState, newState)
                previousState = newState
            }

            return newState
        }
    }
}