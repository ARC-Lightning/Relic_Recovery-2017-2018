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
    }

    private var bot: Hardware? = null
    private var padListener: GamepadListener? = null

    override fun init() {

        // Initialize systems
        bot = Hardware.new(this, Config.motorPower)
        if (bot == null) {
            // The error is produce by Hardware itself, no telemetry necessary
            return
        }
        padListener = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)
    }

    override fun init_loop() {
        padListener!!.update()
    }

    override fun loop() {
        // Gamepad mappings
        with(bot!!) {

            // Drivetrain movement
            val moveVec = Vector2D(gamepad1.left_stick_x.toDouble(), -gamepad1.left_stick_y.toDouble())
            drivetrain.startMove(moveVec, moveVec.length() / Math.sqrt(2.0))
            telemetry.write("Move vector", moveVec.toString())

            // Drivetrain turning
            val turningPower = Config.motorPower * Config.turnSpeed
            fun Boolean.int() = if (this) 1.0 else 0.0
            val turningValue = (gamepad1.dpad_right.int() * turningPower) - (gamepad1.dpad_left.int() * turningPower)
            drivetrain.startTurn(turningValue)
            telemetry.write("Turn power", turningValue.toString())

            // Glyph clamp
            fun clampBind(close: Boolean, open: Boolean, current: Boolean)
                    = current != (close != open && current != open)
            clamp.leftArm = clampBind(
                    gamepad1.left_bumper, gamepad1.left_trigger > 0.3, clamp.leftArm)
            clamp.rightArm = clampBind(
                    gamepad1.right_bumper, gamepad1.right_trigger > 0.3, clamp.rightArm)


        }

        // Messages only pertain to one loop
        bot!!.telemetry.clear()
    }
}