package org.firstinspires.ftc.teamcode.alcom

/**
 * Created by michael on 12/19/17.
 */
class GamepadBinaryInput(override val name: String, val input: () -> Boolean) : Input<Boolean> {
    var shadow = input()

    override fun get() = input()

    fun toggles(current: Boolean): Boolean {
        val newInput = input()
        if (newInput != shadow) {
            // Input was JUST NOW changed
            shadow = newInput
            // shadow current
            // true   true   -> false
            // true   false  -> true
            // false  true   -> true
            // false  false  -> false
            return shadow != current
        }
        return current
    }
}