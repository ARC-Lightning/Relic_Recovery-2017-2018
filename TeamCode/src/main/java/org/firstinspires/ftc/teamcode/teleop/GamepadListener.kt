package org.firstinspires.ftc.teamcode.teleop

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Provides a mechanism that stores the gamepad's state, allowing actions to be performed when a
 * given value of the gamepad *changes*.
 *
 * IMPORTANT: the `update` method should be called, ideally, repetitively to trap gamepad changes
 *            responsively.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 *
 * @constructor Listens for given GamepadProperties of the given Gamepad
 */
class GamepadListener(val pad: Gamepad, val rules: List<GamepadRule>) {

    /**
     * The state variable containing all necessary values. Each element's index corresponds to the same index in the keySet of this.rules.
     *
     * Example: Let rules be defined as follows: mapOf(
     *     {it.x} to {x -> telem.data("X value updated, now", x)}
     *     {it.y} to {y -> telem.data("Y value updated, now", y)}
     * ); the state list, being [true, false], means that previously {it.x} was activated and
     *                                                               {it.y} was not.
     */
    var state: MutableList<Boolean>

    init {
        // Initialize state
        state = fetchState().toMutableList()
    }

    /**
     * Checks the necessary values on the gamepad and determines if they have changed. If this is
     * true, then call the callbacks as defined in the constructor.
     *
     * NOTE: Should be called repeatedly (i.e. added to loop() or init_loop()) to catch gamepad changes.
     *
     */
    fun update() {
        fetchState()
                .zip(this.state)
                .forEachIndexed { index, (new, old) ->
                    if (new != old) {
                        // Change detected, call callback and update state
                        rules[index].second(new)
                        this.state[index] = new
                    }
                }
    }

    private fun fetchState(): List<Boolean> = rules.map { it.first(pad) }
}

/**
 * Describes a binary (or transformed to binary) property of a gamepad.
 *
 * Examples: `{ it.left_bumper }` or `{ it.left_trigger > 0.3 }`
 */
typealias GamepadProperty = (Gamepad) -> Boolean

/**
 * Describes what should be done when a GamepadProperty has changed.
 *
 * Examples: `Pair({ it.left_bumper }, { if (it) activateMotor() else deactivateMotor() })
 */
typealias GamepadRule = Pair<GamepadProperty, Action>

/**
 * Describes actions performed when there is a change in gamepad controls.
 * The argument will be the (new) value of the property to which this action corresponds in the
 *   GamepadListener constructor.
 *
 * Example: with GamepadListener(gamepad1, mapOf(
 *     {it.left_bumper} to {value -> telemetry.write("Update in value! left bumper now", value)}
 * )), "Update in value..." will show up on the driver station when the left bumper is pressed or released.
 */
typealias Action = (Boolean) -> Unit
