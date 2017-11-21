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
        val clampLiftPower = 0.3
    }

    lateinit private var bot: Hardware
    lateinit private var padListener: GamepadListener

    override fun init() {
        // Initialize systems
        bot = Hardware(this, Config.motorPower)
        padListener = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)
    }

    override fun init_loop() {
        padListener.update()
    }

    override fun loop() {
        // Gamepad mappings
        with(bot) {
            fun Boolean.int() = if (this) 1.0 else 0.0

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
                // Glyph clamp
                fun clampBind(close: Boolean, open: Boolean, current: Boolean)
                        = current != (close != open && current != open)
                clamp.leftArm = clampBind(
                        left_bumper, left_trigger > 0.3, clamp.leftArm)
                clamp.rightArm = clampBind(
                        right_bumper, right_trigger > 0.3, clamp.rightArm)

                // Glyph clamp lift
                val liftPower = (dpad_up.int() * Config.clampLiftPower) -
                        (dpad_down.int() * Config.clampLiftPower)
                clamp.liftPower = liftPower
                telemetry.write("Lift power", liftPower.toString())
            }

        }

        // Messages only pertain to one loop
        bot.telemetry.flush()
    }
}