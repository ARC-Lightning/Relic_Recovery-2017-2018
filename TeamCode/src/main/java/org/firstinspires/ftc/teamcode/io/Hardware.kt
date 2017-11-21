package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.firstinspires.ftc.teamcode.telemetry.Telemetry

/**
 * Defines modules and hardware necessary to run both Autonomous and TeleOp.
 * Includes all code that is used in both modes.
 * Both AutonomousMain and TeleOpMain should have an instance of this class.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class Hardware(
        opMode: OpMode,
        motorPower: Double
) {

    val drivetrain: IDrivetrain
    val telemetry: ITelemetry
    val clamp: IGlyphClamp
    val knocker: IJewelKnocker

    init {
        telemetry = Telemetry(opMode.telemetry)
        try {

            // NOTE: A with block is impractical in this situation due to prohibited initialization
            // of vals in a block. Kotlinc will be dissatisfied.

            // Mecanum wheels
            drivetrain = Drivetrain(motorPower, mapOf(
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

            // GlyphClamp instance
            clamp = GlyphClamp(
                    leftClamp,
                    rightClamp,
                    clampLift,
                    telemetry
            )

            // JewelKnocker
            knocker = AuxJewelKnocker(
                    telemetry,
                    drivetrain,
                    color = opMode.hardwareMap.colorSensor.get("JewelSensor"),
                    arm = opMode.hardwareMap.servo.get("JewelArm"))

            instance = this

        } catch (exc: Exception) {
            telemetry.fatal(
                    "Failed to initialize hardware: ${exc.message ?: "the robot, too, doesn't know why"}")
            opMode.requestOpModeStop()
            throw RuntimeException(exc)
        }

    }

    companion object {
        /**
         * A singleton copy of the active hardware.
         * This should never be referenced in "static" blocks, variable declarations, or any other
         *   execution context before the init() of an OpMode (i.e. the creation of a Hardware instance)
         */
        var instance: Hardware? = null
    }
}