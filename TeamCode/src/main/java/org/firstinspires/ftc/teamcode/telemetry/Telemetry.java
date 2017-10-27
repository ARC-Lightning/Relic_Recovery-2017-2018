package org.firstinspires.ftc.teamcode.telemetry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO Document
 */

public class Telemetry implements ITelemetry {
    private org.firstinspires.ftc.robotcore.external.Telemetry telem;
    private LinkedHashMap<String, Object> buffer = new LinkedHashMap<>();

    public Telemetry(org.firstinspires.ftc.robotcore.external.Telemetry telem) {
        this.telem = telem;
        this.telem.addData("Hello World", "Telemetry Initialized!");
        this.telem.update();
    }

    @Override
    public void write(String caption, String data) {
        this.buffer.put(caption, data);
        wrapUp();
    }

    @Override
    public void error(String info) {
        this.buffer.put("[ERROR]", info);
        wrapUp();
    }

    @Override
    public void warning(String info) {
        this.buffer.put("[WARN]", info);
        wrapUp();
    }

    @Override
    public void data(String label, Object data) {
        this.buffer.put("DATA: " + label, data);
        wrapUp();
    }

    @Override
    public void fatal(String info) {
        this.buffer.put("--- FATAL ", " ERROR ---");
        this.buffer.put("Error Info", info);
        wrapUp();
    }

    @Override
    public void clear() {
        this.buffer.clear();
        wrapUp();
    }

    private void wrapUp() {
        for (Map.Entry<String, Object> entry : this.buffer.entrySet()) {
            telem.addData(entry.getKey(), entry.getValue());
        }
        telem.update();
    }
}
