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
class GlyphClamp(var left: Servo, var right: Servo, val telem: ITelemetry) {

    // Constructor
    // Since the servos have the same orientation, one has to be reversed in order for the clamp to
    //   move symmetrically.
    init {
        left.direction = Servo.Direction.FORWARD
        right.direction = Servo.Direction.REVERSE
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

    fun setLeftClamping(newStatus: Boolean): Boolean {
        if (newStatus != this.isLeftClamping) {
            this.left.position = if (newStatus) leftPositions.clamping else leftPositions.open
            this.isLeftClamping = newStatus
            telem.write("GlyphClamp", "Left clamp now ${if (newStatus) "clamping" else "relaxed"}")
            return true
        }
        return false
    }

    fun setRightClamping(newStatus: Boolean): Boolean {
        if (newStatus != this.isRightClamping) {
            this.right.position = if (newStatus) rightPositions.clamping else rightPositions.open
            this.isRightClamping = newStatus
            telem.write("GlyphClamp", "Right clamp now ${if (newStatus) "clamping" else "relaxed"}")
            return true
        }
        return false
    }
}