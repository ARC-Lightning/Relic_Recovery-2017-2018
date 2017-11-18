package org.firstinspires.ftc.teamcode.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range
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
        private val powerSetting: Double,
        /**
         * A mapping from MotorPtrs to DcMotor instances.
         */
        private val motors: Map<IDrivetrain.MotorPtr, DcMotor>) : IDrivetrain {

    // Reverse the direction of motors on the right.
    init {
        forEachOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT,
                IDrivetrain.MotorPtr.REAR_RIGHT
        ) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }
    }

    // CONFIGURATION
    companion object {
        private val TICKS_PER_ROTATION = 1440.0
        private val INCHES_PER_ROTATION = 0.8
        private val TICKS_PER_CIRCULAR_SPIN = TICKS_PER_ROTATION * 4
    }
    // END CONFIGURATION

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

    /**
     * Turns the given vector into one that is in VectorDirection.
     * Creates a clone.
     * @param vec The vector to be normalized
     * @return The normalized vector
     */
    private fun normalize(vec: Vector2D): Vector2D {
        val x = vec.x
        val y = vec.y
        // Ternary in use to avoid Division By Zero
        return Vector2D(if (x == 0.0) 0.0 else Math.abs(x) / x, if (y == 0.0) 0.0 else Math.abs(y) / y)
    }

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
                    // TUDO(remove) bad debugging method below
                    // Hardware.instance!!.telemetry.data(mapping.key.displayName, mapping.value * multiplier)
                    forEachOf(*mapping.key.motors) {
                        it.power = mapping.value * multiplier
                    }
                }
    }

    private fun setRelativeTargetPosition(motor: DcMotor, relativeInch: Double) {
        //      i in      IPR in    TPR tick
        // t = ─────── / ─────── * ──────────
        //        1        1 rot      1 rot
        val relativeTicks = relativeInch / INCHES_PER_ROTATION * TICKS_PER_ROTATION
        motor.targetPosition = motor.currentPosition + Math.round(relativeTicks).toInt()
    }

    private fun normalizeRadian(radian_: Double): Double {
        var radian: Double = radian_
        // Until the radian is within range, make it closer to zero by leaps of 2pi.
        while (radian > 2 * Math.PI || radian < -2 * Math.PI) {
            radian += if (radian < 0)
            // Add 2pi if radian negative
                2 * Math.PI
            else
            // Subtract 2pi if radian positive
                -2 * Math.PI
        }
        return radian
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
    override fun move(vector: Vector2D) = this.move(vector, powerSetting)

    /**
     * Moves the robot according to the specified vector in the specified power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power  The power, [0.0, 1.0], to set the motor(s) to.
     */
    override fun move(vector: Vector2D, power: Double) {
        // TODO test synthetic movement
        checkPower(power)
        while (this.isBusy);

        // Vector with endpoint congruent to origin means no movement
        if (vector.x == 0.0 && vector.y == 0.0)
            return

        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER)

        directionToRelativeTargets(vector).forEach { (pair, position) ->
            pair.motors.forEach {
                setRelativeTargetPosition(getMotor(it), position)
            }
        }

        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION)
        setMotorPowers(vector, power)
    }

    /**
     * Checks if any of the drivetrain motors are busy.
     *
     * @return True if any drivetrain motor is busy, otherwise false
     */
    override fun isBusy(): Boolean = this.motors.values.any { it.isBusy }

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * NOTE: Overrides existing power
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    override fun startMove(direction: Vector2D) {
        this.startMove(direction, powerSetting)
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
        this.setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
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
    override fun turn(radians: Double) = turn(radians, powerSetting)

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

        // Turn the radians into relative ticks for one side of the drivetrain, then the other side
        //   is the negation of that value.
        var tickMagnitude = Math.round(Math.abs(normalizeRadian(radians)) / (2 * Math.PI) * TICKS_PER_CIRCULAR_SPIN)

        // tickMagnitude is always applied to the right side because the unit circle is
        //   counter-clockwise. If the input value is negative, then tickMagnitude shall be negated.
        if (radians < 0.0)
            tickMagnitude *= -1

        forEachOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT,
                IDrivetrain.MotorPtr.REAR_RIGHT
        ) {
            it.targetPosition = (it.currentPosition + tickMagnitude).toInt()
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

    override fun startTurn(isCounterClockwise: Boolean) {
        // Redundancy purposefully included to improve readability
        if (isCounterClockwise)
            startTurn(powerSetting)
        else
            startTurn(-powerSetting)
    }

    override fun startTurn(power_: Double) {
        val power = Range.clip(power_, -1.0, 1.0)

        forEachOf(
                IDrivetrain.MotorPtr.FRONT_RIGHT,
                IDrivetrain.MotorPtr.REAR_RIGHT
        ) {

            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            it.power = power

        }

        forEachOf(
                IDrivetrain.MotorPtr.FRONT_LEFT,
                IDrivetrain.MotorPtr.REAR_LEFT
        ) {
            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            it.power = -power
        }
    }
    /**
     * Gets the DcMotor object at the specified position relative to the robot.
     *
     * @param ptr The motor's position relative to the robot
     * @return The DcMotor object representing the specified motor
     */
    override fun getMotor(ptr: IDrivetrain.MotorPtr): DcMotor = this.motors[ptr]!!

    override fun getDefaultPower(): Double = this.powerSetting
}
