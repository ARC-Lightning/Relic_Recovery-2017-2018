package org.firstinspires.ftc.teamcode.telemetry

import java.util.*

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
     * Clears the telemetry history, thus clearing the telemetry screen on the driver station.
     */
    fun clear()
}

/**
 * A reference implementation of ITelemetry.
 * @author Michael Peng
 */
class Telemetry(private val telem: org.firstinspires.ftc.robotcore.external.Telemetry) : ITelemetry {
    private val buffer = LinkedHashMap<String, Any>()

    init {
        this.telem.addData("Hello World", "Telemetry Initialized!")
        this.telem.update()
    }

    override fun write(caption: String, data: String) {
        this.buffer.put(caption, data)
        wrapUp()
    }

    override fun error(info: String) {
        this.buffer.put("[ERROR]", info)
        wrapUp()
    }

    override fun warning(info: String) {
        this.buffer.put("[WARN]", info)
        wrapUp()
    }

    override fun data(label: String, data: Any) {
        this.buffer.put("DATA: " + label, data)
        wrapUp()
    }

    override fun fatal(info: String) {
        this.buffer.put("--- FATAL ", " ERROR ---")
        this.buffer.put("Error Info", info)
        wrapUp()
    }

    override fun clear() {
        this.buffer.clear()
        wrapUp()
    }

    private fun wrapUp() {
        for ((key, value) in this.buffer) {
            telem.addData(key, value)
        }
        telem.update()
    }
}