package org.firstinspires.ftc.teamcode.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.locationtech.jts.math.Vector2D;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of IDrivetrain that was done solo on October 4, 2017.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public class DrivetrainOct4 implements IDrivetrain {
    // CONFIGURATION
    private final double TICKS_PER_INCH = 400;
    private final double TICKS_PER_ROTATION = 1440;
    // END CONFIGURATION

    /**
     * Defines a pair of diagonal motors. Useful for Mecanum manipulation.
     * The naming convention for enum constants depend on in which direction the wheels, when
     * rotating in positive power, take the robot.
     *
     * @author Michael
     */
    private enum MotorDiagonalPair {
        RIGHT(MotorPtr.FRONT_LEFT, MotorPtr.REAR_RIGHT),
        LEFT(MotorPtr.FRONT_RIGHT, MotorPtr.REAR_LEFT);

        private final MotorPtr[] ptrs;

        MotorDiagonalPair(MotorPtr ptr1, MotorPtr ptr2) {
            this.ptrs = new MotorPtr[]{ptr1, ptr2};
        }

        public MotorPtr[] getMotors() {
            return this.ptrs;
        }

    }

    /*
        # A DISCOVERY ABOUT MECANUM DIRECTIONS
        (0, 1) -> (1, 1) x+1 FWD
        (1, 1) -> (1, 0) y-1 RIGHT-FWD
        (1, 0) -> (1, -1) y-1 RIGHT
        (1, -1) -> (0, -1) x-1 RIGHT-BWD
        (0, -1) -> (-1, -1) x-1 BWD
        (-1, -1) -> (-1, 0) y+1 LEFT-BWD
        (-1, 0) -> (-1, 1) y+1 LEFT
        (-1, 1) -> (0, 1) x+1 LEFT-FWD
     */

    /**
     * Calculates a mapping from a diagonal pair of motors to their desired power multiplier from
     * a vector from {@see VectorDirection}.
     * @param vec The vector indicating the direction to go
     * @return A mapping from a diagonal pair of motors to their desired power multiplier
     * @throws ArrayIndexOutOfBoundsException If the given vector was not from VectorDirection
     */
    private Map<MotorDiagonalPair, Integer> getMotorPowerFromVector(Vector2D vec)
            throws ArrayIndexOutOfBoundsException {

        if (Math.abs(vec.getX()) > 1 || Math.abs(vec.getY()) > 1) {
            throw new ArrayIndexOutOfBoundsException(vec.toString());
        }

        Vector2D clone = new Vector2D(vec);
        clone.rotate(-45);

        HashMap<MotorDiagonalPair, Integer> output = new HashMap<>();
        output.put(MotorDiagonalPair.RIGHT, (int) Math.round(clone.getX()));
        output.put(MotorDiagonalPair.LEFT, (int) Math.round(clone.getY()));

        return output;
    }

    /**
     * Turns the given vector into one that is in VectorDirection.
     * @param vec The vector to be normalized
     * @return The normalized vector
     */
    private Vector2D normalize(Vector2D vec) {
        double x = vec.getX(), y = vec.getY();
        // Ternary in use to avoid Division By Zero
        return new Vector2D(x == 0 ? 0 : Math.abs(x)/x, y == 0 ? 0 : Math.abs(y)/y);
    }

    private void checkPower(double power) {
        if (power == 0) {
            throw new RuntimeException("power cannot be zero, the robot will not move");
        }
        if (power < 0) {
            throw new RuntimeException("power cannot be negative, robot will move in opposite direction");
        }
    }

    // Same as startMove(), except without mode setting. Universal across encoder and non-encoder.
    private void setMotorPowers(Vector2D direction, double multiplier) {
        // For each motor, check which diagonal pair it's in; Then set the motor's power to the
        // multiplier value corresponding to that diagonal pair in the `powers` map.
        for (Map.Entry<MotorPtr, DcMotor> motor : this.motors.entrySet()) {
            for (MotorDiagonalPair pair : MotorDiagonalPair.values()) {
                if (Arrays.asList(pair.getMotors()).contains(motor.getKey())) {
                    motor.getValue().setPower(this.getMotorPowerFromVector(direction).get(pair) * multiplier);
                }
            }
        }
    }

    private void setRelativeTargetPosition(DcMotor motor, double relativeInch) {
        final double relativeTicks = relativeInch * TICKS_PER_INCH;
        motor.setTargetPosition(motor.getCurrentPosition() + (int)Math.round(relativeTicks));
    }

    /**
     * The default power setting. Used when `move` or `startMove` is called without the 2nd argument.
     */
    private double powerSetting;

    /**
     * A mapping from MotorPtrs to DcMotor instances.
     */
    private Map<MotorPtr, DcMotor> motors;

    public DrivetrainOct4(double power, Map<MotorPtr, DcMotor> motors) {
        this.powerSetting = power;
        this.motors = motors;
    }

    /**
     * Moves the robot according to the specified vector in default power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode).
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     */
    public void move(Vector2D vector) {
        this.move(vector, powerSetting);
    }

    /**
     * Moves the robot according to the specified vector in the specified power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power  The power, [0.0, 1.0], to set the motor(s) to.
     */
    public void move(Vector2D vector, double power) {
        checkPower(power);
        while (this.isBusy());

        for (DcMotor motor : this.motors.values()) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        Vector2D normalized = normalize(vector);
        Map<MotorDiagonalPair, Integer> powers = getMotorPowerFromVector(normalized);

        for (MotorDiagonalPair pair : MotorDiagonalPair.values()) {
            for (MotorPtr ptr : pair.getMotors()) {
                setRelativeTargetPosition(this.motors.get(ptr), powers.get(pair) * vector.length());
            }
        }
        for (DcMotor motor : this.motors.values()) {
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        setMotorPowers(normalized, power);
    }

    /**
     * Checks if any of the drivetrain motors are busy.
     *
     * @return True if any drivetrain motor is busy, otherwise false
     */
    public boolean isBusy() {
        for (DcMotor motor : this.motors.values()) {
            if (motor.isBusy()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    public void startMove(Vector2D direction) {
        this.startMove(direction, powerSetting);
    }

    /**
     * Starts moving the robot at the given speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     * @param power     Power, [0.0, 1.0], to set the necessary motors to
     */
    public void startMove(Vector2D direction, double power) {
        Map<MotorDiagonalPair, Integer> powers = this.getMotorPowerFromVector(direction);

        // Wait for existing operations to complete
        while (this.isBusy());

        for (DcMotor motor : motors.values()) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        this.setMotorPowers(direction, power);
    }

    /**
     * Sets the power of all drivetrain motors to 0, thus stopping the robot.
     */
    public void stop() {
        for (DcMotor motor : this.motors.values()) {
            motor.setPower(0);
        }
    }

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the default motor power.
     *
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     */
    @Override
    public void turn(double radians) {
        // TODO stub
    }

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the given motor power.
     *
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     * @param power   The power multiplier to set the motor to, (0, 1]
     */
    @Override
    public void turn(double radians, double power) {
        // TODO stub
    }

    /**
     * Gets the DcMotor object at the specified position relative to the robot.
     *
     * @param ptr The motor's position relative to the robot
     * @return The DcMotor object representing the specified motor
     */
    public DcMotor getMotor(MotorPtr ptr) {
        return this.motors.get(ptr);
    }
}
