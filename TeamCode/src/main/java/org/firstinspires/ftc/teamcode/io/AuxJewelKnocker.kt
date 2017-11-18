package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.TeamColor
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.locationtech.jts.math.Vector2D

/**
 * An auxiliary implementation of the IJewelKnocker interface for backup use.
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
        val raisedPosition: Double = 0.0
        val loweredPosition: Double = 0.6

        // Color sensor data decision threshold
        val colorThreshold = 150

        // Direction in which the color sensor is facing (toward front of robot or back?)
        val isColorSensorFacingFront = true

        // How far it should go when knocking a jewel off (in inches)
        val knockDistance = 2.0
    }

    override fun lowerArm() {
        arm.position = loweredPosition
    }

    override fun detect(): TeamColor? {
        val isBlue = color.blue() > colorThreshold
        val isRed = color.red() > colorThreshold

        return when {
            isBlue && !isRed -> TeamColor.BLUE
            isRed && !isBlue -> TeamColor.RED
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