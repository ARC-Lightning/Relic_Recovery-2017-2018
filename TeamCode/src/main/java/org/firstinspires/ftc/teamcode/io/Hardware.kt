package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.firstinspires.ftc.teamcode.telemetry.Telemetry

/**
 * Defines modules and hardware necessary to run both Autonomous and TeleOp.
 * Includes all code that is used in both modes.
 * Both AutoMain and TeleOpMain should have an instance of this class.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class Hardware(
        val drivetrain: IDrivetrain,
        val telemetry: ITelemetry,
        val clamp: GlyphClamp) {

    companion object {

        // This function was put into a companion object because the return value is nullable.
        // Secondary constructors are required to delegate to the primary one and cannot return null.
        fun new(opMode: OpMode, motorPower: Double): Hardware? {
            val telemetry = Telemetry(opMode.telemetry)
            try {

                // Mecanum wheels
                val drivetrain = Drivetrain(motorPower, mapOf(
                        IDrivetrain.MotorPtr.FRONT_LEFT to opMode.hardwareMap.dcMotor.get("FrontLeft"),
                        IDrivetrain.MotorPtr.FRONT_RIGHT to opMode.hardwareMap.dcMotor.get("FrontRight"),
                        IDrivetrain.MotorPtr.REAR_LEFT to opMode.hardwareMap.dcMotor.get("RearLeft"),
                        IDrivetrain.MotorPtr.REAR_RIGHT to opMode.hardwareMap.dcMotor.get("RearRight")
                ))

                // GlyphClamp servos
                val leftClamp = opMode.hardwareMap.servo.get("LeftClamp")
                val rightClamp = opMode.hardwareMap.servo.get("RightClamp")

                // GlyphClampElevator servo
                // TODO(waiting) Implementation of design

                // JewelKnocker
                // TODO(waiting) Ian's Implementation

                // Grand finale
                return Hardware(
                        drivetrain,
                        telemetry,
                        GlyphClamp(leftClamp, rightClamp, telemetry))

            } catch (exc: IllegalArgumentException) {
                telemetry.fatal("(14) ${exc.message}")
                opMode.requestOpModeStop()
                return null
            }
        }

    }
}