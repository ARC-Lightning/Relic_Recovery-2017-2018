package org.firstinspires.ftc.teamcode

/**
 * Provides data types that represent the color of a team in FTC.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
enum class AllianceColor {
    RED,
    BLUE;

    companion object {
        fun oppositeOf(color: AllianceColor) = when (color) {
            RED -> BLUE
            BLUE -> RED
        }
    }
}