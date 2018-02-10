package org.firstinspires.ftc.teamcode.tests;
//package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

//import static java.lang.Thread.sleep;


@TeleOp(name = "OCT", group = "WeAreLivingALie_")
//@Disabled

public class oct extends OpMode {

    DcMotor FL;
    DcMotor FR;
    DcMotor BL;
    DcMotor BR;

    @Override
    public void init() {
        FL = hardwareMap.dcMotor.get("FrontLeft");
        FR = hardwareMap.dcMotor.get("FrontRight");
        BL = hardwareMap.dcMotor.get("RearLeft");
        BR = hardwareMap.dcMotor.get("RearRight");

        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
    }

    double x, y, z, LF, RF, LB, RB;
    double slope, power;
    double temporary;

    double speed = 1.0;
    double change_speed = 0, prev = 0;

    boolean debugging;
    boolean run_launcher = false;
    boolean swap_front_back = false;

    @Override
    public void loop() {

        /*//debug motors
        debugging = false;
        if(gamepad1.y)
        {
            FL.setPower(1.0);
            debugging = true;
        }
        if(gamepad1.b)
        {
            FR.setPower(1.0);
            debugging = true;
        }
        if(gamepad1.x)
        {
            BL.setPower(1.0);
            debugging = true;
        }
        if(gamepad1.a)
        {
            BR.setPower(1.0);
            debugging = true;
        }
        if(debugging)
        {
            return;
        }*/

        x = gamepad1.left_stick_x;

        y = -gamepad1.left_stick_y;//only y is inversed

        z = gamepad1.right_stick_x;

        x = Range.clip(x, -1, 1);
        y = Range.clip(y, -1, 1);
        z = Range.clip(z, -1, 1);


        if (gamepad1.dpad_right && speed < 1) {
            change_speed = 1;
        }
        if (gamepad1.dpad_left && speed > 0) {
            change_speed = 2;
        }

        if (change_speed != prev) {
            if (change_speed == 1) {
                speed += 0.1;
            }

            if (change_speed == 2) {
                speed -= 0.1;
            }
        }

        prev = change_speed;
        change_speed = 0;


        if (speed > 1) {
            speed = 1;
        }

        if (speed < 0) {
            speed = 0;
        }
        Range.clip(speed, 0, 1);


        telemetry.addData("x", x);
        telemetry.addData("y", y);
        telemetry.addData("z", z);
        telemetry.update();

        telemetry.addData("Speed", speed);
        telemetry.update();

        x *= speed;
        y *= speed;
        z *= speed;

        telemetry.addData("x", x);
        telemetry.addData("y", y);
        telemetry.addData("z", z);
        telemetry.update();

//        x = x * x * x;
//        y = y * y * y;
//        z = z * z * z;

        power = Math.sqrt(x * x + y * y);

        if (gamepad1.dpad_down) {
            if (swap_front_back) {
                swap_front_back = false;
            } else {
                swap_front_back = true;
            }
        }

        int zone = -1;// 0 is right, 1 is up-right, 2 is up, 3 is up-left,
        // 4 is left, 5 is down-left, 6 is down, 7 is down-right

        if (y == 0) {
            // Right
            if (x > 0) zone = 0;
            // Left
            if (x < 0) zone = 4;
            // Nothing
            if (x == 0) zone = -1;
        }
        if (y > 0) {
            // Forward
            if (x == 0) zone = 2;
            // Up-Right
            if (x > 0) {
                slope = Math.sqrt(2);
                // If up
                if (x * slope < y) zone = 2;
                else {
                    slope = 1 / slope;
                    // If right
                    if (x * slope > y) zone = 0;
                    else zone = 1;
                }
            }
            // up-left
            if (x < 0) {
                slope = -Math.sqrt(2);
                // if up
                if (x * slope < y) zone = 2;
                else {
                    slope = 1 / slope;
                    // if left
                    if (x * slope > y) zone = 4;
                    else zone = 3;
                }
            }
        }
        if (y < 0) {
            // Backward
            if (x == 0) zone = 6;
            // Down-Right
            if (x > 0) {
                slope = -Math.sqrt(2);
                // If down
                if (x * slope > y) zone = 6;
                else {
                    slope = 1 / slope;
                    // If right
                    if (x * slope < y) zone = 0;
                    else zone = 7;
                }
            }
            // down-left
            if (x < 0) {
                slope = Math.sqrt(2);
                // if up
                if (x * slope > y) zone = 6;
                else {
                    slope = 1 / slope;
                    // if left
                    if (x * slope < y) zone = 4;
                    else zone = 5;
                }
            }
        }


        if (zone == 0)// right
        {
            LF = RB = power;
            LB = RF = -power;
        }
        if (zone == 4)// left
        {
            LF = RB = -power;
            LB = RF = power;
        }
        if (zone == 2)// up
        {
            LF = RB = power;
            LB = RF = power;
        }
        if (zone == 6)// down
        {
            LF = RB = -power;
            LB = RF = -power;
        }


        if (zone == 1)// up-right
        {
            LF = RB = power;
            LB = RF = 0;
        }
        if (zone == 3)// up-left
        {
            LF = RB = 0;
            LB = RF = power;
        }
        if (zone == 5)// down-left
        {
            LF = RB = -power;
            LB = RF = 0;
        }
        if (zone == 7)// down-right
        {
            LF = RB = 0;
            LB = RF = -power;
        }

        z *= -1;

        if (swap_front_back) {
            LF *= -1;
            RF *= -1;
            LB *= -1;
            RB *= -1;

            z *= -1;
        }

        LF = ((LF) + z) * 0.707;
        RF = ((RF) - z) * 0.707;
        LB = ((LB) + z) * 0.707;
        RB = ((RB) - z) * 0.707;

        LF = Range.clip(LF, -1, 1);
        RF = Range.clip(RF, -1, 1);
        LB = Range.clip(LB, -1, 1);
        RB = Range.clip(RB, -1, 1);


        //set power here
        FR.setPower(RF);
        FL.setPower(LF);
        BR.setPower(RB);
        BL.setPower(LB);


        /*if(touch.isPressed())
        {

            run_launcher = false;
            telemetry.addData(">", "run_launcher is false");
            telemetry.update();
        }

        if(gamepad2.dpad_down)
        {
            telemetry.addData(">", "run_launcher is true");
            telemetry.update();
            run_launcher = true;
        }*/


//        if(gamepad2.dpad_up){
//            launcher.setPower(1);
//        }
//        else{
//            launcher.setPower(0);
//        }


        /*Configuration nonsense
        * bl is sweeper
        * br is bl
        * fr is fl
        * fl is launcher
        *
        * elevator is br
        *
        * sweeper is probably fr
        * launcher is elevator
        *
        * */


    }
}