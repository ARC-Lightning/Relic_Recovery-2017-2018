package org.firstinspires.ftc.teamcode.acs;

import org.locationtech.jts.geom.Coordinate;

/**
 * Defines a position in which the robot can be on the ACS GameMap. Includes both coordinates and
 * orientation (in radians). See the ACS document for more details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public class Position {
    /**
     * The two-dimensional coordinates that describe the location of this position.
     */
    private Coordinate location;

    /**
     * The orientation of this position relative to the positive y direction.
     * This is an angle in radians.
     */
    private double orientation;

    public Position(Coordinate location, double orientation) {
        this.location = location;
        this.orientation = orientation;
    }

    /**
     * @return The location, relative to the game map's origin, of this position.
     */
    public Coordinate getLocation() {
        return location;
    }

    /**
     * @param location The location, relative to the game map's origin, to set this position to.
     */
    public void setLocation(Coordinate location) {
        this.location = location;
    }

    /**
     * @return The orientation, relative to the positive y direction, of this position.
     */
    public double getOrientation() {
        return orientation;
    }

    /**
     * @param orientation The orientation, relative to the positive y direction, to set this
     *                    position to.
     */
    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }
}
