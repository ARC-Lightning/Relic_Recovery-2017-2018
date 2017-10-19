package org.firstinspires.ftc.teamcode.drivetrain;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.locationtech.jts.math.Vector2D;

/**
 * The IDrivetrain interface describes the methods available from a drivetrain to other parts of the program.
 * The actual drivetrain manager shall implement this interface.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public interface IDrivetrain {
    /*
        # A WORD ON DIRECTIONS

        In this implementation of the Drivetrain interface, directions are represented similar to
        how a vector is represented mathematically. To understand this relationship, assume that the
        robot is at the origin on a four-quadrant 2D graph; The robot is facing up, or in the
        positive y direction. Moving forward is equivalent a directly upward vector, or `(0, 1)`. To
        strafe in the rear-left direction, for example, use the vector `(-1, -1)`. Vectors have
        scale, so `-1 <= value <= 1` does not have to be true all the time. If the caller would like
        to move the robot a longer distance, just multiply the values.

        A complete mapping from direction to the corresponding 1-unit vector is provided below.

        ↑ = (0, 1)
        ↗ = (1, 1)
        → = (1, 0)
        ↘ = (1, -1)
        ↓ = (0, -1)
        ↙ = (-1, -1)
        ← = (-1, 0)
        ↖ = (-1, 1)

        ## Motivation

        This system provides the flexibility that allows path-finding algorithms to manipulate
        directions. For example, the forward and right vectors can be summed to produce the up-right
        diagonal vector.

        ## Synthetic Movements

        If you decide to send a vector like `(2, 1)` to the drivetrain manager, it will be broken
        down into two UNORDERED parts: `(1, 1)` and `(1, 0)`. Because the robot can only move in 8
        directions, any vector that is not a uniform scale of any vector in the list above gets
        broken down into multiple motions. `(2, 1)` could make the robot move in the up-right
        direction, stop, then move forward.

        **These vectors are discouraged because there is no guarantee for the order in which the
        robot moves. This could potentially lead to conflict with obstacles.**

        ## Technically...

        The vector is represented by a `Vector2D` object. This class is included with JTS Topology
        Suite. An enum-like class named `VectorDirection` is included with
        this interface for Don't-Let-Me-Think readable code, in which case the method will be
        invoked like this: `drivetrain.move(VectorDirection.FORWARD)`
     */

    /**
     * A class containing vectors representing all of 8 directions in which the robot is capable of
     * moving.
     */
    class VectorDirection {
        public static final Vector2D FORWARD = new Vector2D(0, 1);
        public static final Vector2D FORWARD_RIGHT = new Vector2D(1, 1);
        public static final Vector2D RIGHT = new Vector2D(1, 0);
        public static final Vector2D BACKWARD_RIGHT = new Vector2D(1, -1);
        public static final Vector2D BACKWARD = new Vector2D(0, -1);
        public static final Vector2D BACKWARD_LEFT = new Vector2D(-1, -1);
        public static final Vector2D LEFT = new Vector2D(-1, 0);
        public static final Vector2D FORWARD_LEFT = new Vector2D(-1, 1);
    }

    /**
     * This enum contains values that point to each motor in the drivetrain.
     * This is useful for directly sending commands to the individual motors.
     */
    enum MotorPtr {
        FRONT_LEFT,         FRONT_RIGHT,
        //       |          |
        //       -- ROBOT! --
        //       |          |
        REAR_LEFT,          REAR_RIGHT
    }

    /**
     * Moves the robot according to the specified vector in default power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode).
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     */
    void move(Vector2D vector);

    /**
     * Moves the robot according to the specified vector in the specified power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power The power, (0, 1], to set the motor(s) to.
     */
    void move(Vector2D vector, double power);

    /**
     * Checks if any of the drivetrain motors are busy.
     * @return True if any drivetrain motor is busy, otherwise false
     */
    boolean isBusy();

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    void startMove(Vector2D direction);

    /**
     * Starts moving the robot at the given speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     * @param direction A vector from and only from {@see VectorDirection}.
     * @param power Power, [0.0, 1.0], to set the necessary motors to
     */
    void startMove(Vector2D direction, double power);

    /**
     * Sets the power of all drivetrain motors to 0, thus stopping the robot.
     */
    void stop();

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the default motor power.
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     */
    void turn(double radians);

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the given motor power.
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     * @param power The power multiplier to set the motor to, (0, 1]
     */
    void turn(double radians, double power);

    /**
     * Gets the DcMotor object at the specified position relative to the robot.
     * @param ptr The motor's position relative to the robot
     * @return The DcMotor object representing the specified motor
     */
    DcMotor getMotor(MotorPtr ptr);
}
