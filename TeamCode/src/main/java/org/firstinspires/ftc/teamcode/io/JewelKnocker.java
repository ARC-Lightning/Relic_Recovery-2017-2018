package org.firstinspires.ftc.teamcode.io;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.AllianceColor;
import org.firstinspires.ftc.teamcode.drivetrain.IDrivetrain;
import org.jetbrains.annotations.NotNull;

/**
 * Created by E-Man on 11/17/2017.
 */

public class JewelKnocker implements IJewelKnocker {
    private ColorSensor color;
    private Servo arm;
    private final double down = 0;
    private final double up = 1;
    private final boolean turn = true;

    public AllianceColor detect() {
        int Red = color.red();
        int Blue = color.blue();
        if (Red > Blue) return AllianceColor.RED;
        if (Blue > Red) return AllianceColor.BLUE;
        return null;

    }

    public void removeJewel(boolean towardCorner) {
        final IDrivetrain drivetrain = Hardware.INSTANCE.getDrivetrain();
        if (towardCorner) {
            drivetrain.turn(5 / 18 * Math.PI);
        } else {
            drivetrain.turn(5 / 18 * -Math.PI);
            // Removes jewel that's farther from the corner
        }

    }


    @NotNull
    @Override
    public ColorSensor getColor() {
        return color;
    }

    @NotNull
    @Override
    public Servo getArm() {
        return arm;
    }

    @Override
    public void lowerArm() {
        arm.setPosition(down);
    }

    @Override
    public void raiseArm() {
        arm.setPosition(up);

    }
}
