package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "ClampTest", group = "Pragmaticos")
class ClampTest : OpMode() {

    // -- SERVOS --
    var leftArm: Servo? = null
    var rightArm: Servo? = null

    override fun init() {
        with(hardwareMap.servo) {
            leftArm = get("LeftClamp")
            rightArm = get("RightClamp")
        }
    }

    override fun loop() {
        leftArm!!.position = gamepad1.left_trigger.toDouble()
        rightArm!!.position = gamepad1.right_trigger.toDouble()
    }
}

