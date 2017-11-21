package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

/**
 * A utility program that tests all drivetrain motors individually for potential hardware issues.
 * Does not involve IDrivetrain.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "DrivetrainTest", group = "Pragmaticos")
class DrivetrainTest : OpMode() {

    // -- MOTORS --
    var frontLeft: DcMotor? = null
    var frontRight: DcMotor? = null
    var rearLeft: DcMotor? = null
    var rearRight: DcMotor? = null

    // Quick reference for the unacquainted
    val reference: List<Pair<String, String>> = listOf(
            "Introducing" to "...",
            "THE DRIVE" to "TRAIN TESTER",
            "To test a particular motor," to "press the matching button on gamepad 1",
            "-----" to "-----",
            "Front left" to "Ⓨ",
            "Front right" to "Ⓑ",
            "Rear left" to "Ⓧ",
            "Rear right" to "Ⓐ",
            "-----" to "-----",
            "Disclaimer" to "May be addictive",
            "-----" to "-----"
    )

    override fun init() {
        with(hardwareMap.dcMotor) {
            frontLeft = get("FrontLeft")
            frontRight = get("FrontRight")
            rearLeft = get("RearLeft")
            rearRight = get("RearRight")
        }

        reference.forEach { (fst, snd) -> telemetry.addData(fst, snd) }
        telemetry.update()
    }

    override fun loop() {
        map(frontLeft, gamepad1.y)
        map(frontRight, gamepad1.b)
        map(rearLeft, gamepad1.x)
        map(rearRight, gamepad1.a)
    }

    fun valueOfBool(value: Boolean): Double = if (value) 0.4 else 0.0

    fun map(motor: DcMotor?, value: Boolean) {
        motor!!.power = valueOfBool(value)
    }
}