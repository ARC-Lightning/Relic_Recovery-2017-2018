package org.firstinspires.ftc.teamcode.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range
import com.qualcomm.robotcore.util.RobotLog
import org.locationtech.jts.algorithm.Angle
import org.locationtech.jts.math.Vector2D

/**
 * An implementation of IDrivetrain that was done on October 4, 2017.
 *
 * @author Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
class Drivetrain(
        /**
         * The default power setting. Used when `move` or `startMove` is called without the 2nd argument.
         */
        override val defaultPower: Double,
        /**
         * A mapping from MotorPtrs to DcMotor instances.
         */
        private val motors: Map<IDrivetrain.MotorPtr, DcMotor>) : IDrivetrain {

    init {
        // Reverse the direction of motors on the right.
        forEachOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT,
                IDrivetrain.MotorPtr.REAR_RIGHT
        ) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }

        // Preciseness of movement is crucial in AcsNavigator.
        // Don't let the motors drift.
        motors.values.forEach { it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE }

        // Reset the encoders.
        motors.values.forEach {
            it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }

        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
    }

    // CONFIGURATION
    companion object {
        private val TICKS_PER_REVOLUTION = 280.0
        private val INCHES_PER_REVOLUTION = 2.5
        private val TICKS_PER_CIRCULAR_SPIN = TICKS_PER_REVOLUTION * 4
        private val COUNT_USING_TIME = true
        private val MOVE_MS_PER_INCH = 40
        private val TURN_MS_PER_CIRCLE = 1000
    }
    // END CONFIGURATION

    // Variables
    private var isUsingEncoders: Boolean = false

    /**
     * Defines a pair of diagonal motors. Useful for Mecanum manipulation.
     * The naming convention for enum constants depend on in which direction the wheels, when
     * rotating in positive power, take the robot.
     *
     * @author Michael
     */
    private enum class MotorDiagonalPair(val motors: Array<IDrivetrain.MotorPtr>,
                                         val displayName: String) {
        RIGHT(arrayOf(
                IDrivetrain.MotorPtr.FRONT_LEFT, IDrivetrain.MotorPtr.REAR_RIGHT), "FL RR pair"),
        LEFT(arrayOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT, IDrivetrain.MotorPtr.REAR_LEFT), "FR RL pair");

        override fun toString(): String = displayName
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

        Conclusion: a 45-degree clockwise rotation will convert an input vector to a vector where
          the x value is the relative target position for the RIGHT pair and the y value is the <...>
          for the LEFT pair.
     */

    /**
     * Calculates a mapping from a diagonal pair of motors to their desired power multiplier from
     * any arbitrary vector.
     * @param vec The vector indicating the direction to go
     * @return A mapping from a diagonal pair of motors to their desired power multiplier
     */
    private fun getMotorPowerFromVector(vec: Vector2D): Map<MotorDiagonalPair, Double> {
        // Lightning against Division By Zero!
        if (vec.length() == 0.0) {
            return mapOf(
                    MotorDiagonalPair.RIGHT to 0.0,
                    MotorDiagonalPair.LEFT to 0.0
            )
        }

        // TODO Remove scaling of power for TeleOp if gamepad stick limit is circular
        val clone = vec.rotate(Angle.toRadians(315.0))

        val scale = maxOf(Math.abs(clone.x), Math.abs(clone.y))
        val rightPower = clone.x / scale
        val leftPower = clone.y / scale

        return mapOf(
                MotorDiagonalPair.RIGHT to rightPower,
                MotorDiagonalPair.LEFT to leftPower
        )
    }

    /**
     * Converts a direction in which the caller wishes to travel to a mapping from diagonal pair to
     *   that pair's desired target position in inches.
     *
     *  @param direction A vector from the robot to the target position where the robot is at (0,0)
     *  @return A mapping from a diagonal pair to its desired relative position in inches
     */
    private fun directionToRelativeTargets(direction: Vector2D): Map<MotorDiagonalPair, Double> {
        val positions = direction.rotate(Angle.toRadians(315.0))

        return mapOf(
                MotorDiagonalPair.RIGHT to positions.x,
                MotorDiagonalPair.LEFT to positions.y
        )
    }

    /*
     * Turns the given vector into one that is in VectorDirection.
     * Creates a clone.
     * @param vec The vector to be normalized
     * @return The normalized vector
     *
    private fun normalize(vec: Vector2D): Vector2D {
        val x = vec.x
        val y = vec.y
        // Ternary in use to avoid Division By Zero
        return Vector2D(if (x == 0.0) 0.0 else Math.abs(x) / x, if (y == 0.0) 0.0 else Math.abs(y) / y)
    }*/

    private fun checkPower(power: Double) {
        if (power == 0.0) {
            throw RuntimeException("power cannot be 0, the robot will not move")
        }
        if (power < 0) {
            throw RuntimeException("power ($power) cannot be negative, robot will move in opposite direction")
        }
    }

    // Same as startMove(), except without mode setting. Universal across encoder and non-encoder.
    private fun setMotorPowers(direction: Vector2D, multiplier: Double) {
        // Grab the pair -> power map, then set the power of each motor in each pair to its mapped
        // value.
        getMotorPowerFromVector(direction).entries
                // For each pair -> power entry
                .forEach { mapping ->
                    forEachOf(*mapping.key.motors) {
                        it.power = Range.clip(mapping.value * multiplier, -1.0, 1.0)
                        RobotLog.ii(mapping.key.displayName, (mapping.value * multiplier).toString())
                    }
                }
    }

    private fun setRelativeTargetPosition(motor: DcMotor, relativeInch: Double) {
        //      i in      IPR in    TPR tick
        // t = ─────── / ─────── * ──────────
        //        1        1 rot      1 rot
        val relativeTicks = relativeInch / INCHES_PER_REVOLUTION * TICKS_PER_REVOLUTION
        motor.targetPosition = motor.currentPosition + Math.round(relativeTicks).toInt()
        // TODO(debugging) not debugging
        RobotLog.ii(motor.connectionInfo, "C=${motor.currentPosition} T=$relativeTicks")
    }

    private fun forEachOf(vararg motors: IDrivetrain.MotorPtr, todo: (DcMotor) -> Unit) {
        motors.map(this::getMotor).forEach(todo)
    }

    private fun setMotorMode(mode: DcMotor.RunMode) {
        this.motors.values
                .forEach { it.mode = mode }
    }

    /**
     * Moves the robot according to the specified vector in default power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode).
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     */
    override fun move(vector: Vector2D) = this.move(vector, defaultPower)

    /**
     * Moves the robot according to the specified vector in the specified power.
     * Blocks until the movement is finished.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power  The power, [0.0, 1.0], to set the motor(s) to.
     */
    override fun move(vector: Vector2D, power: Double) {
        checkPower(power)
        while (this.isBusy);

        // Vector with endpoint as origin means no movement
        if (vector.x == 0.0 && vector.y == 0.0)
            return

        RobotLog.i("Moving to $vector")

        if (COUNT_USING_TIME) {
            setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)

            val waitTime = MOVE_MS_PER_INCH * vector.length() / power

            setMotorPowers(vector, power)
            Thread.sleep(waitTime.toLong())

            stop()
        } else {
            setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER)

            // Determine the positional targets for each motor pair
            directionToRelativeTargets(vector).forEach { (pair, position) ->
                // Set the relative target position of all motors in the pair
                pair.motors.forEach {
                    setRelativeTargetPosition(getMotor(it), position)
                }
            }

            setMotorMode(DcMotor.RunMode.RUN_TO_POSITION)
            setMotorPowers(vector, power)

            // TODO evaluate
            while (this.isBusy);
            stop()
        }
    }

    /**
     * Checks if any of the drivetrain motors are busy.
     *
     * @return True if any drivetrain motor is busy, otherwise false
     */
    override val isBusy: Boolean
        get() = this.motors.values.any {
            it.isBusy
        }

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * NOTE: Overrides existing power
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    override fun startMove(direction: Vector2D) {
        this.startMove(direction, defaultPower)
    }

    /**
     * Starts moving the robot at the given speed according to the specified direction.
     * NOTE: Overrides existing power
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     * @param power     Power, [0.0, 1.0], to set the necessary motors to
     */
    override fun startMove(direction: Vector2D, power: Double) {
        //this.setUsingEncoders(false)
        this.setMotorMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER)
        this.setMotorPowers(direction, power)
    }

    /**
     * Sets the power of all drivetrain motors to 0, thus stopping the robot.
     */
    override fun stop() {
        for (motor in this.motors.values) {
            motor.power = 0.0
        }
    }

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the default motor power.
     *
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     */
    override fun turn(radians: Double) = turn(radians, defaultPower)

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the given motor power.
     *
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     * @param power   The power multiplier to set the motor to, (0, 1]
     */
    override fun turn(radians: Double, power: Double) {
        if (radians == 0.0)
            return

        if (COUNT_USING_TIME) {
            this.setMotorMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER)
            val waitTime = TURN_MS_PER_CIRCLE * (Math.abs(radians) / 2 * Math.PI) / power

            val (leftPower, rightPower) = if (radians < 0.0) {
                power to -power
            } else {
                -power to power
            }

            forEachOf(IDrivetrain.MotorPtr.FRONT_RIGHT, IDrivetrain.MotorPtr.REAR_RIGHT) {
                it.power = rightPower
            }
            forEachOf(IDrivetrain.MotorPtr.FRONT_LEFT, IDrivetrain.MotorPtr.REAR_LEFT) {
                it.power = leftPower
            }

            Thread.sleep(waitTime.toLong())

            stop()
        } else {
            // RUN_USING_ENCODER first
            this.setMotorMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER)

            // Turn the radians into relative ticks for one side of the drivetrain, then the other side
            //   is the negation of that value.
            var tickMagnitude = Math.round(Math.abs(Angle.normalize(radians)) / (2 * Math.PI) * TICKS_PER_CIRCULAR_SPIN)

            // tickMagnitude is always applied to the right side because the unit circle is
            //   counter-clockwise. If the input value is positive, then tickMagnitude shall be negated.
            if (radians < 0.0)
                tickMagnitude *= -1

            // Wait for other motor operations to complete
            while (this.isBusy);

            forEachOf(
                    IDrivetrain.MotorPtr.FRONT_RIGHT,
                    IDrivetrain.MotorPtr.REAR_RIGHT
            ) {
                it.targetPosition = it.currentPosition + tickMagnitude.toInt()
                it.mode = DcMotor.RunMode.RUN_TO_POSITION
                it.power = power
            }

            forEachOf(
                    IDrivetrain.MotorPtr.FRONT_LEFT,
                    IDrivetrain.MotorPtr.REAR_LEFT
            ) {
                it.targetPosition = (it.currentPosition - tickMagnitude).toInt()
                it.mode = DcMotor.RunMode.RUN_TO_POSITION
                it.power = power
            }
        }
    }

    // Redundancy purposefully included to improve readability
    override fun startTurn(isCounterClockwise: Boolean) =
            if (isCounterClockwise) {
                startTurn(defaultPower)
            } else {
                startTurn(-defaultPower)
            }

    override fun startTurn(power: Double) {
        val validPower = Range.clip(power, -1.0, 1.0)

        this.setMotorMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER)

        if (power == 0.0) {
            return
        }

        forEachOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT,
                IDrivetrain.MotorPtr.REAR_RIGHT
        ) {

            it.power = validPower

        }

        forEachOf(
                IDrivetrain.MotorPtr.FRONT_LEFT,
                IDrivetrain.MotorPtr.REAR_LEFT
        ) {
            it.power = -validPower
        }
    }
    /**
     * Gets the DcMotor object at the specified position relative to the robot.
     *
     * @param ptr The motor's position relative to the robot
     * @return The DcMotor object representing the specified motor
     */
    override fun getMotor(ptr: IDrivetrain.MotorPtr): DcMotor = this.motors[ptr]!!
}
