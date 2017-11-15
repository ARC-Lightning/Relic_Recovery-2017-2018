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
class Hardware internal constructor(
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

                // GlyphClampElevator motor
                val clampLift = opMode.hardwareMap.dcMotor.get("ClampLift")

                // JewelKnocker
                // TODO(waiting) Ian's Implementation

                // Grand finale
                val hw = Hardware(
                        drivetrain,
                        telemetry,
                        GlyphClamp(leftClamp, rightClamp, clampLift, telemetry))
                instance = hw
                return hw

            } catch (exc: Exception) {
                telemetry.fatal("Failed to initialize hardware: ${exc.message ?: "the robot, too, doesn't know why"}")
                opMode.requestOpModeStop()
                throw RuntimeException(exc)
            }
        }

        /**
         * A singleton copy of the active hardware.
         * This should never be referenced in "static" blocks, variable declarations, or any other
         *   execution context before the init() of an OpMode (i.e. the creation of a Hardware instance)
         */
        var instance: Hardware? = null
    }
}