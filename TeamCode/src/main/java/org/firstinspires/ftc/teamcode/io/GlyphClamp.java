package org.firstinspires.ftc.teamcode.io;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Controls the clamp of glyphs.
 *
 * @author Michael Peng
 *         For team: 4410 (Lightning)
 *         <p>
 *         FIRST - Gracious Professionalism
 */
public class GlyphClamp {
    public Servo left;
    public Servo right;

    // Store the status on our own so we do not flood the servo with getPosition requests
    private boolean isLeftClamping;
    private boolean isRightClamping;

    public GlyphClamp(Servo left, Servo right) {
        this.left = left;
        this.right = right;
    }


}
