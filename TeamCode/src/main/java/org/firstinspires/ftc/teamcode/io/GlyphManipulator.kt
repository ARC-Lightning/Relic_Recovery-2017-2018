package org.firstinspires.ftc.teamcode.io

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo

/**
 * A generic implementation of `IGlyphManipulator`.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
class GlyphManipulator(
        override val collectors: Set<DcMotor>,
        bucketLiftLeft: DcMotor,
        bucketLiftRight: DcMotor,
        override val bucketPour: Servo,
        override val bucketClamp: Servo,
        override val collectorFolder: Servo) : IGlyphManipulator {

    override val bucketLifts: Set<DcMotor> = setOf(bucketLiftLeft, bucketLiftRight)

    // CONFIGURATIONS
    companion object {
        // Collector folder servo position when unfolded
        val UNFOLDED_POS = 1.0

        // Bucket clamp servo position when clamping
        val CLAMPING_POS = 0.7
        // Bucket clamp servo position when released
        val UNCLAMPING_POS = 0.5

        // Bucket pouring servo position when pouring
        val POURING_POS = 1.0
        // Bucket pouring servo position when flat
        val UNPOURING_POS = 0.5
    }

    // Shadow values for avoiding unnecessary calls to hardware
    private var _collectorPower: Double = 0.0   // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _liftPower: Double = 0.0        // DO NOT CHANGE INITIAL VALUE - DANGEROUS
    private var _bucketPouring: Boolean = false
    private var _bucketClamping: Boolean = false

    // Initialization should take place to ensure that shadow values and hardware state matches
    // before applyState is called.
    init {
        applyState()

        // MOTOR-RUINING DANGER! Mirroring & connected lift motors require one to be reversed
        bucketLiftRight.direction = DcMotorSimple.Direction.REVERSE

        Hardware.telemetry.write("GlyphManipulator", "Initialized")
    }

    /**
     * Applies the values of all shadow variables to hardware.
     */
    private fun applyState() {
        collectors.forEach { it.power = _collectorPower }
        bucketLifts.forEach { it.power = _liftPower }
        bucketPour.position = if (_bucketPouring) POURING_POS else UNPOURING_POS
        bucketClamp.position = if (_bucketClamping) CLAMPING_POS else UNCLAMPING_POS
    }

    override var collectorPower: Double
        get() = _collectorPower
        set(value) {
            _collectorPower = value
            applyState()
        }

    override var liftPower: Double
        get() = _liftPower
        set(value) {
            _liftPower = value
            applyState()
        }

    override fun unfoldCollector() {
        collectorFolder.position = UNFOLDED_POS
    }

    override var bucketPouring: Boolean
        get() = _bucketPouring
        set(value) {
            _bucketPouring = value
            applyState()
        }

    override var bucketClamping: Boolean
        get() = _bucketClamping
        set(value) {
            _bucketClamping = value
            applyState()
        }

    // FIXME Redundancy - Factory functions?
    // Furthermore, this pattern can be found in some other hardware devices. It would be ideal to
    // generalize this pattern and reduce redundancy.
}