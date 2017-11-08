package org.firstinspires.ftc.teamcode.acs

import org.locationtech.jts.geom.Coordinate

/**
 * Defines a position in which the robot can be on the ACS GameMap. Includes both coordinates and
 * orientation (in radians). See the ACS document for more details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */

data class Position(val location: Coordinate, val orientation: Double)

// Oh, look how neat this is compared to its Java predecessor!
// Kotlin for the win!