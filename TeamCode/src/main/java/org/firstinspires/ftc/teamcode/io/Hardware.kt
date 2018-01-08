package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotorSimple
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
object Hardware {

    lateinit var opMode: OpMode
    var motorPower: Double = 0.8

    lateinit var drivetrain: IDrivetrain
    lateinit var telemetry: ITelemetry
    lateinit var glypher: GlyphManipulator
    // TODO fix usages
    lateinit var knocker: IJewelKnocker

    // LATEINIT - OpModes MUST initialize ASAP using this function, otherwise expect NPEs!
    fun init(_opMode: OpMode, _motorPower: Double) {

        // Assign to lateinit (has to stay above any other initialization procedure)
        opMode = _opMode
        motorPower = _motorPower

        telemetry = Telemetry(opMode.telemetry)

        try {
            with(opMode.hardwareMap) {
                // Mecanum wheels
                drivetrain = Drivetrain(motorPower, mapOf(
                        IDrivetrain.MotorPtr.FRONT_LEFT to dcMotor.get("FrontLeft"),
                        IDrivetrain.MotorPtr.FRONT_RIGHT to dcMotor.get("FrontRight"),
                        IDrivetrain.MotorPtr.REAR_LEFT to dcMotor.get("RearLeft"),
                        IDrivetrain.MotorPtr.REAR_RIGHT to dcMotor.get("RearRight")
                ))

                // GlyphManipulator instance
                glypher = GlyphManipulator(
                        collectors = setOf(
                                dcMotor.get("FlywheelLeft"),
                                dcMotor.get("FlywheelRight")
                        ),
                        bucketLift = dcMotor.get("BucketLift"),
                        bucketPour = servo.get("BucketPour"),
                        bucketClamp = servo.get("BucketClamp"),
                        collectorFolder = servo.get("CollectorFold"),
                        collectorHugger = servo.get("CollectorHug"))

                // Reverse direction of FlywheelRight motor due to symmetry
                dcMotor.get("FlywheelRight").direction = DcMotorSimple.Direction.REVERSE

                /* JewelKnocker
                knocker = AuxJewelKnocker(
                        telemetry,
                        drivetrain,
                        color = colorSensor.get("JewelSensor"),
                        arm = servo.get("JewelArm"))*/
            }

        } catch (exc: Exception) {
            telemetry.fatal(
                    "Failed to initialize hardware: ${exc.message ?: "the robot, too, doesn't know why"}")
            opMode.requestOpModeStop()
            throw RuntimeException(exc)
        }

    }

}