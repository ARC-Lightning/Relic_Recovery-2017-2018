package org.firstinspires.ftc.teamcode.autonomous

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark

/**
 * Describes methods provided by the autonomous navigation module for OpMode use.
 *
 * @author Michael Peng
 * For team: 4410
 *
 * FIRST - Gracious Professionalism
 */
interface IAutoNav {
    // The implementation should use info from DynamicConfig to determine the final drivetrain
    //   instructions.

    /**
     * Moves from the starting point to the position in which jewel knocking is performed.
     */
    fun beginJewelKnock()

    /**
     * Returns from the jewel-knocking position to the starting point.
     */
    fun endJewelKnock()

    /**
     * Moves/turns from the starting position. to the position in which the phone's camera faces the VuMark.
     */
    fun beginReadingVuMark()

    /**
     * Moves/turns from the position in which the phone's camera faces the VuMark to the starting position.
     */
    fun endReadingVuMark()

    /**
     * Moves from the starting point to the position in which a glyph may be scored to the given
     * column of the appropriate cryptobox.
     */
    fun goToCryptoBox(vuMark: RelicRecoveryVuMark)

    /**
     * Moves from the given column of the appropriate cryptobox back to the starting point.
     */
    fun returnFromCryptoBox(vuMark: RelicRecoveryVuMark)
}