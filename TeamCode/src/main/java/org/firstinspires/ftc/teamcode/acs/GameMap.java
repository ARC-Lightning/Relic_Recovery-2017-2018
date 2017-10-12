package org.firstinspires.ftc.teamcode.acs;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of game obstacles with a polygonal boundary. See the ACS document, 3.1.2, for more
 * details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public class GameMap implements IGameMap {
    private static final Map<String, GameMap> maps = new HashMap<>();

    static {
        // Produces all polygons in maps
        GeometryFactory factory = new GeometryFactory();

        // This map is for the top-left quadrant of the arena, as shown in the game manual.
        // The positive y direction is shown downwards in the game manual.
        // The origin is at the top in the middle, where the red and blue lines meet.
        // This map is only for robots on the RED team.
        maps.put("TopLeft", new GameMap(
                factory.createPolygon(new Coordinate[]{
                        new Coordinate(0, 0),
                        new Coordinate(24*3, 0),
                        new Coordinate(24*3, 24*3),
                        new Coordinate(24, 24*3),
                        new Coordinate(24, 24*2),
                        new Coordinate(0, 24)}),
                new HashMap<String, Polygon>(),
                new HashMap<String, Position>() {{
                    // TODO Make fine adjustments to these coordinates

                    // On the balancing stone
                    put("start", new Position(new Coordinate(24*2, 24), 0));
                    // On the balancing stone, ready to read/knock the jewels
                    put("jewel-knock", new Position(new Coordinate(24*2, 24), Angle.toRadians(90)));
                    // (Parked) In the safe zone
                    put("safe-zone", new Position(new Coordinate(24*2.4, 24*2.5), Angle.toRadians(270)));
                    // Putting the pre-loaded glyph into the leftmost crypto-box column
                    put("load-column1", new Position(new Coordinate(24*2.1, 24*2.8), Angle.toRadians(90)));
                    // Putting the pre-loaded glyph into the middle crypto-box column
                    put("load-column2", new Position(new Coordinate(24*2.1, 24*2.5), Angle.toRadians(90)));
                    // Putting the pre-loaded glyph into the rightmost crypto-box column
                    put("load-column3", new Position(new Coordinate(24*2.1, 24*2.2), Angle.toRadians(90)));
                }}
        ));

        // This map is for the top-right quadrant of the arena, as shown in the game manual.
        // The positive y direction is shown downwards in the game manual.
        // The origin is at the top right corner.
        // This map is only for robots on the BLUE team.
        maps.put("TopRight", new GameMap(
                factory.createPolygon(new Coordinate[] {
                        new Coordinate(0, 0),
                        new Coordinate(24*3, 0),
                        new Coordinate(24*3, 24),
                        new Coordinate(24*2, 24*2),
                        new Coordinate(24*2, 24*3),
                        new Coordinate(0, 24*3)
                }),
                new HashMap<String, Polygon>(),
                new HashMap<String, Position>() {{
                    // TODO Make fine adjustments to these coordinates

                    // On the balancing stone
                    put("start", new Position(new Coordinate(24, 24), 0));
                    // On the balancing stone, ready to read/knock the jewels
                    put("jewel-knock", new Position(new Coordinate(24, 24), Angle.toRadians(270)));
                    // (Parked) in the safe zone
                    put("safe-zone", new Position(new Coordinate(24*0.6, 24*2.5), Angle.toRadians(90)));
                    // Putting the pre-loaded glyph into the leftmost crypto-box column
                    put("load-column1", new Position(new Coordinate(24*0.9, 24*2.2), Angle.toRadians(270)));
                    // Putting the pre-loaded glyph into the middle crypto-box column
                    put("load-column2", new Position(new Coordinate(24*0.9, 24*2.5), Angle.toRadians(270)));
                    // Putting the pre-loaded glyph into the rightmost crypto-box column
                    put("load-column3", new Position(new Coordinate(24*0.9, 24*2.8), Angle.toRadians(270)));
                }}
        ));

        // This map is for the bottom-left quadrant of the arena, as shown in the game manual.
        // The positive y direction is shown upwards in the game manual.
        // The origin is at the left bottom corner.
        // This map is only for robots on the RED team.
        maps.put("BottomLeft", new GameMap(
                factory.createPolygon(new Coordinate[] {
                        new Coordinate(0, 0),
                        new Coordinate(24*3, 0),
                        new Coordinate(24*3, 24*2),
                        new Coordinate(24*2, 24*3),
                        new Coordinate(0, 24*3)
                }),
                new HashMap<String, Polygon>(),
                new HashMap<String, Position>() {{
                    // TODO Make fine adjustments to these coordinates

                    // On the balancing stone
                    put("start", new Position(new Coordinate(24, 24*2), 0));
                    // On the balancing stone, ready to read/knock the jewels
                    put("jewel-knock", new Position(new Coordinate(24, 24*2), Angle.toRadians(270)));
                    // (Parked) in the safe zone
                    put("safe-zone", new Position(new Coordinate(24*1.5, 24*0.6), 0));
                    // Putting the pre-loaded glyph into the leftmost crypto-box column
                    put("load-column1", new Position(new Coordinate(24*1.8, 24*0.9), Angle.toRadians(180)));
                    // Putting the pre-loaded glyph into the middle crypto-box column
                    put("load-column2", new Position(new Coordinate(24*1.5, 24*0.9), Angle.toRadians(180)));
                    // Putting the pre-loaded glyph into the rightmost crypto-box column
                    put("load-column3", new Position(new Coordinate(24*0.2, 24*0.9), Angle.toRadians(180)));
                }}
        ));

        // This map is for the bottom-right quadrant of the arena, as shown in the game manual.
        // The positive y direction is shown upwards in the game manual.
        // The origin is at the bottom in the middle, between the blue and red lines.
        // The map is only for robots on the BLUE team.
        maps.put("BottomRight", new GameMap(
                factory.createPolygon(new Coordinate[] {
                        new Coordinate(0, 0),
                        new Coordinate(24*3, 0),
                        new Coordinate(24*3, 24*3),
                        new Coordinate(24, 24*3),
                        new Coordinate(0, 24*2)
                }),
                new HashMap<String, Polygon>(),
                new HashMap<String, Position>() {{
                    // TODO Make fine adjustments to these coordinates

                    // On the balancing stone
                    put("start", new Position(new Coordinate(24*2, 24*2), 0));
                    // On the balancing stone, ready to read/knock the jewels
                    put("jewel-knock", new Position(new Coordinate(24*2, 24*2), Angle.toRadians(90)));
                    // (Parked) in the safe zone
                    put("safe-zone", new Position(new Coordinate(24*1.5, 24*0.6), 0));
                    // Putting the pre-loaded glyph into the leftmost crypto-box column
                    put("load-column1", new Position(new Coordinate(24*1.8, 24*0.9), Angle.toRadians(180)));
                    // Putting the pre-loaded glyph into the middle crypto-box column
                    put("load-column2", new Position(new Coordinate(24*1.5, 24*0.9), Angle.toRadians(180)));
                    // Putting the pre-loaded glyph into the rightmost crypto-box column
                    put("load-column3", new Position(new Coordinate(24*0.2, 24*0.9), Angle.toRadians(180)));
                }}
        ));
    }

    /**
     * Gets a GameMap by its name.
     * @param name Name of the querying GameMap
     * @return The querying GameMap
     * @throws NoSuchFieldException If the GameMap with the querying name does not exist
     */
    public static GameMap getMapByName(String name) throws NoSuchFieldException {
        GameMap map = maps.get(name);
        if (map == null) {
            throw new NoSuchFieldException(name);
        }
        return map;
    }

    // ----- END STATIC -----

    /**
     * The boundaries of this map. The robot will not touch this polygon when controlled by ACS.
     */
    Polygon boundary;

    /**
     * The game obstacles of this map. Each obstacle is given a name for reference purposes.
     * Similar to {@see boundary}, the robot will not touch any obstacle polygons when controlled
     *   by ACS.
     */
    Map<String, Polygon> obstacles;

    /**
     * The positions available in this map. Each position is given a name for reference purposes.
     * These positions may only be valid on this GameMap.
     */
    Map<String, Position> positions;

    /**
     * Constructs a new GameMap with the following properties. Only meant to be used by the static
     * block. To grab a GameMap, use {@see getMapByName} instead.
     * @param boundary Boundary of this map
     * @param obstacles Obstacles, assigned to names, of this map
     * @param positions Positions in this map
     */
    private GameMap(Polygon boundary, Map<String, Polygon> obstacles, Map<String, Position> positions) {
        this.boundary = boundary;
        this.obstacles = obstacles;
        this.positions = positions;
    }

    /**
     * Gets the position with the given name in this GameMap.
     * @param name The name of the requesting position
     * @return The position requested
     * @throws NoSuchFieldException If the given name does not exist
     */
    public Position getPosition(String name) throws NoSuchFieldException {
        Position pos = this.positions.get(name);
        if (pos == null) {
            throw new NoSuchFieldException(name);
        }
        return pos;
    }

    /**
     * Gets the boundary of this GameMap.
     * @return The boundary of this GameMap.
     */
    public Polygon getBoundary() {
        return this.boundary;
    }

    /**
     * Gets the obstacle with the given name in this GameMap
     * @param name The name of the requesting obstacle
     * @return The requested obstacle
     * @throws NoSuchFieldException If the given name does not exist
     */
    public Polygon getObstacle(String name) throws NoSuchFieldException {
        Polygon obs = this.obstacles.get(name);
        if (obs == null) {
            throw new NoSuchFieldException(name);
        }
        return obs;
    }
}
