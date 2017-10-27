package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.acs.GameMap;
import org.firstinspires.ftc.teamcode.acs.IGameMap;
import org.firstinspires.ftc.teamcode.acs.Position;
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain;
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry;
import org.locationtech.jts.math.Vector2D;

/**
 * Includes the algorithms needed to navigate through an ACS map. See the algorithms in the ACS
 * document for more details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public class AcsNavigator {
    // CONFIGURATION
    private static final String mapName = "TopLeft";
    // END CONFIGURATION

    /**
     * Describes the current position of the robot in relation to the origin of the GameMap on the
     * playing field plane.
     */
    private Position currentPos;

    /**
     * Describes the map in use. This is only used as reference for drivetrain vectors, therefore
     * it is immutable (final).
     */
    private final IGameMap map;

    /**
     * Describes the telemetry manager that the navigator uses to write logs.
     */
    private final ITelemetry telemetry;

    /**
     * Describes the drivetrain manager that the navigator sends commands to.
     */
    private final IDrivetrain drivetrain;

    /**
     * Creates a new instance of the ACS Navigator, using the given telemetry manager for logging
     * and error-reporting purposes.
     *
     * @param telemetry The telemetry manager to write logs to
     */
    public AcsNavigator(ITelemetry telemetry, IDrivetrain drivetrain) {
        this.telemetry = telemetry;
        this.drivetrain = drivetrain;

        // Try getting the map, throwing a fatal error if the name does not exist
        try {
            this.map = GameMap.getMapByName(mapName);
        } catch (final NoSuchFieldException noField) {
            this.telemetry.fatal("Code error: There is no map by the name of '" + mapName + "'");
            throw new RuntimeException("Code error");
        }

        // Try getting the start position, throwing a fatal error if the position does not exist
        try {
            this.currentPos = this.map.getPosition("start");
        } catch (final NoSuchFieldException noField) {
            this.telemetry.fatal("Code error: There is no position in the IGameMap by the name of "
                    + "'start'");
            throw new RuntimeException("Code error");
        }
    }

    // -- IMPORTANT: When this navigator is active, no calls to the drivetrain (except turn()) shall be called
    // in the drivetrain. This will create a mismatch between where the robot thinks it is and where
    // it actually is.

    public void goToPosition(String mapName, double power) throws NoSuchFieldException {
        final Position target = this.map.getPosition(mapName);

        final Vector2D driveVector = new Vector2D(this.currentPos.getLocation(), target.getLocation());
        final double turnRadian = target.getOrientation() - this.currentPos.getOrientation();

        this.drivetrain.move(driveVector, power);
        this.drivetrain.turn(turnRadian, power);
    }

    public void goToPosition(String mapName) throws NoSuchFieldException {
        final Position target = this.map.getPosition(mapName);

        final Vector2D driveVector = new Vector2D(this.currentPos.getLocation(), target.getLocation());
        final double turnRadian = target.getOrientation() - this.currentPos.getOrientation();

        this.drivetrain.move(driveVector);
        this.drivetrain.turn(turnRadian);
    }
}
