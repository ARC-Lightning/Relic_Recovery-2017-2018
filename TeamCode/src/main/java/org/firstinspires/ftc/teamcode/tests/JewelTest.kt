package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo

/**
 * TODO document
 */
@TeleOp(name = "JewelTest", group = "Pragmaticos")
class JewelTest : OpMode() {
    lateinit var color: ColorSensor
    lateinit var arm: Servo

    override fun init() {
        with(hardwareMap) {
            color = colorSensor.get("JewelSensor")
            arm = servo.get("JewelArm")
        }
    }

    override fun loop() {
        arm.position = gamepad1.left_trigger.toDouble()
        when {
            color.red() > color.blue() -> telemetry.addData("I saw", "Red")
            color.blue() > color.red() -> telemetry.addData("I saw", "Blue")
            else -> telemetry.addData("I didn't see", "Anything")
        }
        telemetry.update()
    }
}