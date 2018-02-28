package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.AllianceColor
import org.firstinspires.ftc.teamcode.config.ConfigUser
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
    class Config : ConfigUser("JewelKnocker/config.properties") {
        val servoDelay = file.getInteger("ServoDelay")

        // Servo positions
        val raisedPosition = file.getDouble("RaisedPosition")
        val loweredPosition = file.getDouble("LoweredPosition")

        // When to be certain that it is one color?
        val colorThreshold = file.getInteger("ColorThreshold")

        // Facing direction of the color sensor
        val isColorSensorFacingFront = file.getBoolean("IsColorSensorFacingFront")

        // How far to go when knocking a jewel off?
        val knockDistance = file.getDouble("KnockDistance")
    }
    val config = Config()

    init {
        // Lock the servo upon start
        raiseArm()
    }

    override fun lowerArm() {
        arm.position = config.loweredPosition
        Thread.sleep(config.servoDelay.toLong())
    }

    override fun detect(): AllianceColor? {
        // Enable LED
        color.enableLed(true)

        // Allow values to stabilize
        Thread.sleep(600)

        val isBlue = color.blue() > config.colorThreshold
        val isRed = color.red() > config.colorThreshold

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
                (if (towardDetectedJewel == config.isColorSensorFacingFront)
                        1 else -1) * config.knockDistance)

        drivetrain.move(initialVec)
        drivetrain.move(initialVec.negate())
    }

    override fun raiseArm() {
        arm.position = config.raisedPosition
        Thread.sleep(config.servoDelay.toLong())
    }
}