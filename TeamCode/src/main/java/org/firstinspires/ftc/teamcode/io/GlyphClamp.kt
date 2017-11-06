package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.telemetry.ITelemetry

/**
 * Controls the clamp of glyphs.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 *
 * @constructor Creates a clamp controller with the given servos
 */
class GlyphClamp(var leftServo: Servo, var rightServo: Servo, private val telem: ITelemetry) {

    // Constructor
    // Since the servos have the same orientation, one has to be reversed in order for the clamp to
    //   move symmetrically.
    init {
        leftServo.direction = Servo.Direction.FORWARD
        rightServo.direction = Servo.Direction.REVERSE
    }

    // Positions
    private val leftPositions = object {
        val open = 0.0
        val clamping = 0.25
    }
    private val rightPositions = object {
        val open = 0.6
        val clamping = 0.85
    }

    // Store the status on our own so we do not flood the servo with getPosition requests
    private var isLeftClamping: Boolean = false
    private var isRightClamping: Boolean = false

    // Public getter/setter
    // Usage example:
    //   - Set: Use `clamp.leftArm = gamepad1.left_bumper`
    //   - Query: Use `clamp.leftArm`
    var leftArm: Boolean
        get() = this.isLeftClamping
        set(newStatus) {
            if (newStatus != this.isLeftClamping) {
                this.leftServo.position = if (newStatus) leftPositions.clamping else leftPositions.open
                this.isLeftClamping = newStatus
                telem.write("GlyphClamp", "Left clamp now ${if (newStatus) "clamping" else "relaxed"}")
            }
        }
    var rightArm: Boolean
        get() = this.isRightClamping
        set(newStatus) {
            if (newStatus != this.isRightClamping) {
                this.rightServo.position = if (newStatus) rightPositions.clamping else rightPositions.open
                this.isRightClamping = newStatus
                telem.write("GlyphClamp", "Right clamp now ${if (newStatus) "clamping" else "relaxed"}")
            }
        }
}