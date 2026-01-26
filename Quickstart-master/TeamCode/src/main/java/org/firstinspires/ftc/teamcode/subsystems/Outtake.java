package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Outtake {

    // Motors
    private DcMotorEx leftShooter;
    private DcMotorEx rightShooter;

    // OpMode & Gamepads
    private final OpMode opMode;
    private final Gamepad driver2;

    // State
    private boolean outtakeOn = false;

    // Button edge tracking
    private boolean prevX = false;
    private boolean prevRB = false;
    private boolean prevLB = false;

    // Power control
    private double outtakePower = -0.75;
    private static final double POWER_STEP = 0.05;
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1.0;

    // Constructor
    public Outtake(OpMode opMode) {
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;

        driver2 = opMode.gamepad2;

        leftShooter = hardwareMap.get(DcMotorEx.class, "outtake1");
        rightShooter = hardwareMap.get(DcMotorEx.class, "outtake2");

        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.FORWARD);

        leftShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // Call this every loop in TeleOp
    public void teleOp() {
        handleToggle();
        handleSpeedAdjust();

        if (outtakeOn) {
            setPower(outtakePower);
        } else {
            stop();
        }

        sendTelemetry();
    }

    // Toggle outtake ON/OFF with X
    private void handleToggle() {
        boolean xPressed = driver2.x;

        if (xPressed && !prevX) {
            outtakeOn = !outtakeOn;
        }

        prevX = xPressed;
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
    private void sendTelemetry() {
        opMode.telemetry.addData("Outtake", outtakeOn ? "ON" : "OFF");
        opMode.telemetry.addData("Outtake Power", "%.2f", outtakePower);
        opMode.telemetry.update();
    }
}