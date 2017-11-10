package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
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
class GlyphClamp(
        var leftServo: Servo,
        var rightServo: Servo,
        var lift: DcMotor?,
        private val telem: ITelemetry) {

    // Positions
    private val leftPositions = object {
        val open = 0.0
        val clamping = 0.2
    }
    private val rightPositions = object {
        val open = 1.0
        val clamping = 0.8
    }

    // Reset when initializing
    init {
        leftServo.position = leftPositions.open
        rightServo.position = rightPositions.open
    }

    // Store the status on our own so we do not flood the servo with getPosition requests
    private var isLeftClamping: Boolean = false
    private var isRightClamping: Boolean = false

    // True if clamping
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

    // In TeleOp's mappings, simply use `clamp.liftPower = gamepad1.stick_left_y` or similar
    var liftPower: Double
        get() = this.lift!!.power
        set(newPower) {
            this.lift!!.power = newPower
        }
}