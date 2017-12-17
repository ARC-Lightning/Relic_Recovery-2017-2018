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
        val raisedPosition: Double = 0.17
        val loweredPosition: Double = 0.6

        // Color sensor data decision threshold
        val colorThreshold = 6

        // Direction in which the color sensor is facing (toward front of robot or back?)
        val isColorSensorFacingFront = false

        // How far it should go when knocking a jewel off (in inches)
        val knockDistance = 3.0

    }

    override fun lowerArm() {
        arm.position = loweredPosition
    }

    override fun detect(): AllianceColor? {
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
    }
}