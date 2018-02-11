package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.AllianceColor

/**
 * The knocker of jewels, implemented for use in Autonomous.
 * Includes all functions necessary to control the knocker.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
interface IJewelKnocker {

    // I/O requirements
    val color: ColorSensor
    val arm: Servo

    /**
     * Lowers the knocker arm on which the color sensor is mounted.
     * Blocks until the arm has finished moving.
     */
    fun lowerArm()

    /**
     * Reads from the ColorSensor and returns the TeamColor to which the values are closest.
     * @return The team color at which the color sensor seems to be looking, or null if the data is confusing.
     */
    fun detect(): AllianceColor?

    /**
     * Moves or turns the robot in a way that knocks off a jewel in the direction specified by the
     * parameter, then returns to its position before the knock.
     * Blocks until the arm has finished moving.
     *
     * @param towardDetectedJewel Whether the jewel whose color is detected by color sensor shall be knocked off
     */
    fun removeJewel(towardDetectedJewel: Boolean)

    /**
     * Raises the knocker arm on which the color sensor is mounted.
     * Blocks until the arm has finished moving.
     */
    fun raiseArm()

    // The ideal scoring process:
    //  - lowerArm()
    //  - detect() == DynamicConfig.team ?
    //  - removeJewel([result above])
    //  - raiseArm()
}