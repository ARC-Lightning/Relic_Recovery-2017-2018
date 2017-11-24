package org.firstinspires.ftc.teamcode.io

/**
 * Describes functions that the GlyphClamp class should implement and its external behavior.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
@Deprecated(message = "This design has been abandoned by the team in favor of a more radical design.")
interface IGlyphClamp {

    /**
     * Represents the left arm of the clamp.
     * Getter gets current status (true if clamping), setter moves servo to the new status
     */
    var leftArm: Boolean

    /**
     * Represents the right arm of the clamp.
     * Getter gets current status (true if clamping), setter moves servo to the new status
     */
    var rightArm: Boolean

    /**
     * Wraps the lift motor's getPower() and setPower() methods.
     */
    var liftPower: Double
}