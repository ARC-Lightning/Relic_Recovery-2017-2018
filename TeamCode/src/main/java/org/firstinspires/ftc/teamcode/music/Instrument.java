package org.firstinspires.ftc.teamcode.music;

/**
 * Describes a musical instrument that is capable of synthesis (generating sound) of musical Notes.
 *
 * @see Note
 */

public interface Instrument {
    void initialize();
    void finalize();

    // Note cannot be null
    void play(final Note note);
    void silence();
}