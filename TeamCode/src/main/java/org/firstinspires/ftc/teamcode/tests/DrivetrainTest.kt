package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

/**
 * Created by michael on 11/9/17.
 */
@TeleOp(name = "DrivetrainTest", group = "Pragmaticos")
class DrivetrainTest : OpMode() {

    // -- MOTORS --
    var frontLeft: DcMotor? = null
    var frontRight: DcMotor? = null
    var rearLeft: DcMotor? = null
    var rearRight: DcMotor? = null

    override fun init() {
        with(hardwareMap.dcMotor) {
            frontLeft = get("FrontLeft")
            frontRight = get("FrontRight")
            rearLeft = get("RearLeft")
            rearRight = get("RearRight")
        }
    }

    override fun loop() {
        map(frontLeft, gamepad1.y)
        map(frontRight, gamepad1.b)
        map(rearLeft, gamepad1.x)
        map(rearRight, gamepad1.a)
    }

    fun valueOfBool(value: Boolean): Double = if (value) 0.2 else 0.0

    fun map(motor: DcMotor?, value: Boolean) {
        motor!!.power = valueOfBool(value)
    }
}