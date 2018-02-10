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
    private lateinit var bucketLiftLeft: DcMotor
    private lateinit var bucketLiftRight: DcMotor
    private lateinit var bucketPour: Servo
    private lateinit var bucketClamp: Servo
    private lateinit var collectorFold: Servo

    // -- Default power values --
    private val LIFT_POWER = 0.2
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
            bucketLiftLeft = get("BucketLiftLeft")
            bucketLiftRight = get("BucketLiftRight")
        }
        with(hardwareMap.servo) {
            bucketPour = get("BucketPour")
            bucketClamp = get("BucketClamp")
            collectorFold = get("CollectorFold")
        }
        telemetry.addData("Output devices", "Recognized")

        // Inverse direction of mirrored motors
        flywheelLeft.direction = DcMotorSimple.Direction.REVERSE
        bucketLiftRight.direction = DcMotorSimple.Direction.REVERSE

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
                on(dpad_up, LIFT_POWER) {
                    bucketLiftLeft.power = it
                    bucketLiftRight.power = it
                }
                on(dpad_down, -LIFT_POWER) {
                    bucketLiftLeft.power = it
                    bucketLiftRight.power = it
                }
            }
            // TODO add bucket pouring pos back
            // Perhaps could be more sophisticated and pleasing to the eye
        }
    }

    fun on(input: Boolean, power: Double, todo: (Double) -> Unit) = todo(if (input) power else 0.0)
}