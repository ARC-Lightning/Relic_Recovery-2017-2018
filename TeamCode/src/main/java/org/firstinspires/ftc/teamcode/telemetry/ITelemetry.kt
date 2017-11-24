package org.firstinspires.ftc.teamcode.telemetry

/**
 * Defines the ways in which the other parts of the program can access the telemetry features.
 * Ideally, no direct access to the "telemetry" field in the OpMode classes should be permitted
 * in any other part of the program except when initializing ITelemetry.
 * Structurally similar to logging by storing the history of telemetry messages by default.
 *
 * @author Michael Peng
 * For team: 4410 (Lightning)
 *
 * FIRST - Gracious Professionalism
 */
interface ITelemetry {

    /**
     * Determines whether written data will be cleared in the buffer when flush() is called.
     * Should be mapped to setAutoClear and isAutoClear in FTC's API.
     */
    var autoClear: Boolean

    /**
     * Determines whether all written data will be flushed immediately, thus cancelling out the
     * effect of calling flush() itself.
     */
    var autoUpdate: Boolean

    /**
     * Writes a message to telemetry.
     * Similar to addData()
     *
     * @param caption Caption of the message
     * @param data Data of the message
     */
    fun write(caption: String, data: String)

    /**
     * Writes an error to telemetry.
     *
     * @param info The error message
     */
    fun error(info: String)

    /**
     * Writes a warning to telemetry.
     *
     * @param info The warning message
     */
    fun warning(info: String)

    /**
     * Writes data to telemetry.
     *
     * @param label Name of data
     * @param data Data
     */
    fun data(label: String, data: Any)

    /**
     * Writes a fatal message to telemetry.
     * Ideally, the OpMode is expected to end immediately after this method is called.
     *
     * @param info Diagnostic info about the fatal error
     */
    fun fatal(info: String)

    /**
     * Flushes all added messages to the screen and clears the buffer.
     */
    fun flush()
}
