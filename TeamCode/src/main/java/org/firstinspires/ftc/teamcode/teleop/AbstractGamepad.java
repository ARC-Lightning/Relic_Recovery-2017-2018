package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a barebone class that all TeleOp controller managers should extend.
 *
 * Created by: Michael Peng
 * For team: Lightning (4410)
 *
 * FIRST - Gracious Professionalism
 */
public abstract class AbstractGamepad {
    /*
     * # Callback-based response to gamepad events
     * If you'd like to have the robot perform something whenever a particular event occurs to the
     * gamepad, then use this feature to register your event callback with the controller manager.
     * A method that is required to run in loop() is callEventCallbacks(), which checks the
     * controller's values and invokes your callbacks as necessary.
     * Callbacks are stored as HashMap<Consumer<Gamepad>, Consumer<OpMode>>, where the key defines when the event occurs
     * and the value defines what the callback does.
     */
    private HashMap<Consumer<Gamepad>, Consumer<OpMode>> callbacks;

    public void addCallback(Consumer<Gamepad> trigger, Consumer<OpMode> call) {
        this.callbacks.put(trigger, call);
    }

    public void doCallbacks() {

    }
}
