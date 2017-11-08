package org.firstinspires.ftc.teamcode.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import org.locationtech.jts.math.Vector2D
import java.util.*

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

    // CONFIGURATION
    companion object {
        private val TICKS_PER_ROTATION = 1440.0
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
    private enum class MotorDiagonalPair(ptr1: IDrivetrain.MotorPtr, ptr2: IDrivetrain.MotorPtr) {
        RIGHT(IDrivetrain.MotorPtr.FRONT_LEFT, IDrivetrain.MotorPtr.REAR_RIGHT),
        LEFT(IDrivetrain.MotorPtr.FRONT_RIGHT, IDrivetrain.MotorPtr.REAR_LEFT);

        val motors: Array<IDrivetrain.MotorPtr> = arrayOf(ptr1, ptr2)

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
    @Throws(ArrayIndexOutOfBoundsException::class)
    private fun getMotorPowerFromVector(vec: Vector2D): Map<MotorDiagonalPair, Int> {

        if (Math.abs(vec.x) > 1 || Math.abs(vec.y) > 1) {
            throw ArrayIndexOutOfBoundsException(vec.toString())
        }

        val clone = Vector2D(vec)
        clone.rotate(-45.0)

        val output = HashMap<MotorDiagonalPair, Int>()
        output.put(MotorDiagonalPair.RIGHT, Math.round(clone.x).toInt())
        output.put(MotorDiagonalPair.LEFT, Math.round(clone.y).toInt())

        return output
    }

    /**
     * Turns the given vector into one that is in VectorDirection.
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
            throw RuntimeException("power cannot be zero, the robot will not move")
        }
        if (power < 0) {
            throw RuntimeException("power cannot be negative, robot will move in opposite direction")
        }
    }

    // Same as startMove(), except without mode setting. Universal across encoder and non-encoder.
    private fun setMotorPowers(direction: Vector2D, multiplier: Double) {
        // For each motor, check which diagonal pair it's in; Then set the motor's power to the
        // multiplier value corresponding to that diagonal pair in the `powers` map.
        for ((ptr, motor) in this.motors) {
            MotorDiagonalPair.values()
                    .filter { it.motors.contains(ptr) }
                    .forEach { motor.power = this.getMotorPowerFromVector(direction)[it]!! * multiplier }
        }
    }

    private fun setRelativeTargetPosition(motor: DcMotor, relativeInch: Double) {
        val TICKS_PER_INCH = 400.0
        val relativeTicks = relativeInch * TICKS_PER_INCH
        motor.targetPosition = motor.currentPosition + Math.round(relativeTicks).toInt()
    }

    /**
     * Moves the robot according to the specified vector in default power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode).
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     */
    override fun move(vector: Vector2D) {
        this.move(vector, powerSetting)
    }

    /**
     * Moves the robot according to the specified vector in the specified power.
     * If any motor in the drivetrain is busy when this is called, it will block until no motors are busy.
     * Ideal for Autonomous (LinearOpMode)
     *
     * @param vector The vector to move the robot in. See comment above for how it works.
     * @param power  The power, [0.0, 1.0], to set the motor(s) to.
     */
    override fun move(vector: Vector2D, power: Double) {
        checkPower(power)
        while (this.isBusy);

        for (motor in this.motors.values) {
            motor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }

        val normalized = normalize(vector)
        val powers = getMotorPowerFromVector(normalized)

        for (pair in MotorDiagonalPair.values()) {
            for (ptr in pair.motors) {
                setRelativeTargetPosition(this.motors[ptr]!!, powers[pair]!! * vector.length())
            }
        }
        for (motor in this.motors.values) {
            motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        }
        setMotorPowers(normalized, power)
    }

    /**
     * Checks if any of the drivetrain motors are busy.
     *
     * @return True if any drivetrain motor is busy, otherwise false
     */
    override fun isBusy(): Boolean {
        for (motor in this.motors.values) {
            if (motor.isBusy) {
                return true
            }
        }
        return false
    }

    /**
     * Starts moving the robot at the default speed according to the specified direction.
     * NOTE: Is ineffective if motors are busy
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     */
    override fun startMove(direction: Vector2D) {
        this.startMove(direction, powerSetting)
    }

    /**
     * Starts moving the robot at the given speed according to the specified direction.
     * NOTE: Is ineffective if motors are busy
     * Ideal for TeleOp (OpMode)
     *
     * @param direction A vector from and only from {@see VectorDirection}.
     * @param power     Power, [0.0, 1.0], to set the necessary motors to
     */
    override fun startMove(direction: Vector2D, power: Double) {

        // Cancel if motors are busy already
        if (isBusy)
            return

        for (motor in motors.values) {
            motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }

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
    override fun turn(radians: Double) {
        // TODO stub
    }

    /**
     * Turns the robot in position for the given amount of radians (of change applied to the robot's
     * orientation) at the given motor power.
     *
     * @param radians The amount of radians to rotate the robot for, [-2π, 2π]
     * @param power   The power multiplier to set the motor to, (0, 1]
     */
    override fun turn(radians: Double, power: Double) {
        // Turning has a special motor configuration

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
