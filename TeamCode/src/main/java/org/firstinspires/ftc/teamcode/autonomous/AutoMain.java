package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.drivetrain.Drivetrain;
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain;

import java.util.HashMap;

/**
 * The main LinearOpMode procedure in which autonomous operation is performed.
 * Three actions that score points for us:
 *  - Putting pre-loaded glyph into column
 *    - The rightServo column according to the VuMark
 *  - Knocking off the rightServo jewel
 *  - Parking in the safe zone
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Autonomous(name = "Autonomous Main", group = "Pragmaticos")
public class AutoMain extends LinearOpMode {

    /* NECESSARY MODULES */
    IDrivetrain drivetrain;
    IGameMap gameMap;
    ITelemetry _telemetry;

    /**
     * Override this method and place your code here.
     * <p>
     * Please do not swallow the InterruptedException, as it is used in cases
     * where the op mode needs to be terminated early.
     *
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException {
        if (!initAll()) return;

        waitForStart();

        // TODO finish once hardware is determined
    }

    /**
     * Initializes all necessary systems for Autonomous operation.
     * Includes the following systems:
     * - Telemetry
     * - GameMap
     * - Drivetrain
     * - Navigator
     * - Manipulators
     * - Sensors
     */
    private boolean initAll() {
        _telemetry = new Telemetry(this.telemetry);

        // Load GameMap
        try {
            gameMap = GameMap.getMapByName("BottomRight");
        } catch (final NoSuchFieldException noField) {
            _telemetry.fatal("(13) + " + noField.getMessage());
            return false;
        }

        // Initialize drivetrain
        try {
            hardwareMap.dcMotor.get("FrontLeft");
            drivetrain = new Drivetrain(0.8, new HashMap<IDrivetrain.MotorPtr, DcMotor>() {{
                put(IDrivetrain.MotorPtr.FRONT_LEFT, hardwareMap.dcMotor.get("FrontLeft"));
                put(IDrivetrain.MotorPtr.FRONT_RIGHT, hardwareMap.dcMotor.get("FrontRight"));
                put(IDrivetrain.MotorPtr.REAR_LEFT, hardwareMap.dcMotor.get("RearLeft"));
                put(IDrivetrain.MotorPtr.REAR_RIGHT, hardwareMap.dcMotor.get("RearRight"));
            }});
        } catch (final IllegalArgumentException argumentExc) {
            _telemetry.fatal("(14) " + argumentExc.getMessage());
            return false;
        }

        return false;
    }
}
