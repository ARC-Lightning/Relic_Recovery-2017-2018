package org.firstinspires.ftc.teamcode.drivetrain

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.Range
import com.qualcomm.robotcore.util.RobotLog
import org.firstinspires.ftc.teamcode.config.ConfigUser
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
        // Reverse the direction of motors on the left.
        forEachOf(
                IDrivetrain.MotorPtr.FRONT_LEFT,
                IDrivetrain.MotorPtr.REAR_LEFT
        ) {
            it.direction = DcMotorSimple.Direction.REVERSE
        }

        // Preciseness of movement is crucial in AcsNavigator.
        // Don't let the motors drift.
        motors.values.forEach { it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE }

        // Reset the encoders.
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
    }

    // CONFIGURATION
    class Config : ConfigUser("Drivetrain/config.properties") {

        val ticksPerRevolution      = file.getInteger("TicksPerRevolution")
        val inchesPerRevolution     = file.getDouble("InchesPerRevolution")
        val ticksPerCircularSpin    = file.getInteger("TicksPerCircularSpin")
        val countUsingTime          = file.getBoolean("CountUsingTime")
        val msPerMovedInch          = file.getInteger("MsPerMovedInch")
        val msPerCircularSpin       = file.getInteger("MsPerCircularSpin")
        val precisePowerMultiplier  = file.getDouble("PrecisePowerMultiplier")
    }

    private val config = Config()
    // END CONFIGURATION

    /**
     * When true, this multiplies the final power output of the motors by a value specified in the config
     * (as "PrecisePowerMultiplier"). This feature intends to help the driver make precise movements
     * when necessary, such as when unloading glyphs.
     */
    override var isUsingPrecisePower: Boolean = false
    private val preciseMultiplier: Double
            get() = if (isUsingPrecisePower) config.precisePowerMultiplier else 1.0

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

        Power calculation:
         - Rotate vector 45 deg
         - Scale vector to have absolute value of at least one component be 1 ((-1, -0.4), (0.3, 1))
         - Multiply vector by given power value
         - Assign components to diagonal pairs

        Target position calculation:
         - Rotate vector 45 deg
         - Convert both components from inches to revolutions, then to ticks
         - Assign converted ticks to diagonal pairs

        Duration calculation:
         - Rotate vector 45 deg
         - Convert both components from inches to milliseconds, then divide both by power value
         - Sleep for converted milliseconds (then stop motors)
     */

    /**
     * Calculates a mapping from the diagonal pairs of motors to their power from
     * any arbitrary vector.
     * @param vec The vector indicating the direction to move in
     * @param power Power multiplier, (0, 1]
     * @return A mapping from a diagonal pair of motors to their desired power
     */
    private fun getMovementPowers(vec: Vector2D, power: Double): Map<MotorDiagonalPair, Double> {
        val directionPowers = normalize(vec.rotate(Angle.toRadians(315.0))).multiply(power)
        return mapOf(
                MotorDiagonalPair.RIGHT to directionPowers.x,
                MotorDiagonalPair.LEFT to directionPowers.y
        )
    }

    // Power has range [-1, 1]
    // Positive power means clockwise turning
    private fun getTurnPowers(power: Double): Map<IDrivetrain.MotorPtr, Double> {
        if (power == 0.0) {
            // No turning, 0 power
            return this.motors.keys.map { it to power }.toMap()
        }
        return mapOf(
                IDrivetrain.MotorPtr.REAR_LEFT to power,
                IDrivetrain.MotorPtr.FRONT_LEFT to power,
                IDrivetrain.MotorPtr.REAR_RIGHT to -power,
                IDrivetrain.MotorPtr.FRONT_RIGHT to -power
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
     * Scales the given vector proportionally into one whose max abs(component) is 1.
     * Creates a clone.
     * @param vec The vector to be normalized
     * @return The normalized vector
     */
    private fun normalize(vec: Vector2D): Vector2D {
        if (vec.x == 0.0 && vec.y == 0.0) {
            return Vector2D(vec)
        }
        return vec.divide(Math.max(Math.abs(vec.x), Math.abs(vec.y)))
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
        // Grab the pair -> power properties, then set the power of each motor in each pair to its mapped
        // value.
        getMovementPowers(direction, multiplier).entries
                // For each pair -> power entry
                .forEach { mapping ->
                    forEachOf(*mapping.key.motors) {
                        it.power = mapping.value
                        RobotLog.ii(mapping.key.displayName, mapping.value.toString())
                    }
                }
    }

    private fun setRelativeTargetPosition(motor: DcMotor, relativeInch: Double) {
        //      i in      IPR in    TPR tick
        // t = ─────── / ─────── * ──────────
        //        1        1 rot      1 rot
        val relativeTicks = relativeInch / config.inchesPerRevolution * config.ticksPerRevolution
        motor.targetPosition = motor.currentPosition + Math.round(relativeTicks).toInt()
        RobotLog.dd(motor.connectionInfo, "POS_SET C=${motor.currentPosition} T=$relativeTicks")
    }

    private fun forEachOf(vararg motors: IDrivetrain.MotorPtr, todo: (DcMotor) -> Unit) {
        motors.map(this::getMotor).forEach(todo)
    }

    private fun setMotorMode(mode: DcMotor.RunMode) {
        this.motors.values
                .forEach { it.mode = mode }
    }

    private fun dissolvePairMap(map: Map<MotorDiagonalPair, Double>):
            Map<IDrivetrain.MotorPtr, Double> =
            map.entries.map { (pair, power) -> pair.motors.map { it to power } }.flatten().toMap()

    private fun normalizeDoubleCircle(radians: Double): Double =
        Angle.normalizePositive(Math.abs(radians)) * (if (radians < 0.0) -1 else 1)

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

        // Vector with endpoint as origin means no movement
        if (vector.x == 0.0 && vector.y == 0.0)
            return

        RobotLog.i("Moving to $vector")

        if (config.countUsingTime) {
            setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)

            val waitTime = config.msPerMovedInch * vector.length() / power

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
    override fun startMove(direction: Vector2D) =
        this.startMove(direction, defaultPower)


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
        this.setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
        this.setMotorPowers(direction, power * preciseMultiplier)
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

        if (config.countUsingTime) {
            this.setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
            val waitTime = config.msPerCircularSpin * (Math.abs(radians) / 2 * Math.PI) / power

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
            this.setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER)

            // Turn the radians into relative ticks for one side of the drivetrain, then the other side
            //   is the negation of that value.
            val tickMagnitude = Math.round(normalizeDoubleCircle(radians) / (2 * Math.PI) * config.ticksPerCircularSpin)

            // !! Used to negate if below 0.0

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

            while (this.isBusy);
            stop()
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
        val validPower = Range.clip(power, -1.0, 1.0) * preciseMultiplier

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

    override fun actuate(movement: Vector2D, power: Double, turnClockwise: Boolean,
                         turnPower: Double) {
        if (power == 0.0 && turnPower == 0.0) {
            stop()
            return
        }

        // Step 1: Get the movement powers
        val powers = dissolvePairMap(getMovementPowers(movement, power)).toMutableMap()

        // Step 2: Adjust by the turn powers
        val turnPowers = getTurnPowers(
                if (turnClockwise) turnPower else -turnPower)
        powers.entries.forEach { (ptr, pwr) -> powers[ptr] = pwr + turnPowers[ptr]!! }

        // Step 3: Scale to [-1, 1] if not in limits
        val scale = powers.values.map(Math::abs).max() ?: 0.0
        if (scale > 1.0) {
            powers.entries.forEach { (ptr, pwr) -> powers[ptr] = pwr / scale }
        }

        // Step 4: Assign powers to motors, with preciseMultiplier
        setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER)
        for ((ptr, pwr) in powers) {
            this.getMotor(ptr).power = pwr * preciseMultiplier
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
