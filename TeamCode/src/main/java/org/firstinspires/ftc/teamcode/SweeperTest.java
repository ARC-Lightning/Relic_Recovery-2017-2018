package org.firstinspires.ftc.teamcode;

        import com.qualcomm.robotcore.eventloop.opmode.OpMode;
        import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
        import com.qualcomm.robotcore.hardware.Servo;

//import static java.lang.Thread.sleep;



//@Disabled

/**
 * Created by Joshua Krinsky :) on 10/11/2017.
 *
 * A basic test of the sweeper concept for picking up glyphs.
 * @author Joshua Krinsky
 * @author Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
@TeleOp(name="SweeperTest", group ="Test")
public class SweeperTest extends OpMode {

    private Servo RightSweeper;
    private Servo LeftSweeper;
    private boolean isRightClamping = false;
    private boolean isLeftClamping = false;

    public void init() {
        RightSweeper = hardwareMap.servo.get("RightSweeper");
        LeftSweeper = hardwareMap.servo.get("LeftSweeper");
        RightSweeper.setPosition(0);
        LeftSweeper.setPosition(0);
    }
    public void loop() {
        // Changelog update: October 18, 2017
        // The original logic that sets the servo to a position between 0 and 1 whenever the pressed
        //   state of the controller changes *is replaced with* the trigger initiating the clamp and
        //   the bumper releasing the clamp for each side; The clamp position is now sustained.

        // Status determination logic: See getNewStatus().
        boolean rightNewStatus = getNewStatus(isRightClamping,
                gamepad1.right_bumper,
                gamepad1.right_trigger>0.5);
        boolean leftNewStatus = getNewStatus(isLeftClamping,
                gamepad1.left_bumper,
                gamepad1.left_trigger>0.5);

        if (isRightClamping != rightNewStatus) {
            isRightClamping = rightNewStatus;
            updateSweeperStatus(RightSweeper, "Right sweeper", rightNewStatus);
        }
        if (isLeftClamping != leftNewStatus) {
            isLeftClamping = leftNewStatus;
            updateSweeperStatus(LeftSweeper, "Left sweeper", leftNewStatus);
        }
        telemetry.update();

    }

    /**
     * Deduce the current desired status of a given servo given the bumper and trigger values.
     *
     * Truth table:
     * Current  Bumper  Trigger     RETURN VALUE
     * T        T       T           T
     * T        T       F           F
     * T        F       T           T
     * T        F       F           T
     * F        T       T           F
     * F        T       F           F
     * F        F       T           T
     * F        F       F           F
     *
     * @param currentStatus The current status of the servo, true for clamping
     * @param bump True if the corresponding un-clamping button is being triggered
     * @param trigger True if the corresponding clamping button is being triggered
     * @return The new desired status of the servo
     */
    private boolean getNewStatus(boolean currentStatus, boolean bump, boolean trigger) {
        return currentStatus != (bump != trigger && currentStatus != trigger);
    }

    /**
     * Sets the given servo to a position corresponding to the given status, then adds a message
     *   using the given servo name to the telemetry.
     * @param sweeper The sweeper to set the position of
     * @param name The name of the given sweeper
     * @param newStatus The new status boolean that the servo should be in
     */
    private void updateSweeperStatus(Servo sweeper, String name, boolean newStatus) {
        final double CLAMPED_POS = 0.2;
        sweeper.setPosition(newStatus ? CLAMPED_POS : 0);
        telemetry.addData(name + " is now ", newStatus ? "Clamping" : "Relaxed");
    }
}
