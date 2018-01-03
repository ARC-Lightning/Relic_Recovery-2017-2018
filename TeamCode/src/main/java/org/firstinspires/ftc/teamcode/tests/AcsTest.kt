package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.io.Hardware
import org.locationtech.jts.algorithm.Angle
import org.locationtech.jts.math.Vector2D

/**
 * Created by michael on 12/17/17.
 * TODO document
 */
@TeleOp(name = "AcsTest", group = "Pragmaticos")
class AcsTest : LinearOpMode() {

    override fun runOpMode() {
        Hardware.init(this, 0.7)

        waitForStart()

        telemetry.addData("Y axis", "X axis: Diagonal: Turning")
        telemetry.update()

        Hardware.telemetry.autoUpdate = true

        with(Hardware.drivetrain) {
            move(Vector2D(0.0, 1.0))
            move(Vector2D(1.0, 0.0))
            move(Vector2D(1.0, 2.0))
            turn(Angle.toRadians(90.0))
        }
    }
}