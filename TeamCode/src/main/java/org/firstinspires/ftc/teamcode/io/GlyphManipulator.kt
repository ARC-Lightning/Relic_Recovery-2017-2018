package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.config.ConfigFile

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
    private val config = ConfigFile("GlyphManipulator/config.properties")
    private val rectMax             = config.getDouble("RectifierMax")
    private val rectMin             = config.getDouble("RectifierMin")
    private val leftCollectorPower  = config.getDouble("LeftCollectorPower")
    private val rightCollectorPower = config.getDouble("RightCollectorPower")
    private val pourMax             = config.getDouble("PourMax")
    private val pourMin             = config.getDouble("PourMin")


    // Shadow values for avoiding unnecessary calls to hardware
    // Values to apply during initialization
    private var _collectorPower: Double = 0.0   // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _bucketPourPos: Double = 0.0    // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _rectifierPos: Double = rectMax

    // Initialization should take place to ensure that shadow values and hardware state match
    // before applyState is called.
    init {
        // Apply scaleRange
        bucketPour.scaleRange(pourMin, pourMax)
        offsideBucketPour.scaleRange(pourMin, pourMax)
        glyphRectifiers.forEach { it.scaleRange(rectMin, rectMax) }

        applyState()
        Hardware.telemetry.write("GlyphManipulator", "Initialized")
    }

    /**
     * Applies the values of all shadow variables to hardware.
     */
    private fun applyState() {
        collectorLeft.power = _collectorPower * leftCollectorPower
        collectorRight.power = _collectorPower * rightCollectorPower
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