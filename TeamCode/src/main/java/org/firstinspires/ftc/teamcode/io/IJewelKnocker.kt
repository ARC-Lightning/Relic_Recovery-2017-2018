package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.TeamColor

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

    fun detect(): TeamColor

    fun removeJewel(towardCorner: Boolean)
}