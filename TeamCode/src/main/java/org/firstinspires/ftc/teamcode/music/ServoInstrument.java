package org.firstinspires.ftc.teamcode.music;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Defines an Instrument that uses a servo to generate noise, which can either be produced by the servo itself
 * or a mobile structure that is worthy as a percussion-based synthesizer.
 */

class ServoInstrument implements Instrument {
    private Servo servo;

    private double liftedPosition;
    private double downPosition;
    private double pressingPosition;

    public ServoInstrument(Servo servo, double liftedPosition, double downPosition, double pressingPosition) {
        this.servo = servo;
        this.liftedPosition = liftedPosition;
        this.downPosition = downPosition;
        this.pressingPosition = pressingPosition;
    }

    @Override
    public void initialize() {
        liftServo();
    }

    @Override
    public void finalize() {
        liftServo();
    }

    @Override
    public void play(Note note) {
        // TODO
    }

    @Override
    public void silence() {
        liftServo();
    }

    private void liftServo() {
        servo.setPosition(liftedPosition);
    }
}
