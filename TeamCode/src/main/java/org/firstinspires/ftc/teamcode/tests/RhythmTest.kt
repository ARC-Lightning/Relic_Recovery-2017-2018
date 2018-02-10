package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 * A rhythmic test of the BucketPour servos and the collector motors.
 */
@Autonomous(name = "Rhythmic", group = "Pragmaticos")
class RhythmTest : LinearOpMode() {

    /* ELECTRONICS */
    lateinit var leftPour: Servo
    lateinit var rightPour: Servo
    lateinit var leftCollector: DcMotor
    lateinit var rightCollector: DcMotor

    companion object {
        /* POSITIONS and POWER */
        const val POUR_DOWN = 0.0
        const val POUR_UP = 0.2
        const val COLLECTOR_POWER = 0.4

        /* RHYTHM SPEED */
        const val BPM = 440.0
    }

    /* RHYTHM */
    val pourPhrase = arrayOf(4.0, 2.0, 2.0, 4.0, 6.0)
    val collectorPhrase = arrayOf(4.0, 4.0)

    override fun runOpMode() {
        // Define electronics
        with(hardwareMap) {
            leftPour = servo.get("BucketPour")
            rightPour = servo.get("OffsideBucketPour")

            leftCollector = dcMotor.get("FlywheelLeft")
            rightCollector = dcMotor.get("FlywheelRight")
        }

        // Follow symmetry
        rightPour.direction = Servo.Direction.REVERSE
        rightCollector.direction = DcMotorSimple.Direction.REVERSE

        telemetry.isAutoClear = false

        // Initialize instruments
        val drum = DrumAlternator(this)

        waitForStart()

        // Run through phrases
        runPhrase(drum::alternate, pourPhrase)
        runPhrase(this::activateCollectors, collectorPhrase)
    }

    fun activateCollectors() {
        leftCollector.power = COLLECTOR_POWER
        rightCollector.power = COLLECTOR_POWER
        sleep(100)
        leftCollector.power = 0.0
        rightCollector.power = 0.0
    }

    fun runPhrase(func: () -> Unit, phrase: Array<Double>) {
        for (beat in phrase) {
            func()
            sleep(Math.round(beat / (BPM / 60_000.0)))
        }
    }

    class DrumAlternator(val opMode: RhythmTest) {
        var isLeftDown: Boolean = false

        fun alternate() {
            opMode.leftPour.position = if (isLeftDown) POUR_UP else POUR_DOWN
            opMode.rightPour.position = if (isLeftDown) POUR_DOWN else POUR_UP

            isLeftDown = !isLeftDown
        }
    }
}