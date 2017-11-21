package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

/**
 * A utility program that tests the operation of the glyph clamp.
 * For controls, see the `reference' field below.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "ClampTest", group = "Pragmaticos")
class ClampTest : OpMode() {

    // -- SERVOS --
    var leftArm: Servo? = null
    var rightArm: Servo? = null

    // -- LIFT --
    var lift: DcMotor? = null

    // Reference for the unacquainted
    val reference = listOf(
            "Introducing" to "...",
            "THE CLAMP" to "TESTER",
            "-----" to "-----",
            "To control the left clamp arm" to "Press the left trigger ('LT')",
            "To control the right clamp arm" to "Press the right trigger ('RT')",
            "To control the lift" to "Press ▲ for up and ▼ for down on the left half of the gamepad",
            "-----" to "-----",
            "Disclaimer" to "May be addictive",
            "-----" to "-----"
    )

    override fun init() {
        with(hardwareMap) {
            leftArm = servo.get("LeftClamp")
            rightArm = servo.get("RightClamp")
            lift = dcMotor.get("ClampLift")
        }

        // Lift holds mass, therefore it should be locked when zero-powered
        lift!!.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        reference.forEach { (fst, snd) -> telemetry.addData(fst, snd) }
        telemetry.update()
    }

    override fun loop() {
        leftArm!!.position = gamepad1.left_trigger.toDouble()
        rightArm!!.position = 1.0 - gamepad1.right_trigger.toDouble()

        when {
            gamepad1.dpad_up -> lift!!.power = 0.3
            gamepad1.dpad_down -> lift!!.power = -0.3
            else -> lift!!.power = 0.0
        }
    }
}

