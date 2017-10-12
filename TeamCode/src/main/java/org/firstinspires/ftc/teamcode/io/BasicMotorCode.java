package org.firstinspires.ftc.teamcode.io;

        import com.qualcomm.robotcore.eventloop.opmode.OpMode;
        import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.Servo;
        import com.qualcomm.robotcore.hardware.TouchSensor;
        import com.qualcomm.robotcore.util.Range;

//import static java.lang.Thread.sleep;


@TeleOp(name="BasicMototCode", group ="TeleOp")
//@Disabled

/**
 * Created by Joshua Krinsky :) on 10/11/2017.
 */

public class BasicMotorCode extends OpMode {
    double slowPower = .1;
    DcMotor RightSweeper;
    DcMotor LeftSweeper;

@Override
    public void init() {
        RightSweeper = hardwareMap.dcMotor.get("RightSweep");
        LeftSweeper = hardwareMap.dcMotor.get("LeftSweeper");


    }
    public void loop(){
        if (gamepad1.right_bumper){
           RightSweeper.setPower(slowPower);
        }

    }

}
