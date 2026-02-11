package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
public class Delivery {

    private DcMotor deliveryMotor;
    private final OpMode opMode;
    private final ElapsedTime runtime = new ElapsedTime();

    private double DELIVERY_POWER = 1;
    private final Gamepad driver1;
    private final Gamepad driver2;

    public Delivery(OpMode opMode) {
        this.opMode = opMode;

        HardwareMap hardwareMap = opMode.hardwareMap;

        driver1 = opMode.gamepad1;
        driver2 = opMode.gamepad2;

        // Names must match the Robot Configuration
        deliveryMotor = hardwareMap.get(DcMotor.class, "delivery");

        deliveryMotor.setDirection(DcMotor.Direction.FORWARD);

        deliveryMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**  tele-op that just runs the spinner continuously*/

    public void teleOp(){
        double power = 0;
        if(driver2.left_trigger > 0){
            power = DELIVERY_POWER;
        }
        else if(driver2.right_trigger > 0){

            power = -DELIVERY_POWER;
        }
        else{
            power = 0;
        }

        deliveryMotor.setPower(power);

    }
    public void deliveryAuto(int seconds,double power){
        deliveryMotor.setPower(power);

        try {
            sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        deliveryMotor.setPower(0);

    }
    public void deliveryOn(double power)
    {

        deliveryMotor.setPower(power);

    }
    public void deliveryOff()
    {
        deliveryMotor.setPower(0);

    }
}
