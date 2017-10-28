package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.io.GlyphClamp
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.firstinspires.ftc.teamcode.telemetry.Telemetry

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

    // Necessary Modules
    private var drivetrain: IDrivetrain? = null
    private var _telemetry: ITelemetry? = null
    private var clamp: GlyphClamp? = null

    // I/O Devices that are irrelevant to the drivetrain
    private var leftClamp: Servo? = null
    private var rightClamp: Servo? = null

    override fun init() {

        // Initialize systems
        if (booleanArrayOf(
                initModules(),
                initIO()
        ).contains(false)) {
            _telemetry?.fatal("Initialization has failed")
            this.requestOpModeStop()
            return
        }

        clamp = GlyphClamp(leftClamp!!, rightClamp!!, _telemetry!!)
    }

    override fun loop() {

    }

    fun initModules(): Boolean {
        this._telemetry = Telemetry(this.telemetry)
        try {
            this.drivetrain = Drivetrain(Config.motorPower, mapOf(
                    IDrivetrain.MotorPtr.FRONT_LEFT to hardwareMap.dcMotor.get("FrontLeft"),
                    IDrivetrain.MotorPtr.FRONT_RIGHT to hardwareMap.dcMotor.get("FrontRight"),
                    IDrivetrain.MotorPtr.REAR_LEFT to hardwareMap.dcMotor.get("RearLeft"),
                    IDrivetrain.MotorPtr.REAR_RIGHT to hardwareMap.dcMotor.get("RearRight")
            ))
        } catch (exc: IllegalArgumentException) {
            _telemetry?.fatal("(14) ${exc.message}")
            return false
        }
        return true
    }

    fun initIO(): Boolean {
        try {

            // GlyphClamp servos
            this.leftClamp = hardwareMap.servo.get("LeftClamp")
            this.rightClamp = hardwareMap.servo.get("RightClamp")

            // GlyphClampElevator servo
            // TODO(waiting) Implementation of design

        } catch (exc: IllegalArgumentException) {
            _telemetry?.fatal("(14) ${exc.message}")
            return false
        }
        return true
    }
}