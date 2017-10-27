package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain;
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry;

/**
 * Main procedure for TeleOp during Relic Recovery.
 * As a quick reference, the following actions score points for our team during TeleOp/end game.
 * - Glyph stored in cryptobox
 * - Completed row of 3 or column of 4
 * - Completed cipher
 * - Robot balanced on balancing stone
 * - (end game) Relic in Zone 1 thru 3
 * - (end game) Relic upright bonus
 *
 * @author Michael Peng
 *         For team: 4410 (Lightning)
 *         <p>
 *         FIRST - Gracious Professionalism
 */
@TeleOp(name = "TeleOp Main", group = "Pragmaticos")
public class TeleOpMain extends OpMode {

    // Necessary Modules
    IDrivetrain drivetrain;
    ITelemetry _telemetry;

    public void init() {

    }

    public void loop() {

    }
}