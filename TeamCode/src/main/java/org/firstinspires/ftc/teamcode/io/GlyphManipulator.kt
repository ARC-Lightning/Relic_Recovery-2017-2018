package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.io.IGlyphManipulator.Companion.POUR_MAXIMUM
import org.firstinspires.ftc.teamcode.io.IGlyphManipulator.Companion.POUR_MINIMUM

/**
 * A generic implementation of `IGlyphManipulator`.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class GlyphManipulator(
        override val collectorLeft: DcMotor,
        override val collectorRight: DcMotor,
        override val bucketPour: Servo,
        override val offsideBucketPour: Servo,
        override val glyphRectifiers: Set<Servo>) : IGlyphManipulator {

    // CONFIGURATIONS
    companion object {

        // Side-dependent collector power multipliers
        val LEFT_COLLECTOR_POWER = 1.0
        val RIGHT_COLLECTOR_POWER = 1.1

        // Rectifier servo positions
        val RECT_MAXIMUM = 1.0
        val RECT_MINIMUM = 0.5
    }

    // Shadow values for avoiding unnecessary calls to hardware
    // Values to apply during initialization
    private var _collectorPower: Double = 0.0   // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _bucketPourPos: Double = 0.0    // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _rectifierPos: Double = RECT_MAXIMUM

    // Initialization should take place to ensure that shadow values and hardware state match
    // before applyState is called.
    init {
        // Apply scaleRange
        bucketPour.scaleRange(POUR_MINIMUM, POUR_MAXIMUM)
        offsideBucketPour.scaleRange(POUR_MINIMUM, POUR_MAXIMUM)
        glyphRectifiers.forEach { it.scaleRange(RECT_MINIMUM, RECT_MAXIMUM) }

        applyState()
        Hardware.telemetry.write("GlyphManipulator", "Initialized")
    }

    /**
     * Applies the values of all shadow variables to hardware.
     */
    private fun applyState() {
        collectorLeft.power = _collectorPower * LEFT_COLLECTOR_POWER
        collectorRight.power = _collectorPower * RIGHT_COLLECTOR_POWER
        bucketPour.position = _bucketPourPos
        offsideBucketPour.position = _bucketPourPos
        glyphRectifiers.forEach { it.position = _rectifierPos }
    }

    override var collectorPower: Double
        get() = _collectorPower
        set(value) {
            _collectorPower = value
            applyState()
        }

    override var bucketPourPos: Double
        get() = _bucketPourPos
        set(value) {
            _bucketPourPos = Range.clip(value, 0.0, 1.0)
            applyState()
        }

    override var rectifierPos: Double
        get() = _rectifierPos
        set(value) {
            _rectifierPos = Range.clip(value, 0.0, 1.0)
            applyState()
        }

    // FIXME Redundancy - Factory functions?
    // Furthermore, this pattern can be found in some other hardware devices. It would be ideal to
    // generalize this pattern and reduce redundancy.
}