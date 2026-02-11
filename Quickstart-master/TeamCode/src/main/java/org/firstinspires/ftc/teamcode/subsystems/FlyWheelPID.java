package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class FlyWheelPID {

    // Motors
    private DcMotorEx leftShooter;

    private DcMotorEx rightShooter;

    // OpMode & Gamepads
    private final OpMode opMode;
    private final Gamepad driver1;

    private final Gamepad driver2;

    // State
    private boolean outtakeOn = false;

    // Button edge tracking
    private boolean prevX = false;
    private boolean prevRB = false;
    private boolean prevLB = false;

    // Power control
    private double highVelocity = 1500;
    public double lowVelocity = 900;

    double curTargetVelocity = 900;

    double F = 0;

    double P = 0;

    double[] stepSizes = {10.0, 1.0 , 0.1 , 0.001, 0.0001};

    int stepIndex = 1;


    private double outtakePower = -0.90;
    private static final double POWER_STEP = 0.075;
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1.0;

    // Constructor
    public FlyWheelPID(OpMode opMode) {
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;
        driver1 = opMode.gamepad1;

        driver2 = opMode.gamepad2;

        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        // what we are using for pidfs
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");

        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.FORWARD);

        leftShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        rightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        rightShooter.setVelocity(curTargetVelocity);



        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // Call this every loop in TeleOp




    public void teleOp() {
        handleToggle();
        handleSpeedAdjust();
        if (driver1.yWasPressed()){
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            } else { curTargetVelocity = highVelocity; }


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

    // Toggle outtake ON/OFF with X
    private void handleToggle() {

        if (driver2.xWasPressed()) {
            outtakeOn = !outtakeOn;
        }

    }

    // Adjust speed with bumpers

    private void handleSpeedAdjust() {
        boolean rb = driver2.right_bumper;
        boolean lb = driver2.left_bumper;

        if (rb && !prevRB) {
            outtakePower += POWER_STEP;
        }

        if (lb && !prevLB) {
            outtakePower -= POWER_STEP;
        }

        // Clamp power
        outtakePower = Math.max(MIN_POWER, Math.min(MAX_POWER, outtakePower));

        prevRB = rb;
        prevLB = lb;

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        rightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        rightShooter.setVelocity(curTargetVelocity);

    }



    // Motor helpers
    public void setPower(double power) {
        leftShooter.setPower(power);
        rightShooter.setPower(power);
    }

    private void stop() {
        leftShooter.setPower(0);
        rightShooter.setPower(0);
    }

    // Telemetry
    public void sendTelemetry() {
        opMode.telemetry.addData("Outtake", outtakeOn ? "ON" : "OFF");
        opMode.telemetry.addData("Outtake Power", "%.2f", outtakePower);
        double curVelocity = rightShooter.getVelocity();
        double error = curTargetVelocity - curVelocity;

        telemetry.addData("Target Velocity" , curTargetVelocity);
        telemetry.addData("Current Velocity" , "%.2f" , curVelocity);
        telemetry.addData("Error" , "%.2f" , error);
        telemetry.addLine("-----------------------------------------");
        telemetry.addData("Tuning P" , "%.4f (D-Pad U/D" , P);
        telemetry.addData("Tuning F" , "%.4f (D-Pad L/R" , F);
        telemetry.addData("Step Size" , "%.4f (B Button)" , stepSizes[stepIndex]);



        opMode.telemetry.update();

    }
}