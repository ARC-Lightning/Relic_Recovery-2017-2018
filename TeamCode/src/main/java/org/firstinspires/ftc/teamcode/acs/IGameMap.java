package org.firstinspires.ftc.teamcode.acs;

import org.locationtech.jts.geom.Polygon;

/**
 * An interface that describes the Game Map component of ACS. See the ACS document, 3.1.2, for
 * more details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public interface IGameMap {
    Position getPosition(String name) throws NoSuchFieldException;
    Polygon getObstacle(String name) throws NoSuchFieldException;
    Polygon getBoundary();
}
