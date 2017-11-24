package org.firstinspires.ftc.teamcode.autonomous

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark

/**
 * Describes functions that the Vuforia wrapper shall implement and what it should be able to do.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
interface IVuforia {

    /**
     * Initiate the tracking.
     * Should call trackables.activate().
     */
    fun startTracking()

    /**
     * Tries to read the VuMark that may be shown on camera.
     * May call RelicRecoveryVuMark.from(template).
     *
     * @return The VuMark that was read
     */
    fun readVuMark(): RelicRecoveryVuMark

    /**
     * Stop the tracking.
     * No clean-up code was found in the example class file. This function may be empty.
     */
    fun stopTracking()
}