package org.firstinspires.ftc.teamcode.acs

import org.firstinspires.ftc.teamcode.AllianceColor
import org.locationtech.jts.algorithm.Angle
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon

/**
 * A map of game obstacles with a polygonal boundary. See the ACS document, 3.1.2, for more
 * details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
class GameMap

/**
 * Constructs a new GameMap with the following properties. Only meant to be used by the static
 * block. To grab a GameMap, use {@see getMapByName} instead.
 * @param boundary Boundary of this map
 * @param obstacles Obstacles, assigned to names, of this map
 * @param positions Positions in this map
 */
private constructor(
        /**
         * The boundaries of this map. The robot will not touch this polygon when controlled by ACS.
         */
        override val boundary: Polygon,
        /**
         * The game obstacles of this map. Each obstacle is given a name for reference purposes.
         * Similar to {@see boundary}, the robot will not touch any obstacle polygons when controlled
         * by ACS.
         */
        private var obstacles: Map<String, Polygon>,
        /**
         * The positions available in this map. Each position is given a name for reference purposes.
         * These positions may only be valid on this GameMap.
         */
        private var positions: Map<String, Position>
) : IGameMap {


    /**
     * Gets the position with the given name in this GameMap.
     * @param name The name of the requesting position
     * @return The position requested, or null if the position does not exist.
     */
    override fun getPosition(name: String): Position? = positions[name]

    /**
     * Gets the obstacle with the given name in this GameMap
     * @param name The name of the requesting obstacle
     * @return The requested obstacle, or null if the obstacle does not exist.
     */
    override fun getObstacle(name: String): Polygon? = obstacles[name]

    companion object {
        private val factory = GeometryFactory()
        private val maps: Map<String, GameMap> = mapOf(
                // This map is for the top-left quadrant of the arena, as shown in the game manual.
                // The positive y direction is shown downwards in the game manual.
                // The origin is at the top in the middle, where the red and blue lines meet.
                // This map is only for robots on the RED team.
                "TopLeft" to GameMap(
                        factory.createPolygon(arrayOf(
                                Coordinate(0.0, 0.0),
                                Coordinate(24.0 * 3, 0.0),
                                Coordinate(24.0 * 3, 24.0 * 3),
                                Coordinate(24.0, 24.0 * 3),
                                Coordinate(24.0, 24.0 * 2),
                                Coordinate(0.0, 24.0),
                                Coordinate(0.0, 0.0))),
                        mapOf(),
                        mapOf(
                                // TODO Make fine adjustments to these coordinates

                                // On the balancing stone
                                "start" to Position(Coordinate((24 * 2).toDouble(), 24.0), 0.0),
                                // On the balancing stone, ready to read/knock the jewels
                                "jewel-knock" to Position(Coordinate((24 * 2).toDouble(), 24.0), Angle.toRadians(90.0)),
                                // (Parked) In the safe zone
                                "safe-zone" to Position(Coordinate(24 * 2.4, 24 * 2.5), Angle.toRadians(270.0)),
                                // Putting the pre-loaded glyph into the leftmost crypto-box column
                                "load-column1" to Position(Coordinate(24 * 2.1, 24 * 2.8), Angle.toRadians(90.0)),
                                // Putting the pre-loaded glyph into the middle crypto-box column
                                "load-column2" to Position(Coordinate(24 * 2.1, 24 * 2.5), Angle.toRadians(90.0)),
                                // Putting the pre-loaded glyph into the rightmost crypto-box column
                                "load-column3" to Position(Coordinate(24 * 2.1, 24 * 2.2), Angle.toRadians(90.0))
                        )
                ),
                // This map is for the top-right quadrant of the arena, as shown in the game manual.
                // The positive y direction is shown downwards in the game manual.
                // The origin is at the top right corner.
                // This map is only for robots on the BLUE team.
                "TopRight" to GameMap(
                        factory.createPolygon(arrayOf(
                                Coordinate(0.0, 0.0),
                                Coordinate(24.0 * 3, 0.0),
                                Coordinate(24.0 * 3, 24.0),
                                Coordinate(24.0 * 2, 24.0 * 2),
                                Coordinate(24.0 * 2, 24.0 * 3),
                                Coordinate(0.0, 24.0 * 3),
                                Coordinate(0.0, 0.0)
                        )),
                        mapOf(),
                        mapOf(
                                // TODO Make fine adjustments to these coordinates

                                // On the balancing stone
                                "start" to Position(Coordinate(24.0, 24.0), 0.0),
                                // On the balancing stone, ready to read/knock the jewels
                                "jewel-knock" to Position(Coordinate(24.0, 24.0), Angle.toRadians(270.0)),
                                // (Parked) in the safe zone
                                "safe-zone" to Position(Coordinate(24 * 0.6, 24 * 2.5), Angle.toRadians(90.0)),
                                // Putting the pre-loaded glyph into the leftmost crypto-box column
                                "load-column1" to Position(Coordinate(24 * 0.9, 24 * 2.2), Angle.toRadians(270.0)),
                                // Putting the pre-loaded glyph into the middle crypto-box column
                                "load-column2" to Position(Coordinate(24 * 0.9, 24 * 2.5), Angle.toRadians(270.0)),
                                // Putting the pre-loaded glyph into the rightmost crypto-box column
                                "load-column3" to Position(Coordinate(24 * 0.9, 24 * 2.8), Angle.toRadians(270.0))
                        )
                ),
                // This map is for the bottom-left quadrant of the arena, as shown in the game manual.
                // The positive y direction is shown upwards in the game manual.
                // The origin is at the left bottom corner.
                // This map is only for robots on the RED team.
                "BottomLeft" to GameMap(
                        factory.createPolygon(arrayOf(
                                Coordinate(0.0, 0.0),
                                Coordinate(24.0 * 3, 0.0),
                                Coordinate(24.0 * 3, 24.0 * 2),
                                Coordinate(24.0 * 2, 24.0 * 3),
                                Coordinate(0.0, 24.0 * 3),
                                Coordinate(0.0, 0.0)
                        )),
                        mapOf(),
                        mapOf(
                                // TODO Make fine adjustments to these coordinates

                                // On the balancing stone
                                "start" to Position(Coordinate(24.0, (24 * 2).toDouble()), 0.0),
                                // On the balancing stone, ready to read/knock the jewels
                                "jewel-knock" to Position(Coordinate(24.0, (24 * 2).toDouble()), Angle.toRadians(270.0)),
                                // (Parked) in the safe zone
                                "safe-zone" to Position(Coordinate(24 * 1.5, 24 * 0.6), 0.0),
                                // Putting the pre-loaded glyph into the leftmost crypto-box column
                                "load-column1" to Position(Coordinate(24 * 1.8, 24 * 0.9), Angle.toRadians(180.0)),
                                // Putting the pre-loaded glyph into the middle crypto-box column
                                "load-column2" to Position(Coordinate(24 * 1.5, 24 * 0.9), Angle.toRadians(180.0)),
                                // Putting the pre-loaded glyph into the rightmost crypto-box column
                                "load-column3" to Position(Coordinate(24 * 0.2, 24 * 0.9), Angle.toRadians(180.0)))
                ),
                // This map is for the bottom-right quadrant of the arena, as shown in the game manual.
                // The positive y direction is shown upwards in the game manual.
                // The origin is at the bottom in the middle, between the blue and red lines.
                // The map is only for robots on the BLUE team.
                "BottomRight" to GameMap(
                        factory.createPolygon(arrayOf(
                                Coordinate(0.0, 0.0),
                                Coordinate(24.0 * 3, 0.0),
                                Coordinate(24.0 * 3, 24.0 * 3),
                                Coordinate(24.0, 24.0 * 3),
                                Coordinate(0.0, 24.0 * 2),
                                Coordinate(0.0, 0.0)
                        )),
                        mapOf(),
                        mapOf(
                                "start" to Position(Coordinate(24.0 * 2, 24.0 * 2), 0.0),
                                "jewel-knock" to Position(Coordinate(24.0 * 2, 24.0 * 2), Angle.toRadians(90.0)),
                                "safe-zone" to Position(Coordinate(24 * 1.5, 24 * 0.6), 0.0),
                                "load-column1" to Position(Coordinate(24 * 1.8, 24 * 0.9), Angle.toRadians(180.0)),
                                "load-column2" to Position(Coordinate(24 * 1.5, 24 * 0.9), Angle.toRadians(180.0)),
                                "load-column3" to Position(Coordinate(24 * 0.2, 24 * 0.9), Angle.toRadians(180.0))
                        )

                )
        )

        /**
         * Gets a GameMap by its name.
         * @param name Name of the querying GameMap
         * @return The querying GameMap, or null if there is no map by the name.
         */
        fun getMapByName(name: String): IGameMap? = maps[name]

        /**
         * Depending on the robot's position in our drivers' perspective AND the team we're on,
         * determine which GameMap we should use.
         *
         * @param color The color of the alliance we're in
         * @param isLeft Whether the robot is on the left side of our drivers' perspective
         *
         * @return The name of the corresponding GameMap to use
         */
        fun getNameVisually(color: AllianceColor, isLeft: Boolean): String = when (color) {
            AllianceColor.RED -> if (isLeft) "TopLeft" else "BottomLeft"
            AllianceColor.BLUE -> if (isLeft) "BottomRight" else "TopRight"
        }
    }
}
