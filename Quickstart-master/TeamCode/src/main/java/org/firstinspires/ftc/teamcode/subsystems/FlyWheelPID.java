package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class FlyWheelPID {

    private DcMotorEx outtake;
    private Gamepad driver1, driver2;
    private double outtakeVelocity;
    private double lowVelocity;
    private double currentVelocity;
    private boolean outtakeOn = false;
    private double outtakePower = -0.90;
    private static final double POWER_STEP = 0.075;
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1.0;
    private double F = 0; //says how much power the motor should have, -1
    private double P = 0;//dampener 82.9
    double [] stepSizes = {10, 1, 0.1, 0.01, 0.001, 0.0001};
    int stepIndex = 2;

    boolean shootFromBack;

    public FlyWheelPID(OpMode mode) {
        driver1 = mode.gamepad1;
        driver2 = mode.gamepad2;
        shootFromBack = true;

        outtakeVelocity = 1350;
        lowVelocity = 900; //shoot from large launch vertex
        currentVelocity = outtakeVelocity;

        HardwareMap hardwareMap = mode.hardwareMap;
        outtake = hardwareMap.get(DcMotorEx.class, "outtake2");   //e 3
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setDirection(DcMotorSimple.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        outtake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
    }
    public void teleOp() {
        handleToggle();
        handleSpeedAdjust();
        if (outtakeOn) {
            setPower(outtakePower);
        } else {
            stop();
        }

       /*

        if (driver2.yWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (driver2.bWasPressed()) {
            outtakeVelocity += 5;
        }

        if (driver2.aWasPressed()) {
            outtakeVelocity -= 5;
        }

        if (driver2.dpadLeftWasPressed()) {
            F -= stepSizes[stepIndex];
        }
        if (driver2.dpadRightWasPressed()) {
            F += stepSizes[stepIndex];
        }
        if (driver2.dpadUpWasPressed()) {
            P += stepSizes[stepIndex];
        }
        if (driver2.dpadDownWasPressed()) {
            P -= stepSizes[stepIndex];
        }


// For tuning the PID Controller
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        outtake.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        outtake.setVelocity(currentVelocity);
*/

    }
    // Toggle outtake ON/OFF with X
    private void handleToggle() {

        if (driver2.xWasPressed()) {
            outtakeOn = !outtakeOn;
        }

    }

    // Adjust speed with bumpers

    private void handleSpeedAdjust() {

        if(driver2.rightBumperWasPressed()) {
            outtakePower += POWER_STEP;
        }

        if (driver2.leftBumperWasPressed()) {
            outtakePower -= POWER_STEP;
        }

        // Clamp power
        outtakePower = Math.max(MIN_POWER, Math.min(MAX_POWER, outtakePower));

    }
    // Motor helpers
    public void setPower(double power) {
        outtake.setPower(power);
    }

    private void stop() {
        outtake.setPower(0);
    }



    public void addTelemetry(OpMode opMode) {

        double currVelocity = outtake.getVelocity();
        double error = outtakeVelocity - currVelocity;

        opMode.telemetry.addData("Target Velocity: ", outtakeVelocity);
        opMode.telemetry.addData("Current Velocity: ", currVelocity);
        opMode.telemetry.addData("Error: ", error);
        opMode.telemetry.addLine("----------------------------------------------------");
        opMode.telemetry.addData("Tuning P: ", P);
        opMode.telemetry.addData("Tuning F: ", F);
        opMode.telemetry.addData("Step Size: ", stepSizes[stepIndex]);

        opMode.telemetry.update();
    }
}
/*
    // Motors
    private DcMotorEx leftShooter;

    private DcMotorEx rightShooter;

    // OpMode & Gamepads
    private final OpMode opMode;
    private final Gamepad driver1;

    private final Gamepad driver2;





    // Constructor
    public FlyWheelPID(OpMode opMode) {

        curTargetVelocity = 900;
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;
        driver1 = opMode.gamepad1;

        driver2 = opMode.gamepad2;

        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        // what we are using for pidfs
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");



    public void teleOp() {
        if (driver1.yWasPressed()){
            if (curTargetVelocity == outtakeVelocity) {
                curTargetVelocity = lowVelocity;
            } else { curTargetVelocity = outtakeVelocity; }


            if (driver1.bWasPressed()) {
                stepIndex = (stepIndex * 1) % stepSizes.length;
            }
            if (driver1.dpadLeftWasPressed()) {
                F -= stepSizes[stepIndex];
            }
            if (driver1.dpadRightWasPressed()) {
                F += stepSizes[stepIndex];
            }

            if (driver1.dpadUpWasPressed()) {
                P += stepSizes[stepIndex];
            }
            if (driver1.dpadDownWasPressed()) {
                P -= stepSizes[stepIndex];
            }
        }
        if (outtakeOn) {
            setPower(outtakePower);
        } else {
            stop();
        }
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        rightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        rightShooter.setVelocity(curTargetVelocity);


    }





    // Telemetry
    public void sendTelemetry(OpMode opMode) {

        double currVelocity = rightShooter.getVelocity();
        double error = outtakeVelocity - currVelocity;

        opMode.telemetry.addData("Target Velocity: ", outtakeVelocity);
        opMode.telemetry.addData("Current Velocity: ", currVelocity);
        opMode.telemetry.addData("Error: ", error);
        opMode.telemetry.addLine("----------------------------------------------------");
        opMode.telemetry.addData("Tuning P: ", P);
        opMode.telemetry.addData("Tuning F: ", F);
        opMode.telemetry.addData("Step Size: ", stepSizes[stepIndex]);

        opMode.telemetry.update();
    }
}

 */