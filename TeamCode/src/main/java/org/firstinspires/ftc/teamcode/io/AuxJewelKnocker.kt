package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.locationtech.jts.math.Vector2D

/**
 * An auxiliary implementation of the IJewelKnocker interface for backup use.
 * For documentation on the individual methods, see IJewelKnocker.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class AuxJewelKnocker(val telemetry: ITelemetry,
                      val drivetrain: IDrivetrain,
                      override val color: ColorSensor,
                      override val arm: Servo) : IJewelKnocker {

    // CONFIGURATION
    companion object {

        // Servo positions
        const val raisedPosition: Double = 1.0
        const val loweredPosition: Double = 0.38

        // Color sensor data decision threshold
        const val colorThreshold = 6

        // Direction in which the color sensor is facing (toward front of robot or back?)
        const val isColorSensorFacingFront = false

        // How far it should go when knocking a jewel off (in inches)
        const val knockDistance = 3.0

        // How long does it take for the servo to go from up/down to down/up?
        const val servoDelay = 500
    }

    init {
        // Lock the servo upon start
        raiseArm()
    }

    override fun lowerArm() {
        arm.position = loweredPosition
        Thread.sleep(servoDelay.toLong())
    }

    override fun detect(): AllianceColor? {
        // Enable LED
        color.enableLed(true)

        // Allow values to stabilize
        Thread.sleep(600)

        val isBlue = color.blue() > colorThreshold
        val isRed = color.red() > colorThreshold

        return when {
            isBlue && !isRed -> AllianceColor.BLUE
            isRed && !isBlue -> AllianceColor.RED
            else -> {
                telemetry.error("ColorSensor data is confusing")
                telemetry.data("RED", color.red())
                telemetry.data("BLUE", color.blue())
                null
            }
        }
    }

    override fun removeJewel(towardDetectedJewel: Boolean) {
        val initialVec = Vector2D(
                0.0,
                (if (towardDetectedJewel == isColorSensorFacingFront) 1 else -1) * knockDistance)

        drivetrain.move(initialVec)
        drivetrain.move(initialVec.negate())
    }

    override fun raiseArm() {
        arm.position = raisedPosition
        Thread.sleep(servoDelay.toLong())
    }
}