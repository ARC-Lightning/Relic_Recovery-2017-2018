package org.firstinspires.ftc.teamcode.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.acs.IGameMap
import org.firstinspires.ftc.teamcode.io.DynamicConfig
import org.firstinspires.ftc.teamcode.io.Hardware
import org.firstinspires.ftc.teamcode.teleop.GamepadListener

/**
 * The main LinearOpMode procedure in which autonomous operation is performed.
 * Four actions that score points for us:
 * - Putting pre-loaded glyph into column
 * - The right column according to the VuMark
 * - Knocking off the right jewel
 * - Parking in the safe zone
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Autonomous(name = "Autonomous Main", group = "Pragmaticos")
class AutoMain : LinearOpMode() {
    var hardware: Hardware? = null

    /* NECESSARY MODULES UNIQUE TO AUTONOMOUS */
    var gameMap: IGameMap? = null

    /* NECESSARY MODULES ONLY TO INIT / INIT LOOP */
    var padListener: GamepadListener? = null

    /**
     *
     *
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    override fun runOpMode() {
        if (!initAll()) return

        waitForStart()

        // TODO(waiting) hardware unfinished
    }

    /**
     * Initializes all necessary systems for Autonomous operation.
     * Includes the following systems:
     * - Telemetry   ╮
     * - GameMap     │
     * - Drivetrain  ├─ Hardware
     * - Manipulators│
     * - Sensors     ╯
     * - Navigator
     */
    private fun initAll(): Boolean {
        try {
            hardware = Hardware.new(this, 1.0)
            // It is important to call the constructor to GamepadListener before Hardware because
            //   the lambdas in the D.C. mappings require it for telemetry.
            padListener = GamepadListener(gamepad1, DynamicConfig.Mapping.mappings)
            // TODO(impossible) GamepadListener configuration not practical to Autonomous

        } catch (exc: Exception) {
            telemetry.addData("FATAL", "ERROR")
            telemetry.addData("Initialization failed", exc.message ?: "for a reason unknown to humankind")
            return false
        }
        return true
    }
}

