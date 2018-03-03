package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.config.ConfigUser

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
    val config = Config()
    class Config : ConfigUser("GlyphManipulator/config.properties") {
        val rectMax = file.getDouble("RectifierMax")
        val rectMin = file.getDouble("RectifierMin")
        val leftCollectorPower = file.getDouble("LeftCollectorPower")
        val rightCollectorPower = file.getDouble("RightCollectorPower")
        val pourMax = file.getDouble("PourMax")
        val pourMin = file.getDouble("PourMin")
        val pourTime = file.getInteger("PourTime")
        val pourIntermediate = file.getDouble("PourIntermediate")
    }


    // Shadow values for avoiding unnecessary calls to hardware
    // Values to apply during initialization
    private var _collectorPower: Double = 0.0   // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _bucketPourPos: Double = 0.0    // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _rectifierPos: Double = config.rectMax

    // Initialization should take place to ensure that shadow values and hardware state match
    // before applyState is called.
    init {
        // Apply scaleRange
        bucketPour.scaleRange(config.pourMin, config.pourMax)
        offsideBucketPour.scaleRange(config.pourMin, config.pourMax)
        glyphRectifiers.forEach { it.scaleRange(config.rectMin, config.rectMax) }

        applyState()
        Hardware.telemetry.write("GlyphManipulator", "Initialized")
    }

    /**
     * Applies the values of all shadow variables to hardware.
     */
    private fun applyState() {
        collectorLeft.power = _collectorPower * config.leftCollectorPower
        collectorRight.power = _collectorPower * config.rightCollectorPower
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

    override fun placeGlyph() {
        bucketPourPos = config.pourIntermediate
        Thread.sleep((config.pourTime * config.pourIntermediate).toLong() + 100)
        bucketPourPos = config.pourMax
    }

    // FIXME Redundancy - Factory functions?
    // Furthermore, this pattern can be found in some other hardware devices. It would be ideal to
    // generalize this pattern and reduce redundancy.
}