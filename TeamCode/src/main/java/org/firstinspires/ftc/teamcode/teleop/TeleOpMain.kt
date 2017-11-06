package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.io.Hardware

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
        val motorPower = 0.7
    }

    private var bot: Hardware? = null

    override fun init() {

        // Initialize systems
        bot = Hardware.new(this, Config.motorPower)
        if (bot == null) {
            return
        }

    }

    override fun loop() {

        // Is init successful?
        if (bot == null) {
            telemetry.addData("--- FATAL", "ERROR ---")
            telemetry.addData("Initialization", "unsuccessful")
            telemetry.update()
            this.requestOpModeStop()
            return
        }

        // Gamepad mappings
        with(bot!!) {
            clamp.leftArm = getClampValue(clamp.leftArm, gamepad1.left_trigger > 0.3, gamepad1.left_bumper)
            clamp.rightArm = getClampValue(clamp.rightArm, gamepad1.right_trigger > 0.3, gamepad1.right_bumper)
        }
    }

    /**
     * Deduce the current desired status of a given servo given the bumper and trigger values.
     *
     * Truth table:
     * Current  Retractor   Activator   RETURN VALUE
     * T        T           T           T
     * T        T           F           F
     * T        F           T           T
     * T        F           F           T
     * F        T           T           F
     * F        T           F           F
     * F        F           T           T
     * F        F           F           F
     *
     * @param current The current status of the servo, true for clamping
     * @param retractor True if the corresponding un-clamping button is being triggered
     * @param activator True if the corresponding clamping button is being triggered
     * @return The new desired status of the servo
     */
    private fun getClampValue(current: Boolean, activator: Boolean, retractor: Boolean): Boolean =
            current != (retractor != activator && current != activator)
}