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
public class Intake {

    private DcMotor intakeMotor = null;
    private final OpMode opMode;
    private final ElapsedTime runtime = new ElapsedTime();

    private double INTAKE_POWER = 1;
    private final Gamepad driver1;
    private final Gamepad driver2;

    public Intake(OpMode opMode) {
        this.opMode = opMode;

        HardwareMap hardwareMap = opMode.hardwareMap;

        driver1 = opMode.gamepad1;
        driver2 = opMode.gamepad2;

        // Names must match the Robot Configuration
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**  tele-op that just runs the spinner continuously*/

    public void teleOp(){
        double power = 0;
        if(driver1.left_trigger > 0){
            power = INTAKE_POWER;
        }
        else if(driver1.right_trigger > 0){

            power = -INTAKE_POWER;
        }
        else{
            power = 0;
        }

        intakeMotor.setPower(power);

    }
    public void intakeAuto(int seconds,double power){
        intakeMotor.setPower(power);

        try {
            sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        intakeMotor.setPower(0);

    }
    public void intakeOn(double power)
    {

        intakeMotor.setPower(power);

    }
    public void intakeOff()
    {
        intakeMotor.setPower(0);

    }
}
