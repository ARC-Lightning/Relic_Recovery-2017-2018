package org.firstinspires.ftc.teamcode.music;

/**
 * Represents a singular musical note, with variable pitch, length, strength, and optional effects.
 */
public class Note {
    public int pitch;
    public double beats;
    public double strength;

    public Note(int pitch, double beats, double strength) {
        this.pitch = pitch;
        this.beats = beats;
        this.strength = strength;
    }
}
