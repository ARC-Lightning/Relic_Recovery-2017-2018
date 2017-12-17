package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range

/**
 * Provides the ability to test individual servos and survey their positions.
 * Before running this test, set hardware device "Test_Servo" to the desired port in configuration.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name = "ServoDirectTest", group = "Pragmaticos")
class ServoDirectTest : OpMode() {
    private val TICK_CHANGE = 0.01

    lateinit var servo: Servo

    override fun init() {
        servo = hardwareMap.servo.get("Test_Servo")
    }

    override fun loop() {
        with(gamepad1) {
            var nextPosition = servo.position

            if (left_bumper)
                nextPosition += TICK_CHANGE

            if (right_bumper)
                nextPosition -= TICK_CHANGE

            nextPosition = Range.clip(nextPosition, 0.0, 1.0)
            servo.position = nextPosition

            telemetry.addData("Current Servo Position", nextPosition)
            telemetry.update()
        }
    }
}