package org.firstinspires.ftc.teamcode.alcom

/**
 * Describes a general interface that should be implemented by all available Input columns.
 */
interface Input<out T> {
    /**
     * The display name of this input device, used in telemetry messages.
     */
    val name: String

    /**
     * Get the value of the input **at the time of calling this function**
     */
    fun get(): T
}