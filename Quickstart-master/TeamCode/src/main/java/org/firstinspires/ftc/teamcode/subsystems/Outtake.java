package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Disabled
public class Outtake {

    private DcMotorEx leftShooter = null;
    private DcMotorEx rightShooter = null;

    private final OpMode opMode;
    private final ElapsedTime runtime = new ElapsedTime();

    private final double TELEOP_POWER = 0.8;

    private final double TELEOP_VELOCITY = 1500;
    private final Gamepad driver1;
    private final Gamepad driver2;


    public Outtake(OpMode opMode) {
        this.opMode = opMode;

        HardwareMap hardwareMap = opMode.hardwareMap;

        driver1 = opMode.gamepad1;
        driver2 = opMode.gamepad2;

        // Names must match the Robot Configuration
        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");

        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.FORWARD);

        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }


    public void teleOp(){
        if(driver2.right_bumper){
            startShooters(TELEOP_POWER);
        }else if(driver2.left_bumper){
            startShooters(-TELEOP_POWER);
        }else{
            stopShooter();
        }
    }

    /** Sets power to the shooters, based on power parameter*/
    public void startShooters(double power) {
        leftShooter.setPower(power);
        //rightShooter.setPower(power);
    }


    /** Stops both shooter motors */
    public void stopShooter() {
        leftShooter.setPower(0);
        //rightShooter.setPower(0);
    }


    /** Returns true if the shooter is currently spinning */
    public boolean isRunning() {
        return Math.abs(leftShooter.getPower()) > 0.01;
    }
    public void shootForTime( double power)  {
        leftShooter.setPower(power);
        rightShooter.setPower(power);

    }

    public void startShootersVelocity(double power){
        leftShooter.setVelocity(power);
        //rightShooter.setVelocity(power);
    }

    public void stopShooterVelocity() {
        leftShooter.setVelocity(0);
        //rightShooter.setVelocity(0);
    }
    public void addTelemetry(OpMode opMode){
        opMode.telemetry.addData("Shooter Velocity: ",leftShooter.getVelocity());
        opMode.telemetry.update();
    }
}
