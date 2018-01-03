package org.firstinspires.ftc.teamcode.telemetry

/**
 * A reference implementation of ITelemetry.
 * @author Michael Peng
 */
class Telemetry(private val telem: org.firstinspires.ftc.robotcore.external.Telemetry) : ITelemetry {

    override var autoClear: Boolean
        get() = telem.isAutoClear
        set(value) {
            telem.isAutoClear = value
        }

    override var autoUpdate = false

    init {
        this.telem.addData("Hello World", "Telemetry Initialized!")
        flush()
    }

    override fun write(caption: String, data: String) {
        this.telem.addData(caption, data)
        if (autoUpdate) flush()
    }

    override fun error(info: String) {
        this.telem.addData("[ERROR]", info)
        if (autoUpdate) flush()
    }

    override fun warning(info: String) {
        this.telem.addData("[WARN]", info)
        if (autoUpdate) flush()
    }

    override fun data(label: String, data: Any) {
        this.telem.addData("DATA: " + label, data)
        if (autoUpdate) flush()
    }

    override fun fatal(info: String) {
        this.telem.addData("--- FATAL ", " ERROR ---")
        this.telem.addData("Error Info", info)
        if (autoUpdate) flush()
    }

    // FIXME("repetition") Is there a way to prevent repetition of any kind that accomplishes autoUpdate logic?
    //   Failed solutions:
    //     if (autoUpdate) flush()
    //     fun maybeUpdate() = if (autoUpdate) flush()

    override fun flush() {
        this.telem.update()
    }
}