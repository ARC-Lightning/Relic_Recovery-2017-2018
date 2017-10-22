package org.firstinspires.ftc.teamcode.telemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Defines the ways in which the other parts of the program can access the telemetry features.
 * Ideally, no direct access to the "telemetry" field in the OpMode classes should be permitted
 *   in any other part of the program except when initializing ITelemetry.
 * Structurally similar to logging.
 */
public interface ITelemetry {
    void write(String caption, String data);
    void error(String info);
    void warning(String info);
    void data(String label, Object data);
    void fatal(String info);
}