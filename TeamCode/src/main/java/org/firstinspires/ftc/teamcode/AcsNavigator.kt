package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.acs.GameMap
import org.firstinspires.ftc.teamcode.acs.IGameMap
import org.firstinspires.ftc.teamcode.acs.Position
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry
import org.locationtech.jts.math.Vector2D

/**
 * Includes the algorithms needed to navigate through an ACS map. See the algorithms in the ACS
 * document for more details.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
class AcsNavigator
/**
 * Creates a new instance of the ACS Navigator, using the given telemetry manager for logging
 * and error-reporting purposes.
 *
 * @param telemetry The telemetry manager to write logs to
 */
(
        /**
         * Describes the telemetry manager that the navigator uses to write logs.
         */
        private val telemetry: ITelemetry,
        /**
         * Describes the drivetrain manager that the navigator sends commands to.
         */
        private val drivetrain: IDrivetrain) {

    // CONFIGURATIONS
    companion object {
        private val mapName = "TopLeft"
    }

    /**
     * Describes the current position of the robot in relation to the origin of the GameMap on the
     * playing field plane.
     */
    private var currentPos: Position

    /**
     * Describes the map in use. This is only used as reference for drivetrain vectors, therefore
     * it is immutable (final).
     */
    private val map: IGameMap

    init {
        // Try getting the map, throwing a fatal error if the name does not exist
        val mapOrNull = GameMap.getMapByName(mapName)
        if (mapOrNull == null) {
            this.telemetry.fatal("Code error: There is no map by the name of '$mapName'")
            throw RuntimeException("Code error")
        }
        this.map = mapOrNull

        this.currentPos = this.map.getPosition("start")!!
    }

    // -- IMPORTANT: When this navigator is active, no calls to the drivetrain (except turn()) shall be called
    // in the drivetrain. This will create a mismatch between where the robot thinks it is and where
    // it actually is.

    @Throws(NoSuchFieldException::class)
    fun goToPosition(mapName: String, power: Double) {
        val target = this.map.getPosition(mapName) ?:
                throw NoSuchFieldException("Position '$mapName' does not exist")

        val driveVector = Vector2D(this.currentPos.location, target.location)

        this.drivetrain.turn(-this.currentPos.orientation, power)
        this.drivetrain.move(driveVector, power)
        this.drivetrain.turn(target.orientation, power)
    }

    @Throws(NoSuchFieldException::class)
    fun goToPosition(mapName: String) {
        this.goToPosition(mapName, this.drivetrain.defaultPower)
    }

}