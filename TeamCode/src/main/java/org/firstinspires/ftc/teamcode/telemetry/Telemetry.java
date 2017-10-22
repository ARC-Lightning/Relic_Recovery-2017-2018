package org.firstinspires.ftc.teamcode.telemetry;

/**
 * TODO Document
 */

public class Telemetry implements ITelemetry {
    private org.firstinspires.ftc.robotcore.external.Telemetry telem;

    public Telemetry(org.firstinspires.ftc.robotcore.external.Telemetry telem) {
        this.telem = telem;
        this.write("Hello World", "Telemetry Initialized!");
    }

    @Override
    public void write(String caption, String data) {
        telem.addData(caption, data);
        wrapUp();
    }

    @Override
    public void error(String info) {
        telem.addData("[ERROR]", info);
        wrapUp();
    }

    @Override
    public void warning(String info) {
        telem.addData("[WARN]", info);
        wrapUp();
    }

    @Override
    public void data(String label, Object data) {
        telem.addData("DATA: " + label, data);
        wrapUp();
    }

    @Override
    public void fatal(String info) {
        telem.addData("--- FATAL ", " ERROR ---");
        telem.addData("Error Info", info);
        wrapUp();
    }

    private void wrapUp() {
        telem.update();
    }
}
