package org.firstinspires.ftc.teamcode.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import org.locationtech.jts.math.Vector2D

/**
 * The IDrivetrain interface describes the methods available from a drivetrain to other parts of the program.
 * The actual drivetrain manager shall implement this interface.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
interface IDrivetrain {

    /**
     * Checks if any of the drivetrain motors are busy.
     * @return True if any drivetrain motor is busy, otherwise false
     */
    val isBusy: Boolean

    /**
     * @return The default power that would be used when move(), turn(), etc. is called without
     * a power value.
     */
    val defaultPower: Double
    /*
        # A WORD ON DIRECTIONS

        In this implementation of the Drivetrain interface, directions are represented similar to
        how a vector is represented mathematically. To understand this relationship, assume that the
        robot is at the origin on a four-quadrant 2D graph; The robot is facing up, or in the
        positive y direction. Moving forward is equivalent a directly upward vector, or `(0, 1)`. To
        strafe in the rear-leftServo direction, for example, use the vector `(-1, -1)`. Vectors have
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
        directions. For example, the forward and rightServo vectors can be summed to produce the up-rightServo
        diagonal vector.

        ## Synthetic Movements

        If you decide to send a vector like `(2, 1)` to the drivetrain manager, it will be broken
        down into two UNORDERED parts: `(1, 1)` and `(1, 0)`. Because the robot can only move in 8
        directions, any vector that is not a uniform scale of any vector in the list above gets
        broken down into multiple motions. `(2, 1)` could make the robot move in the up-rightServo
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
    object VectorDirection {
        val FORWARD = Vector2D(0.0, 1.0)
        val FORWARD_RIGHT = Vector2D(1.0, 1.0)
        val RIGHT = Vector2D(1.0, 0.0)
        val BACKWARD_RIGHT = Vector2D(1.0, -1.0)
        val BACKWARD = Vector2D(0.0, -1.0)
        val BACKWARD_LEFT = Vector2D(-1.0, -1.0)
        val LEFT = Vector2D(-1.0, 0.0)
        val FORWARD_LEFT = Vector2D(-1.0, 1.0)
    }

    /**
     * This enum contains values that point to each motor in the drivetrain.
     * This is useful for directly sending commands to the individual motors.
     */
    enum class MotorPtr(var isFront: Boolean, var isLeft: Boolean) {
        FRONT_LEFT(true, true), FRONT_RIGHT(true, false),
        //       │              │
        //       ├─── ROBOT! ───┤
        //       │              │
        REAR_LEFT(false, true), REAR_RIGHT(false, false)
    }

    /**
     * Moves the robot according to the specified vector in default power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode).
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     */
    fun move(vector: Vector2D)

    /**
     * Moves the robot according to the specified vector in the specified power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power The power, (0, 1], to set the motor(s) to.
     */
    fun move(vector: Vector2D, power: Double)

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    fun startMove(direction: Vector2D)

    /**
     * Starts moving the robot at the given speed according to the specified direction.
     * Ideal for TeleOp (OpMode)
     * @param direction A vector from and only from {@see VectorDirection}.
     * @param power Power, [0.0, 1.0], to set the necessary motors to
     */
    fun startMove(direction: Vector2D, power: Double)

    /**
     * Sets the power of all drivetrain motors to 0, thus stopping the robot.
     */
    fun stop()

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the default motor power.
     * Ideal for Autonomous
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     */
    fun turn(radians: Double)

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the given motor power.
     * Ideal for Autonomous
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     * @param power The power multiplier to set the motor to, (0, 1]
     */
    fun turn(radians: Double, power: Double)

    /**
     * Starts rotating the robot in place in the given direction.
     *
     * @param isCounterClockwise true if counter-clockwise rotation desired
     */
    fun startTurn(isCounterClockwise: Boolean)

    /**
     * Starts rotating the robot in place with the given power multiplier for motors.
     * A positive given power value will cause the robot to rotate counter-clockwise.
     *
     * @param power The power multiplier, [-1, 1]
     */
    fun startTurn(power: Double)

    /**
     * Gets the DcMotor object at the specified position relative to the robot.
     * @param ptr The motor's position relative to the robot
     * @return The DcMotor object representing the specified motor
     */
    fun getMotor(ptr: MotorPtr): DcMotor
}
