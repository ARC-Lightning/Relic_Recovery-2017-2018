package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 * Provides straightforward bindings from hardware to gamepad, enabling the testing of the glyph
 * mechanism. Little to no abstraction is present between the gamepad API and the DcMotor/Servo API.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "GlyphTest", group = "Pragmaticos")
class GlyphTest : OpMode() {

    // -- Hardware output devices --
    private lateinit var flywheelLeft: DcMotor
    private lateinit var flywheelRight: DcMotor
    private lateinit var bucketPour: Servo

    // -- Default power values --
    private val POUR_ADDEND = 0.02
    private val FLYWHEEL_POWER = 0.6

    private val references = listOf(
            "THE" to "GLYPH TEST",
            "Ⓑ" to "Right flywheel",
            "Ⓧ" to "Left flywheel",
            "D-pad ▲" to "Pour bucket",
            "D-pad ▼" to "Unpour bucket",
            "Left trigger" to "Left rectifier"
    )

    override fun init() {
        with(hardwareMap.dcMotor) {
            flywheelLeft = get("FlywheelLeft")
            flywheelRight = get("FlywheelRight")
        }
        bucketPour = hardwareMap.servo.get("BucketPour")
        telemetry.addData("Output devices", "Recognized")

        // Inverse direction of mirrored motors
        flywheelLeft.direction = DcMotorSimple.Direction.REVERSE

        references.forEach { (fst, snd) -> telemetry.addData(fst, snd) }
        telemetry.update()
    }

    override fun loop() {
        with(gamepad1) {
            on(b, FLYWHEEL_POWER) {
                flywheelRight.power = it
            }
            on(x, FLYWHEEL_POWER) {
                flywheelLeft.power = it
            }
            if (!dpad_down || !dpad_up) {
                on(dpad_up, POUR_ADDEND) {
                    bucketPour.position += it
                }
                on(dpad_down, -POUR_ADDEND) {
                    bucketPour.position += it
                }
            }
            // Perhaps could be more sophisticated and pleasing to the eye
        }
    }

    fun on(input: Boolean, power: Double, todo: (Double) -> Unit) = todo(if (input) power else 0.0)
}