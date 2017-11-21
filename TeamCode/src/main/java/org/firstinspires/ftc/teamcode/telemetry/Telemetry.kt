package org.firstinspires.ftc.teamcode.telemetry

/**
 * A reference implementation of ITelemetry.
 * @author Michael Peng
 */
class Telemetry(private val telem: org.firstinspires.ftc.robotcore.external.Telemetry) : ITelemetry {

    init {
        this.telem.addData("Hello World", "Telemetry Initialized!")
        flush()
    }

    override fun write(caption: String, data: String) {
        this.telem.addData(caption, data)
    }

    override fun error(info: String) {
        this.telem.addData("[ERROR]", info)
    }

    override fun warning(info: String) {
        this.telem.addData("[WARN]", info)
    }

    override fun data(label: String, data: Any) {
        this.telem.addData("DATA: " + label, data)
    }

    override fun fatal(info: String) {
        this.telem.addData("--- FATAL ", " ERROR ---")
        this.telem.addData("Error Info", info)
    }

    override fun flush() {
        telem.update()
    }
}