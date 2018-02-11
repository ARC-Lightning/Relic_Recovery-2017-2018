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

    companion object {
        // Maximum/minimum servo position for BucketPours, designed to protect the bot
        // bucketPour & offsideBucketPour will have a scaleRange that corresponds to these values
        val POUR_MAXIMUM = 1.0
        val POUR_MINIMUM = 0.0
    }

    // Hardware output devices
    val collectorLeft: DcMotor
    val collectorRight: DcMotor
    val bucketPour: Servo
    val offsideBucketPour: Servo
    val glyphRectifiers: Set<Servo>

    // Getter/setter manipulations
    /**
     * The power of motors that run the flywheels that actuate the glyph through the feeder mechanism.
     * When set to a positive value, the flywheels pull the glyph toward the bucket.
     * When set to a negative value, the flywheels push the glyph out of the feeder.
     */
    var collectorPower: Double

    /**
     * Analog controls for the bucket-tilting servos.
     *
     * POURING_POS represents the position when the platform is lifted.
     * UNPOURING_POS represents the position when the platform is flat.
     *
     */
    // A shadow variable is recommended for implementation.
    var bucketPourPos: Double

    /**
     * Analog controls for the rectifier servos.
     *
     * RECTIFYING_POS represents the position when the rectifier is engaged.
     * UNRECTIFYING_POS represents the position when the rectifier is out of the way.
     */
    var rectifierPos: Double
}