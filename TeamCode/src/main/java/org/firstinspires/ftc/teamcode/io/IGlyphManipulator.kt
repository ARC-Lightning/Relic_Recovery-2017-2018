package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

/**
 * Describes tasks that the glyph manipulator (collectorIn + platform) is capable of doing.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
interface IGlyphManipulator {

    // Hardware output devices
    val collectors: Set<DcMotor>
    val bucketLift: DcMotor
    val bucketPour: Servo
    val bucketClamp: Servo
    val collectorFolder: Servo
    val collectorHugger: Servo

    // Getter/setter manipulations
    /**
     * The power of motors that run the flywheels that actuate the glyph through the feeder mechanism.
     * When set to a positive value, the flywheels pull the glyph toward the bucket.
     * When set to a negative value, the flywheels push the glyph out of the feeder.
     */
    var collectorPower: Double
    /**
     * The power of the bucket lift motors.
     * When set to a positive value, the bucket raises.
     * When set to a negative value, the bucket lowers.
     */
    var liftPower: Double

    /**
     * Unfolds the collectorIn flywheels. Best performed right after the start of a game.
     * Once unfolded, this method has no effect.
     */
    fun unfoldCollector()

    /**
     * Binary controls for the bucket-tilting servo.
     *
     * `true` = vertical, `false` = flat
     *
     */
    // A shadow variable is recommended for implementation.
    var bucketPouring: Boolean

    /**
     * Binary controls for the clamp of the bucket.
     *
     * `true` = pressing against glyph, `false` = released, not holding glyph
     *
     */
    // A shadow variable is recommended for implementation.
    var bucketClamping: Boolean

    var collectorHugging: Boolean
}