package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.config.ConfigFile
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
    class Config {
        private val file =
                ConfigFile("TeleOp/config.properties")

        val motorPower          = file.getDouble("MotorPower")
        val turnSpeed           = file.getDouble("TurnSpeed")
        val glyphCollectorPower = file.getDouble("GlyphCollectorPower")
        val pourSensitivity     = file.getDouble("PourSensitivity")
        val rectSensitivity     = file.getDouble("RectifierSensitivity")

    }

    object InputColumns {
        lateinit var collectorIn: ChangeBasedInputColumn<Boolean>
        lateinit var collectorOut: ChangeBasedInputColumn<Boolean>
        lateinit var precisionMove: ChangeBasedInputColumn<Boolean>
    }

    private lateinit var config: Config

    override fun init() {
        // Initialize systems
        config = Config()
        Hardware.init(this, config.motorPower)

        // Initialize toggle input surfaces, which currently includes collectorIn use (A, gamepad 2)
        InputColumns.collectorIn = ChangeBasedInputColumn { gamepad2.a }
        InputColumns.collectorOut = ChangeBasedInputColumn { gamepad2.b }
        InputColumns.precisionMove = ChangeBasedInputColumn { gamepad1.x }

        // Lock the jewel arm
        Hardware.knocker.raiseArm()
    }

    override fun loop() {
        // Gamepad mappings
        with(Hardware) {
            fun Boolean.int() = if (this) 1.0 else 0.0

            with(gamepad1) {
                // Drivetrain movement
                val moveVec = Vector2D(
                        left_stick_x.toDouble(),
                        -left_stick_y.toDouble())
                telemetry.write("Move vector", moveVec.toString())

                val turnPower = right_stick_x * config.motorPower * config.turnSpeed
                telemetry.write("Turn power", turnPower.toString())

                drivetrain.actuate(moveVec, moveVec.length() / Math.sqrt(2.0) * config.motorPower,
                        true, turnPower)

                // Bumpers -> rectifier
                glypher.rectifierPos = Range.clip(
                        glypher.rectifierPos + (left_trigger - right_trigger) *
                                config.rectSensitivity, 0.0, 1.0)

                if (back) {
                    Hardware.knocker.raiseArm()
                }

                // X button -> toggle -> precise movement
                InputColumns.precisionMove.onChange { _, new -> if (new)
                    drivetrain.isUsingPrecisePower = !drivetrain.isUsingPrecisePower
                }

            }

            with(gamepad2) {

                fun openCloseBind(close: Boolean, open: Boolean, current: Boolean) =
                        current != (close != open && current != open)

                // Bucket pour -> Right stick y, forward = vertical, backward = laid down
                glypher.bucketPourPos = Range.clip(
                        glypher.bucketPourPos - right_stick_y.toDouble() *
                                config.pourSensitivity, 0.0, 1.0)

                // Collector toggle between idle & pulling in -> A button
                // Push out -> B button
                InputColumns.collectorIn.onChange { _, new ->
                    glypher.collectorPower = new.int() * config.glyphCollectorPower
                }
                InputColumns.collectorOut.onChange { _, new ->
                    glypher.collectorPower = new.int() * -config.glyphCollectorPower
                }
                // Temporary solution: TODO
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