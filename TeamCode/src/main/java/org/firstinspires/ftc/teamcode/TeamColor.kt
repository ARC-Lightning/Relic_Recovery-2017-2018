package org.firstinspires.ftc.teamcode

/**
 * Provides data types that represent the color of a team in FTC.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
enum class TeamColor {
    RED,
    BLUE;

    companion object {
        fun oppositeOf(color: TeamColor) = when (color) {
            RED -> BLUE
            BLUE -> RED
        }
    }
}
